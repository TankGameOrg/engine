#!/usr/bin/env python3
import argparse
import json
import random

GENERIC_ENTITY = True

DEFAULT_DURABILITY = 3
DEFAULT_TANK_ATTRIBUTES = {
    "gold": 0,
    "actions": 0,
    "dead": False,
    "range": 2,
    "bounty": 0,
}


def wall(dur=DEFAULT_DURABILITY):
    raw = { "type": "wall" }

    if GENERIC_ENTITY:
        raw["attributes"] = {
            "DURABILITY": dur,
        }
    else:
        raw["health"] = dur

    return raw

tank_counter = 0
names = ["Trevor", "Charlie", "Ryan", "Bryan", "Corey", "Dan", "Beyer", "Ty", "Mike", "Lena", "David", "Isaac", "John", "Stomp", "Schmude", "Xavion"]
bounties = {
    "Corey": 5,
    "Ty": 3,
    "Craig": 3
}
random.shuffle(names)

def tank(name=None, dur=DEFAULT_DURABILITY, **others):
    global tank_counter
    global names
    raw = { "type": "tank" }

    if name is None:
        name = names[tank_counter]

    if GENERIC_ENTITY:
        others.update(DEFAULT_TANK_ATTRIBUTES)
        others = {key.upper(): value for key, value in others.items()}

        
        raw["name"] = name
        raw["attributes"] = {
            "DURABILITY": dur,
            **others
        }

        if name in bounties:
            raw["attributes"]["BOUNTY"] = bounties[name]
        else:
            raw["attributes"]["BOUNTY"] = 0
    else:
        raw.update(DEFAULT_TANK_ATTRIBUTES)
        raw["health"] = dur
        raw["name"] = name
        raw.update(others)

    tank_counter += 1
    return raw


def gold_mine():
    return {
        "type": "gold_mine",
    }


def fill_empty(board):
    for i, row in enumerate(board):
        board[i] = [({ "type": "empty" } if cell is None else cell) for cell in row]

def set_positions(board):
    y = 1
    for row in board:
        x = 0
        for cell in row:
            if cell is not None:
                letter = chr(ord('A')+x)
                position = f'{letter}{y}'
                cell["position"] = position
            x += 1
        y += 1



board = [
    [ wall(dur=6), wall(dur=6), wall(dur=6), wall(dur=6), tank(),      None,        None,        tank(),      wall(dur=6), wall(dur=6), wall(dur=6), wall(dur=6) ],
    [ wall(dur=6), wall(dur=4), wall(dur=4), wall(dur=4), None,        None,        None,        None,        wall(dur=4), wall(dur=4), wall(dur=4), wall(dur=6) ],
    [ wall(dur=6), wall(dur=4), None,        wall(dur=2), None,        None,        None,        None,        wall(dur=2), None,        wall(dur=4), wall(dur=6) ],
    [ wall(dur=6), wall(dur=4), wall(dur=2), wall(dur=1), tank(),      None,        None,        tank(),      wall(dur=1), wall(dur=2), wall(dur=4), wall(dur=6) ],
    [ tank(),      None,        None,        tank(),      wall(dur=6), wall(dur=2), wall(dur=2), wall(dur=6), tank(),      None,        None,        tank(),     ],
    [ None,        None,        None,        None,        wall(dur=2), wall(dur=2), wall(dur=2), wall(dur=2), None,        None,        None,        None,       ],
    [ None,        None,        None,        None,        wall(dur=2), wall(dur=2), wall(dur=2), wall(dur=2), None,        None,        None,        None,       ],
    [ tank(),      None,        None,        tank(),      wall(dur=6), wall(dur=2), wall(dur=2), wall(dur=6), tank(),      None,        None,        tank(),     ],
    [ wall(dur=6), wall(dur=4), wall(dur=2), wall(dur=1), tank(),      None,        None,        tank(),      wall(dur=1), wall(dur=2), wall(dur=4), wall(dur=6) ],
    [ wall(dur=6), wall(dur=4), None,        wall(dur=2), None,        None,        None,        None,        wall(dur=2), None,        wall(dur=4), wall(dur=6) ],
    [ wall(dur=6), wall(dur=4), wall(dur=4), wall(dur=4), None,        None,        None,        None,        wall(dur=4), wall(dur=4), wall(dur=4), wall(dur=6) ],
    [ wall(dur=6), wall(dur=6), wall(dur=6), wall(dur=6), tank(),      None,        None,        tank(),      wall(dur=6), wall(dur=6), wall(dur=6), wall(dur=6) ]
]

floor = [
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, gold_mine(), None, None, None, None, None, None, gold_mine(), None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, gold_mine(), None, None, None, None, None, None, gold_mine(), None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ],
    [ None, None, None,        None, None, None, None, None, None, None,        None, None ]
]

set_positions(board)
fill_empty(board)
fill_empty(floor)

state = {
    "running": True,
    "winner": "",
    "type": "state",
    "day": 0,
    "board": {
        "type": "board",
        "unit_board": board,
        "floor_board": floor,
    },
    "council": {
        "type": "council",
        "coffer": 0,
        "council": [],
        "senate": [],
        "armistice_vote_cap": 35,
        "armistice_vote_count": 0,
        "can_bounty": False
    },
}

with open("example/v4_map.json", "w") as f:
    json.dump({
        "fileFormatVersion": 5,
        "logBook": {
            "gameVersion": "4"
        },
        "initialGameState": state,
    }, f, indent=4)
