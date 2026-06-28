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
| Arrow | Decorative outward-arrow motif in the custom `uncrafting_table.png` GUI texture (layout coordinates unchanged) |
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
3. Update `changelog.md` (public release notes) and `changelog-dev.md` if needed
4. Run `./gradlew build`
5. Deploy the new JAR from `build/libs/`

### CurseForge publishing

When a **new version** needs to be uploaded to CurseForge, request a fresh **API access token** from the author (do not commit or store tokens in the repo).

1. Generate a token at [CurseForge API Tokens](https://www.curseforge.com/account/api-tokens)
2. Provide the token only when an upload or project update is needed
3. **Revoke the token** after publishing is complete
4. Upload the built JAR to project **1590819** via the [CurseForge Upload API](https://support.curseforge.com/support/solutions/articles/9000197321-curseforge-upload-api):
   - Endpoint: `POST https://minecraft.curseforge.com/api/projects/1590819/upload-file`
   - Auth header: `X-Api-Token: <token>`
   - Form fields: `metadata` (JSON) + `file` (the JAR)
   - **File changelog:** use the full cumulative public history from [`branding/curseforge_changelog.md`](branding/curseforge_changelog.md) (CurseForge only shows the latest file’s changelog — do not upload just the new version’s notes)
5. Keep `branding/curseforge_changelog.md` in sync with [`changelog.md`](changelog.md) whenever a public `0.0.x` release ships
6. Update the live project description with `POST .../update-project` if listing copy changed

The author upload API token can upload files and update the project description, but **cannot list or delete** existing files — remove rejected/archived files manually in the [Authors Dashboard](https://authors.curseforge.com/#/projects/1590819/files).

### Branding & listing description

CurseForge artwork and listing text live under `branding/`:

| File | Purpose |
|------|---------|
| `curseforge_logo_400x400.png` | CurseForge project logo (min 400×400) |
| `curseforge_logo_256x256.png` | Smaller logo / README embed |
| `curseforge_screenshot_template.png` | Screenshot layout template |
| `curseforge_description.html` | **Canonical** CurseForge project page description (HTML) |
| `curseforge_description.md` | Markdown mirror for editing / copy-paste |

When updating the mod description on CurseForge:

1. Edit `branding/curseforge_description.html` (and keep `.md` in sync if desired)
2. Push the HTML via `update-project` with the author API token, or paste into **Authors → General → Description**
3. Keep the **Be aware** AI attribution, GitHub repo link, and structured sections (Overview, How it works, Features, Requirements, Configuration, Limitations, Links)

Regenerate visual assets when needed:

```bash
pip install Pillow
python3 scripts/generate_visual_assets.py
```

### Dev client (optional)

```bash
./gradlew runClient
```

Launches a test client with the mod loaded. First run downloads Minecraft and NeoForge dependencies (can take a while).

### Clean rebuild

```bash
./gradlew clean build
```