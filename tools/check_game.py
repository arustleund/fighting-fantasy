#!/usr/bin/env python3
"""Report missing and unused files in a Fighting Fantasy game directory.

Usage:
    tools/check_game.py <game-directory> [--max-page N]

Checks:
  * required files (config/flags.xml, config/items.xml, pages/intro.xml)
  * gaps in numbered pages (1..N, where N defaults to the highest numbered page)
  * links (http://link:N, <link page>, <flaggedLink page>) to pages that don't exist
  * pages that nothing links to
  * pages without a page sound (sounds/<page>.mp3)
  * <text> blocks without a sound (sounds/<page>.<id>.mp3)
  * sounds and images that the engine will never load
"""
import argparse
import re
import sys
from pathlib import Path

REQUIRED = ["config/flags.xml", "config/items.xml", "pages/intro.xml"]
NON_GAME_PAGES = {"template"}


def page_sort_key(name):
    return [(0, int(part), "") if part.lstrip("-").isdigit() else (1, 0, part) for part in name.split(".")]


def show(title, items):
    items = sorted(items, key=page_sort_key)
    print(f"\n{title} ({len(items)}):")
    print("  " + (", ".join(items) if items else "none"))


def main():
    parser = argparse.ArgumentParser(description="Report missing and unused files in a game directory.")
    parser.add_argument("game_dir", type=Path)
    parser.add_argument("--max-page", type=int, help="highest page number expected (default: highest found)")
    args = parser.parse_args()
    d = args.game_dir

    if not (d / "pages").is_dir():
        sys.exit(f"{d} has no pages directory")

    missing_required = [r for r in REQUIRED if not (d / r).exists()]
    print("Required files:", "all present" if not missing_required else "MISSING " + ", ".join(missing_required))

    pages = {p.stem for p in (d / "pages").glob("*.xml")}
    sounds = {p.name for p in (d / "sounds").glob("*.mp3")}
    images = {p.stem for p in (d / "images").glob("*.png")}

    links = {}  # target page -> pages linking to it
    text_ids = {}  # page -> ids of its <text> blocks
    for f in (d / "pages").glob("*.xml"):
        src = f.read_text(errors="replace")
        for target in re.findall(r'http://link:([^"\'\s>]+)', src) + re.findall(r'\bpage="([^"]+)"', src):
            links.setdefault(target, set()).add(f.stem)
        text_ids[f.stem] = set(re.findall(r'<text\b[^>]*\bid="([^"]+)"', src))

    numbered = [int(p) for p in pages if p.isdigit()]
    max_page = args.max_page or max(numbered, default=0)
    show(f"Missing numbered pages (1-{max_page})", [str(n) for n in range(1, max_page + 1) if str(n) not in pages])

    broken = [t for t in links if t not in pages]
    print(f"\nBroken links ({len(broken)}):")
    for t in sorted(broken, key=page_sort_key):
        print(f"  {t} <- linked from {', '.join(sorted(links[t], key=page_sort_key))}")
    if not broken:
        print("  none")

    game_pages = pages - NON_GAME_PAGES
    show("Pages nothing links to", [p for p in game_pages if p not in links and p != "intro"])
    show("Pages without a page sound", [p for p in game_pages if f"{p}.mp3" not in sounds])
    show("<text> blocks without a sound",
         [f"{p}.{i}" for p, ids in text_ids.items() if p in game_pages for i in ids if f"{p}.{i}.mp3" not in sounds])

    unused_sounds = []
    for s in sounds:
        m = re.match(r"^(.+?)(?:\.(-?\d+))?\.mp3$", s)
        page, text_id = m.group(1), m.group(2)
        if page not in pages or (text_id is not None and text_id not in text_ids.get(page, set())):
            unused_sounds.append(s)
    show("Sounds that will never play", unused_sounds)
    show("Images that will never show", [f"{i}.png" for i in images if i not in pages])


if __name__ == "__main__":
    main()