package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.meta.Council;

public class TestState extends State {
    public TestState() {
        super(new Board(1, 1), new Council(), new AttributeList<>());
        Attribute.COFFER.to(getCouncil(), 0);
    }
}
