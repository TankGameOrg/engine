package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class TestUtilities {
    public static State generateBoard(int width, int height, IUnit... units) {
        Board board = new Board(width, height);
        for (IUnit unit : units) {
            board.putUnit(unit);
        }
        return new State(board, buildTestCouncil(0, 0, 0), new AttributeList<>());
    }

    public static Council buildTestCouncil(int coffer, int councilors, int senators) {
        Council c = new Council();
        Attribute.COFFER.to(c, coffer);

        for (int i = 0; i < councilors; i++) {
            c.getCouncillors().add(new PlayerRef("Councilor" + i));
        }

        for (int i = 0; i < senators; i++) {
            c.getSenators().add(new PlayerRef("Senator" + i));
        }

        return c;
    }
}
