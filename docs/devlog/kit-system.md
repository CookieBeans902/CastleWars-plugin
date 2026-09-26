# Devlog & Architecture Record: Part 1

## Module: Kit System and NPCs

**Project:** CastleWars / Minigame Architecture \
**Focus:** Kit saving, Loading and NPCs

---

### 1. Context & Objective

The kit system is a crucial component of the game, as the current implementation is to offer the player a choice to select the kit before entering the arena. As a result,
the kit system must be robust and secure, while preventing players from cheating. The following rules must be enforced:

1. Players cannot switch kits after entering the arena. Before that, they can switch their kits freely.
2. Upon the kit switch, the previous kit must be removed from the player's inventory.
3. Players should not be able to drop any kit items, this is part of the Arena Protection system discussed in Part 2.

This document logs the architectural decisions, refactoring processes, and specific bugs encountered while building this
system.

---

### 2. Architecture & Design Decisions

#### Decision 1: Storing each kit as a custom class object which contains the different data constituting a kit.

Defined a custom class called Kit, in the model package. This class contains the following fields:
```java
public class Kit {
    String displayName;
    ItemStack[] storageItems;
    ItemStack[] armorItems;
    ItemStack offHandItem;
    ItemStack mainHandItem;
    }
```

* **Initial Challenge:** Transporting NBT data between an `ItemStack` in a player's inventory and a physical `Block` (
  TileState) in the world. Initially, I assumed I would need to manually transfer this data during the
  `BlockPlaceEvent`.
* **The Solution:** Used `BlockStateMeta`. By casting the item's meta to `BlockStateMeta`, which is possible as a beacon
  is a block, extracting the `TileState`, setting the PDC key there, and saving it back to the item, vanilla Minecraft
  natively handles the data injection when the block is placed.
* *Result:* Eliminated the need for complex, manual block-placement data transfer code. Created an `ItemCreator` factory
  class to cleanly encapsulate this logic.

#### Decision 2: Decoupling Arena Protection from Game Logic (The "Bouncer" Pattern)

* **Initial Approach:** Hardcoded checks for `Material.BEACON` inside a generic `BlockBreakEvent` alongside the team
  validation logic.
* **The Problem:** Tight coupling. The listener was acting as both the "Arena Guard" (preventing map destruction) and
  the "Game Master" (handling beacon mechanics). If I wanted to allow players to break wool later, the logic would
  require high refactoring.
* **The Refactor:** Split the logic into two highly cohesive listeners:
    1. `ArenaProtectionListener` (Priority: LOW): Acts as a bouncer holding a "guest list" of allowed materials (passed
       via constructor: `Set<Material> allowedMaterials`). It blindly cancels any interaction not on the list.
    2. `NexusListener` (Priority: NORMAL, `ignoreCancelled = true`): Handles the actual team logic, knowing that if the
       event reaches it, the block is legally allowed to be broken.

---

### 3. Critical Bugs & Resolutions

#### Bug 1: The "Ghost Item" & `getActiveItem()` Misunderstanding

* **Symptom:** Successfully generated Nexus items were not showing up in the player's hotbar, and data checks returned
  `null` when trying to read the player's held item.
* **Root Cause:** 1. The `addItem()` method was silently filling up the inventory without visual feedback (no sound).
    2. Used `player.getActiveItem()` to check the PDC. In the Bukkit API, "Active Item" refers exclusively to items
       currently being consumed or animated (like drawing a bow or eating steak), NOT the item passively held in the
       hand.
* **Fix:** Switched to `player.getInventory().getItemInMainHand()` to properly read the data, and added
  `player.playSound()` for UX feedback.

#### Bug 2: Adventure Component API Bleed

* **Symptom:** A warning message ("You cannot mine your own block!") was rendering entirely in **BOLD** red text, even
  though only the `[WARNING]` prefix was assigned the bold decoration.
* **Root Cause:** The Adventure API uses component inheritance. Appended child components automatically inherit the
  styling (decorations like BOLD, ITALICS) of their parent components unless explicitly overridden.
* **Fix:** Used an empty base component `Component.text()` and appended the `[WARNING]` and the message text as sibling
  components, preventing style bleed.

#### Bug 3: The 1-Tick Event State Desync (The "Glowing" Bug)

* **Symptom:** When a player picked up a dropped Nexus, a utility method `EffectProvider.assignGlowing(player)` was
  called to check their inventory and apply a glow effect. The method consistently failed to find the Beacon and removed
  the effect instead.
* **Root Cause:** Bukkit events act as *interrupts*, not callbacks. The `EntityPickupItemEvent` fires *before* the item
  actually enters the player's inventory. Because the listener executes while the game is effectively "paused", the
  `EffectProvider` checked the inventory a microsecond too early.
* **Fix:** Scheduled the visual update for the next server tick using `Bukkit.getScheduler().runTask()`. This acts as
  a "to-do list" note for the server, ensuring the inventory physics finish processing before the effect logic checks
  the player's state.

#### Bug 4: Accidental Drop Deletion on Admin Bypass

* **Symptom:** When a player with the `castlewars.debug.bypass` permission broke a Beacon, the block would disappear,
  but no item would drop.
* **Root Cause:** In the `BlockBreakEvent`, the line `event.setDropItems(false);` was placed *above* the debug
  permission `return;` check. Admins bypassed the custom drop logic, but vanilla drops were already disabled.
* **Fix:** Reordered the logic to perform the permission check before altering the event's native state.

---

### 4. Technical Takeaways

1. **Event Priority is a Pipeline:** Using `LOW` for security checks and `NORMAL` with `ignoreCancelled` for mechanic
   logic is a robust way to chain events safely.
2. **State Verification Requires Patience:** Never assume the world state matches the event's *implication* while the
   event is still firing. Defer post-action state checks to the next tick.
3. **Manageritis Mitigation:** Grouping listeners by *Mechanic* (e.g., `NexusListener`) rather than by *Event* (e.g.,
   `BlockBreakListener`) dramatically improves code cohesion and readability.
