package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Position;

public class GoldMine extends StandardFloor {
    public GoldMine(Position position) {
        super(position);
    }

    @Override
    public char toBoardCharacter() {
        return 'G';
    }
}
