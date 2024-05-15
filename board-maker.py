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

def health_pool(regen):
    return {
        "type": "health_pool",
        "regen_amount": regen,
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

def grid_config_window(row, col, unit_board, floor_board):
    radio_options = ["wall", "tank", "empty"]
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring board space {(col, row)}.')],
                     [sg.Text(f'Which board do you want to configure?')],
                     [sg.Button("Unit Board"), sg.Button("Floor Board")],
                     [sg.Cancel()]]).read(close=True)
    if event == 'Cancel':
        return None, None
    elif event == "Unit Board":
        return "unit", unit_config_window(row, col, unit_board)
    elif event == "Floor Board":
        return "floor", floor_config_window(row, col, floor_board)

def empty_config_window(row, col, board):
    board[row][col] = empty()
    return "empty"

def wall_config_window(row, col, unit_board):
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring unit board space {(col, row)}.')],
                     [sg.Text(f'It will be a wall.')],
                     [sg.Text(f'What is its durability?')],
                     [sg.Input(key="durability")],
                     [sg.Submit(), sg.Cancel()]]).read(close=True)
    if event == 'Cancel':
        return None
    
    durability = int(values["durability"])
    unit_board[row][col] = wall(durability)
    return "wall"

def tank_config_window(row, col, unit_board):
    unit_board[row][col] = tank()
    return "tank"

def unit_config_window(row, col, unit_board):
    radio_options = ["wall", "tank", "empty"]
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring unit board space {(col, row)}.')],
                     [sg.Text(f'What is going to be here?')],
                     [[sg.Radio(opt, 1, key=opt) for opt in radio_options]],
                     [sg.Submit(), sg.Cancel()]]).read(close=True)
    if event == 'Cancel':
        return None
    
    option = [key for key in radio_options if values[key]][0]
    if option == "wall":
        option = wall_config_window(row, col, unit_board)
    elif option == "tank":
        option = tank_config_window(row, col, unit_board)
    elif option == "empty":
        option = empty_config_window(row, col, unit_board)
    else:
        print(f'Error: bad grid space selection: {option}')
        sys.exit()
    return option

def floor_config_window(row, col, floor_board):
    radio_options = ["empty", "gold_mine", "health_pool"]
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring floor board space {(col, row)}.')],
                     [sg.Text(f'What is going to be here?')],
                     [[sg.Radio(opt, 1, key=opt) for opt in radio_options]],
                     [sg.Submit(), sg.Cancel()]]).read(close=True)
    if event == 'Cancel':
        return None
    
    option = [key for key in radio_options if values[key]][0]
    if option == "empty":
        option = empty_config_window(row, col, floor_board)
    elif option == "gold_mine":
        option = gold_mine_config_window(row, col, floor_board)
    elif option == "health_pool":
        option = health_pool_config_window(row, col, floor_board)
    else:
        print(f'Error: bad grid space selection: {option}')
        sys.exit()
    return option

def gold_mine_config_window(row, col, floor_board):
    floor_board[row][col] = gold_mine()
    return "gold_mine"

def health_pool_config_window(row, col, floor_board):
    event, values = sg.Window('Tank Game Board Maker',
                    [[sg.Text(f'You are configuring floor board space {(col, row)}.')],
                     [sg.Text(f'It will be a health pool.')],
                     [sg.Text(f'What is its Regeneration Amount?')],
                     [sg.Input(key="regen_amount")],
                     [sg.Submit(), sg.Cancel()]]).read(close=True)
    if event == 'Cancel':
        return None
    
    regen = int(values["regen_amount"])
    floor_board[row][col] = health_pool(regen)
    return "health_pool"

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
    unit_board = [[empty() for x in range(width)] for y in range(height)]
    floor_board = [[empty() for x in range(width)] for y in range(height)]
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
        "empty": " "
    }
    type_to_color_map = {
        "gold_mine": "gold",
        "empty": "white",
        "health_pool": "pale violet red"
    }

    unit_board = state['board']['unit_board']
    floor_board = state['board']['floor_board']
    height = len(unit_board)
    width = len(unit_board[0])

    layout = [[[sg.Button(type_to_icon_map[unit_board[j][i]['type']], size=(4, 2), key=(j,i), pad=(0,0), button_color=type_to_color_map[floor_board[j][i]['type']]) for i in range(width)] for j in range(height)],
              [[sg.Button("Save")], [sg.Button("Exit")]]]
    window = sg.Window(application_name, layout)

    while True:
        event, values = window.read()
        if event in (sg.WIN_CLOSED, 'Exit'):
            break
        elif event == "Save":
            # Construct and save state
            set_positions(unit_board)
            state['board']['unit_board'] = unit_board
            save_board(name, state)
        else:
            row = event[0]
            col = event[1]
            type, value = grid_config_window(row, col, unit_board, floor_board)
            if value is not None:
                if type == "unit":
                    window[(row, col)].update(type_to_icon_map[value])
                elif type == "floor":
                    window[(row, col)].update(button_color=type_to_color_map[value])
    
    window.close()

if __name__ == "__main__":
    main()
