package pro.trevor.tankgame.util;

import java.util.Map;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class TankBuilder<T extends Tank> {
    private final T tank;

    public TankBuilder(T tank) {
        this.tank = tank;
    }

    public <E> TankBuilder<T> with(Attribute<E> attribute, E value) {
        tank.put(attribute, value);
        return this;
    }

    public TankBuilder<T> at(Position p) {
        tank.setPosition(p);
        return this;
    }

    public T finish() {
        return tank;
    }


    private static int count = 0;

    public static TankBuilder<Tank> buildTank() {
        ++count;
        Tank tank = new Tank(
            new PlayerRef("test " + count),
            new Position("A1"),
            Map.of(
                Attribute.DEAD, false,
                Attribute.ACTION_POINTS, 0
            )
        );

        return new TankBuilder<>(tank);
    }
}
