package state.board;

public class Wall extends AbstractDestroyable {

    public static int INITIAL_HEALTH = 3;


    public Wall(Position position) {
        super(position, INITIAL_HEALTH);

    }

    @Override
    public void handleDestruction() {

    }
}
