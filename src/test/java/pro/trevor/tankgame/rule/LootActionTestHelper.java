package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.definition.range.FunctionVariableRange;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.TankBuilder;
import static pro.trevor.tankgame.util.TestUtilities.generateBoard;

/**
 * A helper for writing looting test cases
 */
public class LootActionTestHelper {
    protected Player subject;
    protected GenericTank subjectTank;
    protected GenericTank targetTank;
    protected State state;

    /**
     * Setup a basic scenario with a subject who can shot and a target to loot
     * @param subjectPosition
     * @param subjectGold
     * @param targetPosition
     * @param targetGold
     */
    protected void setupTest(String subjectPosition, int subjectGold, String targetPosition, int targetGold) {
        subject = new Player("Ted");
        subjectTank = TankBuilder.buildTank()
            .at(new Position(subjectPosition))
            .with(Attribute.PLAYER_REF, new PlayerRef("Ted"))
            .with(Attribute.RANGE, 1)
            .with(Attribute.GOLD, subjectGold)
            .with(Attribute.PLAYER_CAN_LOOT, true)
            .finish();

        targetTank = TankBuilder.buildTank()
            .at(new Position(targetPosition))
            .with(Attribute.GOLD, targetGold)
            .with(Attribute.DEAD, true)
            .finish();

        state = generateBoard(3, 3, subjectTank, targetTank);
        state.getPlayers().add(subject);
    }

    /**
     * Get the range of positions that will be shown to players as lootable locations
     * @param rule
     * @return
     */
    protected Set<Position> getLootablePositions(PlayerConditionRule rule) {
        FunctionVariableRange<PlayerRef, Position> lootablePositionRange = (FunctionVariableRange<PlayerRef, Position>) rule.parameters()[0];
        lootablePositionRange.generate(state, subjectTank.getPlayerRef());
        return lootablePositionRange.getElements();
    }

    /**
     * Check if the rule can be applied to a specific location
     * @param rule
     * @param targetPosition
     * @return
     */
    protected boolean canApply(PlayerConditionRule rule, String targetPosition) {
        boolean canApplyRule = rule.canApplyConditional(state, subjectTank.getPlayerRef(), new Position(targetPosition)).isOk();
        Set<Position> lootablePositions = getLootablePositions(rule);

        assertEquals(canApplyRule, lootablePositions.contains(new Position(targetPosition)),
            "Expected the lootable position range to match canApplyConditional");

        return canApplyRule;
    }

    /**
     * Assert that the rule can be applied and apply it
     * @param rule
     * @param targetPosition
     */
    protected void apply(PlayerConditionRule rule, String targetPosition) {
        assertTrue(canApply(rule, targetPosition));
        rule.apply(state, subjectTank.getPlayerRef(), new Position(targetPosition));
    }

    /**
     * Trigger the looting start of day rules
     */
    protected void startNewDay() {
        TickRules.SET_PLAYER_CAN_LOOT.apply(state, subjectTank);
        TickRules.CLEAR_ONLY_LOOTABLE_BY.apply(state, targetTank);
    }
}
