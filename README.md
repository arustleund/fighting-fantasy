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
    └── 107.xml
```

The game will start at Page 0 (`0.xml`).

See the `docs` directory in this repository for items and page schemas. See 
the `docs/reference.xml` file for an example page that demonstrates the different
tags.