# Requirements

## Runtime

- Minecraft **26.1.2**
- NeoForge **26.1.2.76**

## Build tools

- **JDK 25** (64-bit) — NeoForge 26.1 targets Java 25
- **Gradle** — use the included wrapper (`./gradlew`); no separate install needed
- **Git** — for cloning and version control

## Core functionality

The Uncrafting Table reverses vanilla crafting recipes into their original ingredients.

| Feature | Behaviour |
|---------|-----------|
| Input | Single slot — accepts one item at a time |
| Preview | 3x3 grid shows the crafting layout for the matched recipe |
| Arrow | Decorative arrow between input and preview (vanilla crafting table GUI) |
| Multiple recipes | `>` cycle button appears when more than one crafting recipe produces the input item |
| Uncraft | Click any preview slot — input item is consumed and all ingredients are placed in the player inventory |
| Inventory full | Uncraft is blocked if the player cannot fit all ingredients (no partial consumption) |
| Recipe scope | Shaped and shapeless **crafting table** recipes (v1); smelting/stonecutting not included |
| Crafting the block | 9 oak planks in a 3x3 grid (enabled by default, toggle in config) |

## Configuration

File: `config/uncraftingtable-common.toml`

| Option | Default | Description |
|--------|---------|-------------|
| `general.craftable` | `true` | Whether the Uncrafting Table itself can be crafted from oak planks |

More options will be added in future releases (blacklists, return rates, XP cost, etc.).

## Building the JAR

From the project root:

```bash
./gradlew build
```

Output: `build/libs/uncraftingtable-<version>.jar`

Copy that JAR into your Minecraft instance `mods/` folder alongside NeoForge 26.1.2.76.

### Future updates

1. Pull or sync the latest source
2. Bump `mod_version` in `gradle.properties` if releasing a new version
3. Update `changelog.md`
4. Run `./gradlew build`
5. Deploy the new JAR from `build/libs/`

### Dev client (optional)

```bash
./gradlew runClient
```

Launches a test client with the mod loaded. First run downloads Minecraft and NeoForge dependencies (can take a while).

### Clean rebuild

```bash
./gradlew clean build
```