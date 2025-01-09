package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.attribute.ListEntity;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.meta.Council;

public class TestState extends State {
    public TestState() {
        super(new Board(1, 1), new Council(), new ListEntity<>());
        getCouncil().put(Attribute.COFFER, 0);
        getCouncil().put(Attribute.CAN_BOUNTY, true);
    }
}
