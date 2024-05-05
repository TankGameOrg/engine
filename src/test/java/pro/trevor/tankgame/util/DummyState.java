package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;

public class DummyState extends State {

    public static final State DUMMY_STATE = new DummyState();

    public DummyState() {
        super(1, 1);
    }
}
