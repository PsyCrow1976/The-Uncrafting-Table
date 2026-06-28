# Changelog

Public release notes for **The Uncrafting Table**. Patch-level build history (`0.0.0.x`, `0.0.1.x`, `0.0.2.x`) is in [changelog-dev.md](changelog-dev.md).

---

## 0.0.2 — Anti-duplication recipe filtering

**Minecraft 26.1.2** · **NeoForge 26.1.2.76**

### Fixed

- Tightened which crafting recipes can be reversed to prevent item duplication — only crafts that produce a **single output item** are eligible (e.g. coal block → 9 coal), while bulk-output and ingredient-only matches are rejected (e.g. one coal can no longer become a coal block)

## 0.0.1 — First public release

**Minecraft 26.1.2** · **NeoForge 26.1.2.76**

Turn crafted items back into their original ingredients with a dedicated Uncrafting Table block.

### Features

- **Uncrafting Table block** — placeable block in the Functional Blocks creative tab; craftable via a configurable shaped recipe (default: oak planks, obsidian, and diamond)
- **Single-item input** — insert one item on the left; the mod looks up a matching crafting-table recipe
- **Live 3×3 preview** — ingredients appear in the preview grid on the right, laid out like the original recipe
- **One-click uncraft** — click any preview slot to consume the input and receive all ingredients in your inventory
- **Safe uncrafting** — uncraft is cancelled if your inventory cannot hold every ingredient; the input is never partially consumed
- **Input returned on close** — closing the GUI returns the input item to your inventory (or drops it at your feet if full)
- **Custom visuals** — reversed crafting-table block textures (raised 3×3 grid, hollow center, outward arrows) and a matching oak-toned GUI with subtle cyan/purple accents
- **Tag-aware recipes** — shaped and shapeless crafting recipes are resolved the same way as the crafting book, including tag ingredients (e.g. `#minecraft:planks`)

### Configuration

Edit `config/uncraftingtable-common.toml` after first launch:

| Option | Default | Description |
|--------|---------|-------------|
| `general.craftable` | `true` | Enable the crafting recipe for the Uncrafting Table |
| `general.useCustomCraftingRecipe` | `true` | Build the table recipe from config instead of the datapack default |
| `general.customCraftingPattern` | `POP` / `ODO` / `POP` | Shaped pattern for the table recipe |
| `general.customCraftingKeys` | planks, obsidian, diamond | Ingredient mapping for pattern letters |
| `general.blockedInputItems` | `[]` | Item IDs that cannot be placed in the input slot |
| `general.blockDamagedToolsAndWeapons` | `true` | Block tools and weapons that have lost any durability |
| `[Debugging] debug` | `true` | Log recipe lookup and preview sync to `latest.log` |
| `[Debugging] testModeOnlyBookshelf` | `false` | Restrict input to bookshelves (troubleshooting aid) |

### Requirements

| Component | Version |
|-----------|---------|
| Minecraft | 26.1.2 |
| NeoForge | 26.1.2.76 |

### Known limitations

- Only **crafting table** recipes are reversed (not smelting, stonecutting, smithing, etc.)
- When multiple recipes produce the same item, the **first match** is shown (recipe cycling is planned)
- Shapeless recipes with more than 9 ingredients show only the first 9 in the preview grid
- Mod-specific crafting recipes that need special grid handling are skipped for stability