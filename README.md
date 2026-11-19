# About
A framework and UI for playing games in the Fighting Fantasy book format.

# Game
To play, the game expects a zip file or a directory containing game files. The folder
should contain the following structure:

```
game
├── config
│   ├── flags.xml
│   └── items.xml
├── images
│   ├── 1.png
│   ├── 204.png
│   └── 78.png
└── pages
    ├── 0.xml
    ├── 1.xml
    ├── 10.xml
    ├── 106.xml
    ├── 107.xml
    └── intro.xml
```

The game will start at an intro page: `pages/intro.xml`

## Images
The picture for a page is `images/<page>.png`. It can change while the player is on the page:

* `images/<page>.<id>.png` is shown when `<displayText id="<id>"/>` displays that text block
* `images/<page>.<name>.png` is shown by a `<showImage name="<name>"/>` command, which can go anywhere other
  commands do (e.g. in a `<multicommand>` behind a link)

When a file doesn't exist the current picture stays. Going to another page starts over with that page's picture.

See the `docs` directory in this repository for items and page schemas. See 
the `docs/reference.xml` file for an example page that demonstrates the different
tags.

# Checking a game directory
`tools/check_game.py` reports missing pages, broken links, missing sounds, and
files the game will never load:

```
python3 tools/check_game.py /path/to/game [--max-page 400]
```