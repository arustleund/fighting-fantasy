#!/usr/bin/env python3
"""Generate missing narration sounds for a game directory with ElevenLabs.

For every page without sounds/<page>.mp3 and every <text> block without
sounds/<page>.<id>.mp3, the text the player sees is narrated. Text that already
has a recording somewhere else in the game (e.g. "You win! Click here to
continue.") is copied instead of being generated again.

Without --generate this is a dry run: it lists what would be made and how many
characters it costs.

The API key is read from $ELEVENLABS_API_KEY or ~/.config/elevenlabs/api_key.

  tools/narrate.py ~/Dropbox/nightdragon --voice VOICE_ID
  tools/narrate.py ~/Dropbox/nightdragon --voice VOICE_ID --generate
"""
import argparse
import json
import os
import re
import shutil
import sys
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

SKIP_PAGES = {"template", "0", "doStats"}


def local(tag):
    return tag.rsplit("}", 1)[-1]


def clean(element):
    """The text the player sees, minus the BEGIN BATTLE link, which is never narrated."""
    if element is None:
        return ""
    text = " ".join("".join(element.itertext()).split())
    return re.sub(r"\s*\bBEGIN BATTLE\b\s*", " ", text).strip()


def page_texts(xml_file):
    """Yields (sound file name, text) for the page text and each <text> block."""
    try:
        root = ET.parse(xml_file).getroot()
    except ET.ParseError:
        return  # comment-only placeholder pages such as 230.xml
    page = xml_file.stem
    for child in root:
        if local(child.tag) == "pagetext":
            yield f"{page}.mp3", clean(child)
        elif local(child.tag) == "text":
            yield f"{page}.{child.get('id')}.mp3", clean(child)


def key_for(text):
    return re.sub(r"[^a-z0-9]+", " ", text.lower()).strip()


def api_key():
    key = os.environ.get("ELEVENLABS_API_KEY")
    key_file = Path.home() / ".config" / "elevenlabs" / "api_key"
    if not key and key_file.exists():
        key = key_file.read_text().strip()
    if not key:
        sys.exit(f"Set ELEVENLABS_API_KEY or put the key in {key_file}")
    return key


def tts(text, voice, model, output_format, key):
    request = urllib.request.Request(
        f"https://api.elevenlabs.io/v1/text-to-speech/{voice}?output_format={output_format}",
        data=json.dumps({"text": text, "model_id": model}).encode(),
        headers={"xi-api-key": key, "Content-Type": "application/json", "Accept": "audio/mpeg"},
    )
    try:
        with urllib.request.urlopen(request, timeout=300) as response:
            return response.read()
    except urllib.error.HTTPError as e:
        sys.exit(f"ElevenLabs error {e.code}: {e.read().decode(errors='replace')}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("game_dir", type=Path)
    parser.add_argument("--voice", required=True, help="ElevenLabs voice ID")
    parser.add_argument("--model", default="eleven_multilingual_v2")
    parser.add_argument("--format", default="mp3_44100_128", help="ElevenLabs output_format")
    parser.add_argument("--only", nargs="*", help="Only these sound names, e.g. 295 295.3")
    parser.add_argument("--generate", action="store_true", help="Actually call ElevenLabs (costs credits)")
    args = parser.parse_args()

    pages_dir = args.game_dir.expanduser() / "pages"
    sounds_dir = args.game_dir.expanduser() / "sounds"

    existing = {}  # normalised text -> sound file that already narrates it
    missing = []  # (sound file name, text)
    for xml_file in sorted(pages_dir.glob("*.xml")):
        if xml_file.stem in SKIP_PAGES:
            continue
        for name, text in page_texts(xml_file):
            if not text:
                continue
            if (sounds_dir / name).exists():
                existing.setdefault(key_for(text), name)
            elif not args.only or name[:-4] in args.only:
                missing.append((name, text))

    copies = [(n, t, existing[key_for(t)]) for n, t in missing if key_for(t) in existing]
    to_make = [(n, t) for n, t in missing if key_for(t) not in existing]

    for name, text, source in copies:
        print(f"COPY  {name:<10} from {source}  ({text[:60]})")
    for name, text in to_make:
        print(f"NEW   {name:<10} {len(text):>5} chars  {text[:70]}{'…' if len(text) > 70 else ''}")
    print(f"\n{len(copies)} to copy, {len(to_make)} to generate, {sum(len(t) for _, t in to_make)} characters")

    if not args.generate:
        print("Dry run. Add --generate to create the files.")
        return

    for name, _, source in copies:
        shutil.copyfile(sounds_dir / source, sounds_dir / name)
    key = api_key()
    for i, (name, text) in enumerate(to_make, 1):
        print(f"[{i}/{len(to_make)}] {name}", flush=True)
        (sounds_dir / name).write_bytes(tts(text, args.voice, args.model, args.format, key))
    print("Done.")


if __name__ == "__main__":
    main()
