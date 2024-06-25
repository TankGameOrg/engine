package pro.trevor.tankgame.util;

import org.json.JSONObject;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Player;

public class TankBuilder<T extends GenericTank> {
    private final T tank;

    public TankBuilder(T tank) {
        this.tank = tank;
    }

    public <E> TankBuilder<T> with(Attribute<E> attribute, E value) {
        attribute.to(tank, value);
        return this;
    }

    public TankBuilder<T> at(Position p) {
        tank.setPosition(p);
        return this;
    }

    public T finish() {
        return tank;
    }

    public static TankBuilder<Tank> buildV3Tank() {
        JSONObject json = new JSONObject();
        JSONObject attributes = new JSONObject();
        attributes.put(Attribute.PLAYER.getName(), new Player("test").toJson());
        attributes.put(Attribute.POSITION.getName(), new Position("A1").toJson());
        json.put("attributes", attributes);
        return new TankBuilder<>(new Tank(json));
    }
}
