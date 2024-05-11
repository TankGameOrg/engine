#!/usr/bin/env python3
import argparse
import json
import random
import sys

import PySimpleGUI as sg

application_name = 'Tank Game Map Maker'

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

def empty():
    return {"type": "empty",}

def set_positions(board):
    y = 1
    for row in board:
        x = 0
        for cell in row:
            if cell is not None and cell["type"] != "empty":
                letter = chr(ord('A')+x)
                position = f'{letter}{y}'
                cell["position"] = position
            x += 1
        y += 1

def save_board(filepath, state):
    with open(filepath, "w") as f:
        f.seek(0)
        json.dump({
            "fileFormatVersion": 5,
            "logBook": {
                "gameVersion": "4"
            },
            "initialGameState": state,
        }, f, indent=4)

def load_state(filepath):
    with open(filepath, "r") as f:
        return json.load(f)['initialGameState']

def grid_config_window(row, col, unit_board):
    radio_options = ["wall", "tank", "empty"]
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring board space {(col, row)}.')],
                     [sg.Text(f'What is going to be here?')],
                     [[sg.Radio(opt, 1, key=opt) for opt in radio_options]],
                     [sg.Submit(), sg.Cancel()]]).read(close=True)
    if event == 'Cancel':
        return None
    
    option = [key for key in radio_options if values[key]][0]
    if option == "wall":
        wall_config_window(row, col, unit_board)
    elif option == "tank":
        tank_config_window(row, col, unit_board)
    elif option == "empty":
        empty_config_window(row, col, unit_board)
    else:
        print(f'Error: bad grid space selection: {option}')
        sys.exit()
    return option

def empty_config_window(row, col, unit_board):
    unit_board[col][row] = empty()

def wall_config_window(row, col, unit_board):
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring board space {(col, row)}.')],
                     [sg.Text(f'It will be a wall.')],
                     [sg.Text(f'What is its durability?')],
                     [sg.Input(key="durability")],
                     [sg.Submit()]]).read(close=True)
    
    durability = int(values["durability"])
    unit_board[col][row] = wall(durability)

def tank_config_window(row, col, unit_board):
    unit_board[col][row] = tank()

def new_map_window():
    event, values = sg.Window(application_name,
                    [[sg.Text('Map Name:')],
                     [sg.Input(key='name')],
                     [sg.Text('Board Width:')],      
                     [sg.Input(key='width')],
                     [sg.Text('Board Height')],
                     [sg.Input(key='height')],
                     [sg.Submit(), sg.Cancel()]]).read(close=True)

    name = values['name']
    width = int(values["width"])
    height = int(values["height"])
    unit_board = [[empty() for x in range(height)] for y in range(width)]
    floor_board = [[empty() for x in range(height)] for y in range(width)]
    state = {
        "running": True,
        "winner": "",
        "type": "state",
        "day": 0,
        "board": {
            "type": "board",
            "unit_board": unit_board,
            "floor_board": floor_board,
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
    return name, state

def load_map_window():
    layout = [[sg.Text('Load an existing map.')],
              [sg.Text('File', size=(8, 1)), sg.Input(), sg.FileBrowse()],
              [sg.Submit(), sg.Cancel()]]
    event, values = sg.Window(application_name, layout).read(close=True)
    return values[0], load_state(values[0])

def main():
    sg.theme('DarkAmber')
    event, values = sg.Window(application_name,
                              [[sg.Button("New Map")],
                               [sg.Button("Load Map")]]).read(close=True)
    name = None
    state = None
    if event == "New Map":
        name, state = new_map_window()
    elif event == "Load Map":
        name, state = load_map_window()

    type_to_icon_map = {
        "tank": "T",
        "wall": "W",
        "empty": "_"
    }

    unit_board = state['board']['unit_board'] 
    width = len(unit_board)
    height = len(unit_board[0])

    layout =  [[sg.Button(type_to_icon_map[unit_board[i][j]['type']], size=(4, 2), key=(i,j), pad=(0,0)) for i in range(width)] for j in range(height)]
    window = sg.Window(application_name, layout)

    while True:
        event, values = window.read()
        if event in (sg.WIN_CLOSED, 'Exit'):
            break

        col = event[0]
        row = event[1]
        type = grid_config_window(row, col, unit_board)

        if type is not None: window[(col, row)].update(type_to_icon_map[type])
        # For this example, change the text of the button to the board's value and turn color black
        #window[event].update(board[event[0]][event[1]], button_color=('white','black'))
    window.close()

    # Construct and save state
    set_positions(unit_board)
    state['board']['unit_board'] = unit_board
    save_board(name, state)

if __name__ == "__main__":
    main()
