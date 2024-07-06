package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.impl.shared.rule.TickRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.HealthPool;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.util.TankBuilder;

public class HealthPoolTest {
    @Test
    public void GainHealthInHealthPool() {
        GenericTank tank = TankBuilder.buildTank().at(new Position("A1")).with(Attribute.DURABILITY, 2)
                .with(Attribute.DEAD, false).finish();
        TestState state = new TestState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(3, Attribute.DURABILITY.unsafeFrom(tank));
    }

    @Test
    public void DeadTankInHealthPool() {
        GenericTank tank = TankBuilder.buildTank().at(new Position("A1")).with(Attribute.DURABILITY, 2)
                .with(Attribute.DEAD, true).finish();
        TestState state = new TestState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(2, Attribute.DURABILITY.unsafeFrom(tank));
    }

    @Test
    public void TankNotInHealthPool() {
        GenericTank tank = TankBuilder.buildTank().at(new Position("A1")).with(Attribute.DURABILITY, 2)
                .with(Attribute.DEAD, false).finish();
        State state = new State(new Board(2, 2), new Council(), new AttributeList<>());
        HealthPool hp = new HealthPool(new Position("B2"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(2, Attribute.DURABILITY.unsafeFrom(tank));
    }

    @Test
    public void HealthPoolTwoRegen() {
        GenericTank tank = TankBuilder.buildTank().at(new Position("A1")).with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false).finish();
        TestState state = new TestState();
        HealthPool hp = new HealthPool(new Position("A1"), 2);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(3, Attribute.DURABILITY.unsafeFrom(tank));
    }

    @Test
    public void HealthPoolMultipleApplications() {
        GenericTank tank = TankBuilder.buildTank().at(new Position("A1")).with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false).finish();
        TestState state = new TestState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule<GenericTank> rule = TickRules.GetHealTanksInHealthPoolRule();

        rule.apply(state, tank);
        assertEquals(2, Attribute.DURABILITY.unsafeFrom(tank));
        rule.apply(state, tank);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(tank));
    }
}
