# Devlog & Architecture Record: Part 2

## Module: Cross-Teaming Prevention & Nexus Interaction Logic

**Project:** CastleWars / Minigame Architecture \
**Focus:** Team validation, block interaction rules, and cross team prevention.

---

### 1. Context & Objective

The core mechanic of the game revolves around team bases collecting Nexus (Beacon Block) and placing it in their
Castle's pedestals. To ensure competitive integrity and prevent griefing, the game must strictly enforce interaction
rules:

1. Players cannot mine their own team's Nexus on their Castle's pedestals.
2. Players must only be able to place their Nexus on valid pedestal blocks in their Castle.
3. The arena itself must be protected from general block breaking/placing.
4. Dropped Nexus items must retain their team data, preventing two players from opposite teams exchanging beacons.

This document logs the architectural decisions, refactoring processes, and specific bugs encountered while building this
system.

---

### 2. Architecture & Design Decisions

#### Decision 1: Data Persistence via `PersistentDataContainer` (PDC)

To track which Beacon belongs to which team, I utilized Bukkit's `PersistentDataContainer`.

* **Initial Challenge:** Transporting NBT data between an `ItemStack` in a player's inventory and a physical `Block` (TileState)
   in the world. Initially, I assumed I would need to manually transfer this data during the
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
  actually enters the player's inventory. Because the listener executes while the game is effectively "paused," the
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

### 5. Design Decisions

#### Decision 1: Asset Transfer vs. Cross-Teaming Prevention
* **The Problem:** We need to allow players to freely transfer Nexus blocks (beacons) to teammates by dropping them. 
    However, unrestricted dropping introduces the risk of illicit cross-teaming (intentionally trading beacons with enemy players).
* **Possible Solutions:**
  - **Method 1: (Strict Mechanical Lock)** The player cannot drop beacons. This is the most secure approach, but it does not allow for freedom of transfer
    of beacons between players of the same team.
  - **Method 2: (Team-Restricted Pickup)** Allow dropping, but prevent enemies from picking it up.  
    *Result:* Creates a mechanical exploit where players can drop assets right before dying to deny the enemy a legitimate steal.
  - **Method 3: (Open Transfer) ** No restriction of dropping or picking up beacons. Whoever secures the physical item gains authority over it.

* **The Solution:** Method 3 is what we are currently implementing. We feel that the ability to transfer beacons between teammates
  is a very important feature, so although Method 1 is more robust and ideal, the gameplay experience is not as rewarding. We leave this issue to the
  moderation layer to decide how to handle such cross-teaming.