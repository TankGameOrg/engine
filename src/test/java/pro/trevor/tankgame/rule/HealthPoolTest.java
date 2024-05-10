package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.HealthPool;
import pro.trevor.tankgame.util.DummyState;
import pro.trevor.tankgame.util.TestUtilities;

public class HealthPoolTest 
{
    @Test
    public void GainHealthInHealthPool()
    {
        Tank tank = TestUtilities.buildPositionedTank("A1", 0, 0, 2, false);
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(3, tank.getDurability());
    }
    
    @Test
    public void DeadTankInHealthPool()
    {
        Tank tank = TestUtilities.buildPositionedTank("A1", 0, 0, 2, true);
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(2, tank.getDurability());
    }

    @Test
    public void TankNotInHealthPool()
    {
        Tank tank = TestUtilities.buildPositionedTank("A1", 0, 0, 2, false);
        State state = new State(2, 2);
        HealthPool hp = new HealthPool(new Position("B2"), 1);
        state.getBoard().putFloor(hp);

        TickActionRule rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(2, tank.getDurability());
    }

    @Test
    public void HealthPoolHealMultiple()
    {
        Tank tank = TestUtilities.buildPositionedTank("A1", 0, 0, 1, false);
        DummyState state = new DummyState();
        HealthPool hp = new HealthPool(new Position("A1"), 2);
        state.getBoard().putFloor(hp);

        TickActionRule rule = TickRules.GetHealTanksInHealthPoolRule();
        rule.apply(state, tank);

        assertEquals(3, tank.getDurability());
    }
}
