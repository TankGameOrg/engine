package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.impl.shared.rule.TickRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.HealthPool;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.DummyState;
import pro.trevor.tankgame.util.TankBuilder;

public class HealthPoolTest {
    @Test
    public void GainHealthInHealthPool() {
        Tank tank = TankBuilder.buildV3Tank().at(new Position("A1")).with(Attribute.DURABILITY, 2)
                .with(Attribute.DEAD, false).finish();
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(3, tank.getDurability());
    }

    @Test
    public void DeadTankInHealthPool() {
        Tank tank = TankBuilder.buildV3Tank().at(new Position("A1")).with(Attribute.DURABILITY, 2)
                .with(Attribute.DEAD, true).finish();
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(2, tank.getDurability());
    }

    @Test
    public void TankNotInHealthPool() {
        Tank tank = TankBuilder.buildV3Tank().at(new Position("A1")).with(Attribute.DURABILITY, 2)
                .with(Attribute.DEAD, false).finish();
        State state = new State(2, 2);
        HealthPool hp = new HealthPool(new Position("B2"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(2, tank.getDurability());
    }

    @Test
    public void HealthPoolTwoRegen() {
        Tank tank = TankBuilder.buildV3Tank().at(new Position("A1")).with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false).finish();
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 2);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(3, tank.getDurability());
    }

    @Test
    public void HealthPoolMultipleApplications() {
        Tank tank = TankBuilder.buildV3Tank().at(new Position("A1")).with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false).finish();
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();

        rule.apply(state, tank);
        assertEquals(2, tank.getDurability());
        rule.apply(state, tank);
        assertEquals(3, tank.getDurability());
    }
}
