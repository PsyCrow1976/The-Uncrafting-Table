# The Uncrafting Table

Turn crafted items back into their original ingredients with a dedicated **Uncrafting Table** block.

> **Be aware:** This mod was created **100% by Grok Composer 2.5 fast** (AI-assisted development).

**Source code:** [github.com/PsyCrow1976/The-Uncrafting-Table](https://github.com/PsyCrow1976/The-Uncrafting-Table)

---

## Overview

The Uncrafting Table reverses **crafting table** recipes. Insert a crafted item, preview the matched recipe in a live 3×3 grid, and click any preview slot to recover all ingredients at once.

Custom block textures and GUI use a **reversed crafting-table** look: raised grid ridges, hollow center, outward arrows, and subtle cyan/purple accents.

---

## How it works

1. Craft or find an **Uncrafting Table** (default recipe: diamond center with 8 crafting tables — configurable).
2. Place the block and open the GUI.
3. Insert **one item** into the input slot on the left.
4. View the matched recipe ingredients in the **3×3 preview** on the right.
5. **Click any preview slot** to uncraft — the input is consumed and ingredients go to your inventory.
6. **Close the GUI** — any item still in the input slot is returned to you.

Uncraft is cancelled if your inventory cannot hold every ingredient. The input is never partially consumed.

---

## Features

- Single-item input with live recipe preview
- One-click uncraft to inventory
- Input returned on close
- Shaped and shapeless crafting recipes (tag-aware, e.g. `#minecraft:planks`)
- Configurable crafting recipe, input blocklist, and damaged-tool blocking
- Custom block textures and oak-toned GUI

---

## Requirements

| Component | Version |
|-----------|---------|
| Minecraft | 26.1.2 |
| NeoForge | 26.1.2.76 |

---

## Configuration

Edit `config/uncraftingtable-common.toml` after first launch.

| Option | Default | Description |
|--------|---------|-------------|
| `general.craftable` | `true` | Enable the crafting recipe for the table |
| `general.useCustomCraftingRecipe` | `true` | Build recipe from config |
| `general.customCraftingPattern` | `CCC / CDC / CCC` | Shaped pattern |
| `general.customCraftingKeys` | crafting table, diamond | Pattern ingredients |
| `general.blockedInputItems` | `[]` | Items blocked from input |
| `general.blockDamagedToolsAndWeapons` | `true` | Block damaged tools/weapons |

---

## Known limitations

- Only **crafting table** recipes (not smelting, stonecutting, smithing, etc.)
- When multiple recipes match, the **first** is shown
- Shapeless recipes with more than 9 ingredients show only the first 9 in preview

---

## Links

- **GitHub:** [The-Uncrafting-Table](https://github.com/PsyCrow1976/The-Uncrafting-Table)
- **Issues & feedback:** use the GitHub repository