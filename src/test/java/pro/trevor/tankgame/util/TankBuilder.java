package pro.trevor.tankgame.util;

import java.util.Map;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class TankBuilder<T extends GenericTank> {
    private final T tank;

    public TankBuilder(T tank) {
        this.tank = tank;
    }

    public <E> TankBuilder<T> with(Attribute<E> attribute, E value) {
        tank.put(attribute, value);
        return this;
    }

    public TankBuilder<T> named(String name) {
        tank.put(Attribute.NAME, name);
        return this;
    }

    public TankBuilder<T> at(Position p) {
        tank.setPosition(p);
        return this;
    }

    public T finish() {
        return tank;
    }

    public static TankBuilder<GenericTank> buildTank() {
        GenericTank tank = new GenericTank(
            new PlayerRef("test"),
            new Position("A1"),
            Map.of(
                Attribute.DEAD, false,
                Attribute.ACTION_POINTS, 0
            )
        );

        return new TankBuilder<>(tank);
    }
}
