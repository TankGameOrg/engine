package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.Util;


public class MovePositionRange extends FunctionVariableRange<GenericTank, Position> {

    public MovePositionRange(String name) {
        super(name, (state, tank) -> Util.allPossibleMoves(state.getBoard(), tank.getPosition(), tank.getOrElse(Attribute.SPEED, 1)));
    }

    @Override
    public String getJsonDataType() {
        return "position";
    }

    @Override
    public Class<Position> getBoundClass() {
        return Position.class;
    }
}
