package pro.trevor.tankgame.util;

import org.json.JSONObject;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.rule.impl.version3.TankAttribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.attribute.IAttribute;
import pro.trevor.tankgame.state.board.unit.GenericTank;

public class TankBuilder<T extends GenericTank<E>, E extends Enum<E> & IAttribute> {
    private final T tank;

    public TankBuilder(T tank) {
        this.tank = tank;
    }

    public TankBuilder<T, E> with(E attribute, Object value) {
        tank.set(attribute, value);
        return this;
    }

    public TankBuilder<T, E> at(Position p) {
        tank.setPosition(p);
        return this;
    }

    public T finish() {
        return tank;
    }

    public static TankBuilder<Tank, TankAttribute> buildV3Tank() {
        JSONObject json = new JSONObject();
        JSONObject attributes = new JSONObject();
        json.put("name", "test");
        json.put("position", "A1");
        json.put("type", "tank");
        json.put("attributes", attributes);
        return new TankBuilder<>(new Tank(json));
    }
}
