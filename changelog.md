# Changelog

All notable changes to The Uncrafting Table are documented here.

## 0.0.0.22 — Custom block & GUI visuals

### Added

- Custom block textures (raised 3×3 grid top with hollow center and outward arrows, wrapped side grid, inverted front motif)
- Subtle cyan/purple accent colors on grid lines and arrow details
- CurseForge branding assets (`branding/curseforge_logo_400x400.png`, `curseforge_logo_256x256.png`, `curseforge_screenshot_template.png`)
- `scripts/generate_visual_assets.py` — reproducible asset generator (requires `pip install Pillow`; run `python3 scripts/generate_visual_assets.py`)

### Changed

- Block and item models now reference mod textures instead of vanilla crafting table
- GUI reskinned with oak wood tones and uncrafting accent colors (slot layout unchanged)

### Documentation

- README updated with embedded previews of the mod logo, block textures, and GUI art
- README visual section links to `branding/` assets for CurseForge listing artwork

## 0.0.0.21 — Config layout

### Changed

- `debug` and `testModeOnlyBookshelf` moved to a `[Debugging]` config section
- `general.blockedInputItems` defaults to an empty list (no blocked items by default)

## 0.0.0.20 — Default crafting recipe update

### Changed

- Default crafting recipe is now oak planks in corners, obsidian on sides, diamond in the center
- `general.customCraftingKeys` replaces `general.customCraftingIngredient` for multi-ingredient patterns

## 0.0.0.19 — Custom crafting recipe config

### Added

- `general.useCustomCraftingRecipe` — build the crafting recipe from config instead of the datapack default
- `general.customCraftingPattern` — shaped pattern rows (default 3×3 `PPP`)
- `general.customCraftingIngredient` — ingredient for pattern cells (default `minecraft:oak_planks`, supports `#` tags)

### Changed

- `general.craftable` now defaults to `true`

## 0.0.0.18 — Block damaged tools and weapons

### Added

- `general.blockDamagedToolsAndWeapons` (default `true`) — rejects pickaxes, axes, swords, bows, shields, and other tools/weapons that have lost any durability

## 0.0.0.17 — Config defaults and input blocklist

### Added

- `general.blockedInputItems` — list of item IDs that cannot be placed in the input slot (default: `minecraft:oak_sapling`)

### Changed

- `general.craftable` now defaults to `false` (9-oak-plank crafting recipe disabled until enabled in config)

## 0.0.0.16 — All items enabled

### Changed

- Bookshelf-only test mode disabled by default (`general.testModeOnlyBookshelf = false`) — all craftable items can be placed in the input slot again

## 0.0.0.15 — Client preview sync fix

### Fixed

- Preview grid now appears in the GUI — `setItem` previously ignored preview slots 1–9, so client sync packets from the server were dropped
- Menu sync uses `broadcastChanges` again so slot updates are sent to the client ( `broadcastFullState` does not sync item stacks)

## 0.0.0.14 — Debug logging and bookshelf test mode

### Added

- `general.debug` config option (default `true`) — writes recipe lookup and preview sync details to `latest.log`
- `general.testModeOnlyBookshelf` config option (default `true`) — input slot accepts only `minecraft:bookshelf` while troubleshooting other mods

### Changed

- Rejected items are blocked in the input slot and logged when debug is enabled

## 0.0.0.13 — Recipe preview resolution fix

### Fixed

- Recipe lookup now uses crafting recipe displays (same system as JEI/crafting book) to match outputs and resolve tag ingredients like `#minecraft:planks`
- Shaped recipes render in the 3x3 preview using correct pattern width and row layout
- Input slot always stores a copy of the inserted item, avoiding shared stack references
- Open menus receive a full container sync after preview updates

## 0.0.0.12 — Preview grid null fix

### Fixed

- Shaped recipe preview no longer crashes when empty grid cells are left unset (e.g. bookshelf, 2×2 plank recipes)
- Preview slot update treats null stacks as empty

## 0.0.0.11 — Recipe resolver hardening

### Fixed

- Recipe lookup restricted to vanilla-shaped and shapeless crafting recipes only (excludes mod recipes like DankStorage `UpgradeRecipe`)
- Each recipe is processed in isolation — a bad recipe no longer aborts the entire lookup
- Ingredient index bounds checks prevent preview grid crashes on malformed placement data
- Block entity recipe refresh is wrapped in try/catch so placing items in the input slot never crashes the GUI

## 0.0.0.10 — Recipe resolver crash fix

### Fixed

- Recipe lookup no longer crashes on mod crafting recipes (e.g. DankStorage `UpgradeRecipe`) that require a full crafting grid input
- Only standard shaped/shapeless crafting table recipes (`NormalCraftingRecipe`) are considered for uncrafting

## 0.0.0.9 — Recipe preview sync

### Fixed

- Preview grid now fills with recipe ingredients when an item is placed in the input slot
- Preview grid clears when the input item is removed
- Clicking any preview slot uncrafts correctly (ingredients to inventory, input consumed)
- Recipe lookup uses the crafting recipe map; open menus are synced immediately after preview updates

## 0.0.0.8 — Close GUI returns input

### Fixed

- Input item is returned to the player inventory when the GUI is closed (dropped at the player's feet if inventory is full)
- Block entity is cleared on close so the table can be reopened normally
- Client menu now uses the block entity's synced `ContainerData` instead of a local dummy copy

## 0.0.0.7 — Uncrafting

### Added

- Recipe lookup when an item is placed in the input slot
- 3x3 preview grid shows crafting ingredients for the matched recipe
- Click any preview slot to uncraft — input is consumed and all ingredients go to the player inventory
- Uncraft is blocked if the player inventory cannot fit all output items

## 0.0.0.6 — Visual 3x3 preview grid

### Added

- Custom Iron-Furnaces-style GUI texture with slot backgrounds baked in (`uncrafting_table.png`)
- Empty 3x3 display grid on the right side of the GUI (visual only, no interaction)
- GUI atlas registration for mod-owned textures

### Changed

- GUI rebuilt using Iron Furnaces pattern: mod-owned texture with slot backgrounds baked in, menu slots aligned to texture coordinates
- GUI no longer reuses vanilla `crafting_table.png` (which misaligned the left-side grid)
- Slot art composited from vanilla crafting table texture for correct inset appearance; arrow baked into texture (MC 26.1 has no crafting-table arrow sprite)
- Input slot on the left at `(30, 35)`; player inventory at `(8, 84)`

## 0.0.0.5 — Simple single-slot GUI

### Changed

- GUI reduced to one input slot (centre top) plus player inventory
- Removed 3x3 preview grid, arrow, and cycle button
- Uses vanilla hopper container background (no baked-in crafting grid)

## 0.0.0.4 — Item texture fix

### Fixed

- Inventory, JEI, hand-held, and recipe icon now show the vanilla crafting table texture (MC 26.1 uses `assets/<mod>/items/` instead of `models/item/`)

## 0.0.0.3 — Placeholder art and GUI shell

### Changed

- Block and item now use vanilla crafting table textures (JEI, inventory, and placed block)
- GUI layout mirrored: single input slot on the left, arrow pointing right, 3x3 preview area on the right
- Recipe lookup, preview fill, cycle button, and uncraft actions disabled for step-by-step development

## 0.0.0.2 — Crash fix

### Fixed

- Game no longer crashes on startup with `Block id not set` / `Trying to access unbound value` — block registration now uses `registerBlock()` so Minecraft 26.1 assigns the block ID correctly

## 0.0.0.1 — Initial release

First public release of The Uncrafting Table for Minecraft 26.1.2 / NeoForge 26.1.2.76.

### Added

- **Uncrafting Table block** — placeable block with a crafting-table-style GUI (vanilla textures for now)
- **Single-item input** — insert one craftable item to look up its reverse recipe
- **3x3 recipe preview** — shows the ingredient layout of the matched crafting recipe, with a decorative arrow pointing from input to preview
- **Recipe cycling** — when an item has multiple crafting recipes (e.g. different plank arrangements), a `>` button lets the player scroll through them
- **One-click uncraft** — clicking any item in the preview grid consumes the input and returns all crafting ingredients to the player inventory at once
- **Reverse crafting engine** — resolves shaped and shapeless crafting table recipes from the recipe manager
- **Config file** — `uncraftingtable-common.toml` with `general.craftable` to enable or disable the 9-oak-plank crafting recipe
- **Crafting recipe** — 9 oak planks in a 3x3 grid crafts one Uncrafting Table (when `craftable` is true)

### Known limitations (v1)

- Uses vanilla crafting table GUI textures (custom art planned)
- Only crafting table recipes are reversed (not smelting, stonecutting, etc.)
- Shapeless recipes with more than 9 ingredients show only the first 9 in preview
- Uncraft is cancelled if the player inventory cannot hold all output items