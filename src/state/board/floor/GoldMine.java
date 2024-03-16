package state.board.floor;

import state.board.Position;

public class GoldMine extends StandardFloor {
    public GoldMine(Position position) {
        super(position);
    }

    @Override
    public char toBoardCharacter() {
        return 'G';
    }
}
