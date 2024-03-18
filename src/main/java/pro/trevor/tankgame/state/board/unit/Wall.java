package pro.trevor.tankgame.state.board.unit;

import pro.trevor.tankgame.state.board.Position;

public class Wall extends AbstractDurable {

    public static int INITIAL_HEALTH = 3;


    public Wall(Position position) {
        super(position, INITIAL_HEALTH);

    }

    @Override
    public String toString() {
        return position.toString();
    }


    @Override
    public char toBoardCharacter() {
        return (char) ('0' + getDurability());
    }
}
