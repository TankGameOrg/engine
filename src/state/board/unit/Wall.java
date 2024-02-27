package state.board.unit;

import state.board.AbstractDestroyable;
import state.board.Position;

public class Wall extends AbstractDestroyable {

    public static int INITIAL_HEALTH = 3;


    public Wall(Position position) {
        super(position, INITIAL_HEALTH);

    }

    @Override
    public void handleDestruction() {

    }

    @Override
    public String toString() {
        return "W";
    }
}
