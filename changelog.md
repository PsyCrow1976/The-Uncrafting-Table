# Changelog

All notable changes to The Uncrafting Table are documented here.

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