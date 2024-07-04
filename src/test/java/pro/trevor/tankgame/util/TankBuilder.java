package pro.trevor.tankgame.util;

import org.json.JSONObject;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;

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
        json.put("class", "TankV3");
        json.put(Attribute.PLAYER_REF.getJsonName(), new PlayerRef("test").toJson());
        json.put(Attribute.POSITION.getJsonName(), new Position("A1").toJson());
        return new TankBuilder<>(new Tank(json));
    }
}
