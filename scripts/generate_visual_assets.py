#!/usr/bin/env python3
"""Generate Uncrafting Table block textures, GUI reskin, and CurseForge branding."""

from __future__ import annotations

import hashlib
import sys
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
REF = ROOT / "scripts/refs"
GUI_BASELINE = REF / "uncrafting_table_base.png"
ASSETS = ROOT / "src/main/resources/assets/uncraftingtable"
BRANDING = ROOT / "branding"

# Palette — vanilla crafting-table oak with subtle uncrafting accents
WOOD_LIGHT = (188, 152, 98, 255)
WOOD_MED = (174, 105, 60, 255)
WOOD_RIM = (158, 89, 50, 255)
WOOD_DARK = (115, 57, 32, 255)
WOOD_DEEP = (85, 56, 36, 255)
WOOD_SHADOW = (75, 43, 24, 255)
BORDER = (25, 20, 12, 255)
BORDER_DEEP = (14, 11, 6, 255)
VOID = (14, 11, 6, 255)
HIGHLIGHT = (194, 157, 98, 255)
SIDE_LIGHT = (184, 148, 95, 255)
SIDE_MED = (175, 143, 85, 255)
SIDE_DARK = (159, 132, 77, 255)
ACCENT_CYAN = (72, 168, 175, 255)
ACCENT_PURPLE = (118, 82, 148, 255)
TOOL_DARK = (56, 33, 22, 255)
TOOL_MID = (103, 80, 44, 255)


def save_rgba(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    img.convert("RGBA").save(path)
    print(f"Wrote {path}")


def set_px(img: Image.Image, x: int, y: int, color: tuple[int, int, int, int]) -> None:
    if 0 <= x < img.width and 0 <= y < img.height:
        img.putpixel((x, y), color)


def blend_accent(base: tuple[int, int, int, int], accent: tuple[int, int, int, int], t: float = 0.35):
    return tuple(int(base[i] * (1 - t) + accent[i] * t) for i in range(3)) + (255,)


def draw_raised_line_h(img: Image.Image, y: int, x0: int, x1: int, accent: bool = False) -> None:
    hi = blend_accent(HIGHLIGHT, ACCENT_CYAN) if accent else HIGHLIGHT
    sh = blend_accent(WOOD_DEEP, ACCENT_PURPLE) if accent else WOOD_DEEP
    for x in range(x0, x1 + 1):
        set_px(img, x, y, hi)
        if y + 1 < img.height:
            set_px(img, x, y + 1, sh)


def draw_raised_line_v(img: Image.Image, x: int, y0: int, y1: int, accent: bool = False) -> None:
    hi = blend_accent(HIGHLIGHT, ACCENT_CYAN) if accent else HIGHLIGHT
    sh = blend_accent(WOOD_DEEP, ACCENT_PURPLE) if accent else WOOD_DEEP
    for y in range(y0, y1 + 1):
        set_px(img, x, y, hi)
        if x + 1 < img.width:
            set_px(img, x + 1, y, sh)


def fill_rect(img: Image.Image, x0: int, y0: int, x1: int, y1: int, color) -> None:
    px = img.load()
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            px[x, y] = color


def generate_top() -> Image.Image:
    """Raised 3x3 grid, hollow center, outward arrows — inverse of vanilla recessed top."""
    img = Image.new("RGBA", (16, 16), BORDER_DEEP)

    # Outer rim (matches vanilla crafting-table frame)
    fill_rect(img, 1, 1, 14, 14, WOOD_MED)
    fill_rect(img, 2, 2, 13, 13, WOOD_LIGHT)
    for x in range(1, 15):
        set_px(img, x, 1, BORDER)
        set_px(img, x, 14, BORDER)
    for y in range(1, 15):
        set_px(img, 1, y, BORDER)
        set_px(img, 14, y, BORDER)

    # Corner posts
    for cx, cy in ((2, 2), (13, 2), (2, 13), (13, 13)):
        set_px(img, cx, cy, WOOD_RIM)

    # Cell interiors — darker recess between raised ridges
    cells = [
        (3, 3, 5, 5),
        (6, 3, 8, 5),
        (9, 3, 11, 5),
        (3, 6, 5, 8),
        (9, 6, 11, 8),
        (3, 9, 5, 11),
        (6, 9, 8, 11),
        (9, 9, 11, 11),
    ]
    for x0, y0, x1, y1 in cells:
        fill_rect(img, x0, y0, x1, y1, WOOD_DARK)

    # Hollow center void
    fill_rect(img, 6, 6, 8, 8, VOID)

    # Raised grid ridges with accent tint
    for x in (5, 8):
        draw_raised_line_v(img, x, 3, 11, accent=True)
    for y in (5, 8):
        draw_raised_line_h(img, y, 3, 11, accent=True)

    # Outward arrows from hollow center
    cx, cy = 7, 7
    arrow_cyan = blend_accent(ACCENT_CYAN, WOOD_LIGHT, 0.15)
    arrow_purple = blend_accent(ACCENT_PURPLE, WOOD_DARK, 0.2)

    # North
    for dx, dy, c in [(0, 4, arrow_cyan), (-1, 5, arrow_purple), (0, 5, arrow_cyan), (1, 5, arrow_purple)]:
        set_px(img, cx + dx, cy + dy, c)
    # South
    for dx, dy, c in [(0, -4, arrow_cyan), (-1, -5, arrow_purple), (0, -5, arrow_cyan), (1, -5, arrow_purple)]:
        set_px(img, cx + dx, cy + dy, c)
    # East
    for dx, dy, c in [(-4, 0, arrow_cyan), (-5, -1, arrow_purple), (-5, 0, arrow_cyan), (-5, 1, arrow_purple)]:
        set_px(img, cx + dx, cy + dy, c)
    # West
    for dx, dy, c in [(4, 0, arrow_cyan), (5, -1, arrow_purple), (5, 0, arrow_cyan), (5, 1, arrow_purple)]:
        set_px(img, cx + dx, cy + dy, c)

    # Re-assert void center
    set_px(img, 7, 7, VOID)

    return img


def generate_side() -> Image.Image:
    """Side face with top-edge grid wrapping from the raised top surface."""
    base = Image.open(REF / "crafting_table_side.png").convert("RGBA")
    img = base.copy()
    px = img.load()

    # Top band: continue raised grid motif from the block top
    grid_cols = [4, 7, 10]
    for x in grid_cols:
        set_px(img, x, 0, blend_accent(HIGHLIGHT, ACCENT_CYAN))
        set_px(img, x, 1, blend_accent(WOOD_DEEP, ACCENT_PURPLE))
        set_px(img, x, 2, WOOD_DARK)
    for x in range(3, 12):
        if x not in grid_cols:
            set_px(img, x, 0, WOOD_MED)
            set_px(img, x, 1, WOOD_DARK)
            set_px(img, x, 2, WOOD_SHADOW)

    # Horizontal ridge at y=3
    draw_raised_line_h(img, 3, 3, 12, accent=True)

    # Subtle accent pixels on existing side grid hints
    accent_points = [(7, 7), (8, 8), (7, 9), (4, 7), (10, 8)]
    for x, y in accent_points:
        r, g, b, a = px[x, y]
        if r < 100:
            px[x, y] = blend_accent((r, g, b, a), ACCENT_PURPLE, 0.4)
        else:
            px[x, y] = blend_accent((r, g, b, a), ACCENT_CYAN, 0.25)

    return img


def generate_front() -> Image.Image:
    """Inverted vise motif — diverging arrows / outward burst instead of clamp."""
    base = Image.open(REF / "crafting_table_front.png").convert("RGBA")
    img = base.copy()
    px = img.load()

    # Clear vise / tool area (rows 5-11, cols 4-11)
    for y in range(5, 12):
        for x in range(4, 12):
            if y in (5, 11) or x in (4, 11):
                px[x, y] = TOOL_MID
            elif (x, y) in ((7, 7), (8, 7), (7, 8), (8, 8)):
                px[x, y] = VOID
            else:
                px[x, y] = TOOL_DARK

    # Inverted motif: jaws open outward (reverse of converging vise)
    # Left jaw — opens left
    for y in range(6, 11):
        px[5, y] = SIDE_DARK
        px[6, y] = blend_accent(SIDE_LIGHT, ACCENT_CYAN, 0.3)
    # Right jaw — opens right
    for y in range(6, 11):
        px[10, y] = SIDE_DARK
        px[9, y] = blend_accent(SIDE_LIGHT, ACCENT_PURPLE, 0.3)

    # Outward arrow chevrons on each side of void
    chevrons = [
        (5, 7, ACCENT_CYAN),
        (4, 8, ACCENT_PURPLE),
        (10, 7, ACCENT_PURPLE),
        (11, 8, ACCENT_CYAN),
        (7, 5, ACCENT_CYAN),
        (8, 4, ACCENT_PURPLE),
        (7, 10, ACCENT_PURPLE),
        (8, 11, ACCENT_CYAN),
    ]
    for x, y, accent in chevrons:
        set_px(img, x, y, accent)

    # Horizontal accent bar (uncrafting "release" bar vs vise screw)
    for x in range(6, 10):
        set_px(img, x, 9, blend_accent(TOOL_MID, ACCENT_CYAN, 0.35))

    return img


def recolor_toward_oak(color: tuple[int, int, int, int]) -> tuple[int, int, int, int]:
    r, g, b, a = color
    if a == 0 or (r, g, b) == (0, 0, 0):
        return color
    # Slot neutrals — keep vanilla-compatible
    if 120 <= r <= 145 and g == b == r:
        return color
    if r == 198 and g == 198 and b == 198:
        return color
    if r > 220 and g > 220 and b > 220:
        return (210, 180, 130, a)
    if r > 170 and g > 150:
        return (
            min(255, int(r * 0.85 + 40)),
            min(255, int(g * 0.75 + 30)),
            min(255, int(b * 0.55 + 15)),
            a,
        )
    if r > 100 and g > 80:
        return (
            min(255, int(r * 0.9 + 20)),
            min(255, int(g * 0.8 + 15)),
            min(255, int(b * 0.6 + 10)),
            a,
        )
    return color


def add_gui_accents(img: Image.Image) -> None:
    px = img.load()

    # Preview 3x3 grid lines (88,17 — 18px spacing)
    for col in range(3):
        for row in range(3):
            sx = 88 + col * 18
            sy = 17 + row * 18
            # Top edge highlight
            for dx in range(18):
                c = px[sx + dx, sy]
                if c[0] >= 120:
                    px[sx + dx, sy] = blend_accent(c, ACCENT_CYAN, 0.2)
            # Left edge highlight
            for dy in range(18):
                c = px[sx, sy + dy]
                if c[0] >= 120:
                    px[sx, sy + dy] = blend_accent(c, ACCENT_PURPLE, 0.15)

    # Input slot frame accents
    for dx in range(18):
        for edge_y in (35, 52):
            c = px[30 + dx, edge_y]
            if c[0] >= 100:
                px[30 + dx, edge_y] = blend_accent(c, ACCENT_CYAN, 0.18)
    for dy in range(18):
        for edge_x in (30, 47):
            c = px[edge_x, 35 + dy]
            if c[0] >= 100:
                px[edge_x, 35 + dy] = blend_accent(c, ACCENT_PURPLE, 0.18)

    # Decorative reversed-arrow motif above input (vanilla crafting-grid area)
    deco_y = 20
    for cx in (35, 39, 43):
        # Outward arrows: tip + wings
        set_px(img, cx, deco_y, ACCENT_CYAN)
        set_px(img, cx - 1, deco_y + 1, blend_accent(WOOD_DARK, ACCENT_PURPLE, 0.4))
        set_px(img, cx + 1, deco_y + 1, blend_accent(WOOD_DARK, ACCENT_PURPLE, 0.4))
        set_px(img, cx, deco_y + 2, WOOD_DEEP)

    # Small raised-grid decoration left of preview
    for x in range(74, 84):
        if (x - 74) % 3 == 0:
            px[x, 26] = blend_accent(HIGHLIGHT, ACCENT_CYAN, 0.3)
            px[x, 27] = blend_accent(WOOD_DEEP, ACCENT_PURPLE, 0.3)


def require_ref(path: Path) -> None:
    if not path.is_file():
        raise FileNotFoundError(
            f"Missing reference asset: {path}\n"
            "Vendored refs must live under scripts/refs/ (see README)."
        )


def generate_gui() -> Image.Image:
    """Reskin GUI from frozen baseline: oak frame, accent grid lines; preserve slot layout."""
    require_ref(GUI_BASELINE)
    base = Image.open(GUI_BASELINE).convert("RGBA")

    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    # Copy active 176x166 region
    region = base.crop((0, 0, 176, 166))
    recolored = Image.new("RGBA", (176, 166))
    src_px = region.load()
    dst_px = recolored.load()
    for y in range(166):
        for x in range(176):
            dst_px[x, y] = recolor_toward_oak(src_px[x, y])
    img.paste(recolored, (0, 0))
    add_gui_accents(img)
    return img


def draw_block_icon(draw: ImageDraw.ImageDraw, ox: int, oy: int, scale: int) -> None:
    """Isometric-ish uncrafting table icon for branding."""

    def rect(x, y, w, h, fill):
        draw.rectangle([ox + x * scale, oy + y * scale, ox + (x + w) * scale - 1, oy + (y + h) * scale - 1], fill=fill)

    # Shadow
    rect(2, 14, 12, 2, (30, 25, 20, 180))
    # Sides
    rect(1, 6, 14, 8, (159, 132, 77, 255))
    rect(1, 6, 2, 8, (115, 57, 32, 255))
    rect(13, 6, 2, 8, (85, 56, 36, 255))
    # Top
    rect(0, 0, 16, 7, (174, 105, 60, 255))
    rect(1, 1, 14, 5, (188, 152, 98, 255))

    # Raised grid on top
    for gx in (4, 8, 12):
        draw.line(
            [ox + gx * scale, oy + 1 * scale, ox + gx * scale, oy + 5 * scale],
            fill=ACCENT_CYAN,
            width=max(1, scale // 8),
        )
    for gy in (2, 4):
        draw.line(
            [ox + 2 * scale, oy + gy * scale, ox + 14 * scale, oy + gy * scale],
            fill=ACCENT_PURPLE,
            width=max(1, scale // 8),
        )

    # Hollow center + arrows
    cx, cy = ox + 8 * scale, oy + 3 * scale
    r = max(2, scale // 4)
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=VOID)
    for dx, dy in [(0, -2), (0, 2), (-2, 0), (2, 0)]:
        draw.polygon(
            [
                (cx + dx * scale, cy + dy * scale),
                (cx + (dx - 1) * scale // 2, cy + (dy * 0.6) * scale),
                (cx + (dx + 1) * scale // 2, cy + (dy * 0.6) * scale),
            ],
            fill=ACCENT_CYAN if dx != 0 else ACCENT_PURPLE,
        )


def generate_branding_logo(size: int) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Oak/cyan/purple gradient background
    for y in range(size):
        t = y / max(size - 1, 1)
        r = int(45 + 30 * t)
        g = int(35 + 20 * t)
        b = int(55 + 40 * (1 - t))
        draw.line([(0, y), (size, y)], fill=(r, g, b, 255))

    # Accent glow rings
    cx = cy = size // 2
    for radius, color, alpha in [
        (size * 0.42, ACCENT_CYAN, 40),
        (size * 0.34, ACCENT_PURPLE, 50),
    ]:
        r = int(radius)
        glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        gdraw = ImageDraw.Draw(glow)
        gdraw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=color[:3] + (alpha,))
        img = Image.alpha_composite(img, glow)
        draw = ImageDraw.Draw(img)

    scale = max(size // 16, 12)
    block_w = 16 * scale
    ox = (size - block_w) // 2
    oy = (size - block_w) // 2 - size // 20
    draw_block_icon(draw, ox, oy, scale)

    # Title strip
    strip_h = max(size // 10, 20)
    draw.rectangle([0, size - strip_h, size, size], fill=(25, 20, 12, 220))
    draw.rectangle([0, size - strip_h, size, size - strip_h + 2], fill=ACCENT_CYAN)

    return img


def generate_screenshot_template() -> Image.Image:
    w, h = 1280, 720
    img = Image.new("RGBA", (w, h), (35, 28, 45, 255))
    draw = ImageDraw.Draw(img)

    for y in range(h):
        t = y / h
        c = (
            int(35 + 15 * t),
            int(28 + 10 * t),
            int(45 + 20 * (1 - t)),
        )
        draw.line([(0, y), (w, y)], fill=c + (255,))

    # Center showcase panel
    margin = 80
    draw.rounded_rectangle(
        [margin, margin, w - margin, h - margin],
        radius=24,
        fill=(55, 45, 65, 255),
        outline=ACCENT_CYAN,
        width=3,
    )

    scale = 28
    ox = w // 2 - 8 * scale
    oy = h // 2 - 10 * scale
    draw_block_icon(draw, ox, oy, scale)

    draw.rectangle([margin, h - margin - 60, w - margin, h - margin - 56], fill=ACCENT_PURPLE)
    return img


def image_md5(img: Image.Image) -> str:
    return hashlib.md5(img.tobytes()).hexdigest()


def assert_gui_idempotent() -> None:
    """Fail fast if GUI generation drifts between consecutive runs."""
    first = generate_gui()
    second = generate_gui()
    if image_md5(first) != image_md5(second):
        print("ERROR: GUI generation is not idempotent (output changed between runs).", file=sys.stderr)
        sys.exit(1)


def main() -> None:
    require_ref(REF / "crafting_table_side.png")
    require_ref(REF / "crafting_table_front.png")
    assert_gui_idempotent()

    block_dir = ASSETS / "textures/block"
    gui_dir = ASSETS / "textures/gui"

    save_rgba(generate_top(), block_dir / "uncrafting_table_top.png")
    save_rgba(generate_side(), block_dir / "uncrafting_table_side.png")
    save_rgba(generate_front(), block_dir / "uncrafting_table_front.png")
    save_rgba(generate_gui(), gui_dir / "uncrafting_table.png")

    save_rgba(generate_branding_logo(400), BRANDING / "curseforge_logo_400x400.png")
    save_rgba(generate_branding_logo(256), BRANDING / "curseforge_logo_256x256.png")
    save_rgba(generate_screenshot_template(), BRANDING / "curseforge_screenshot_template.png")


if __name__ == "__main__":
    main()