package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import pro.trevor.tankgame.rule.definition.actions.EnumeratedLogFieldSpec;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.Random;
import pro.trevor.tankgame.util.TankBuilder;
import static pro.trevor.tankgame.util.TestUtilities.generateBoard;

/**
 * A helper for writing looting test cases
 */
public class LootActionTestHelper {
    protected Player subject;
    protected Tank subjectTank;
    protected Tank targetTank;
    protected State state;

    /**
     * Setup a basic scenario with a subject who can shot and a target to loot
     */
    protected void setupTest(String subjectPosition, int subjectGold) {
        subject = new Player("Ted");
        subjectTank = TankBuilder.buildTank()
            .at(new Position(subjectPosition))
            .with(Attribute.PLAYER_REF, new PlayerRef("Ted"))
            .with(Attribute.RANGE, 2)
            .with(Attribute.GOLD, subjectGold)
            .with(Attribute.PLAYER_CAN_LOOT, true)
            .finish();

        state = generateBoard(4, 4, subjectTank);
        state.put(Attribute.RANDOM, new Random(0));
        state.getPlayers().add(subject);
    }

    protected void addTargetTank(String targetPosition, int targetGold) {
        targetTank = TankBuilder.buildTank()
            .at(new Position(targetPosition))
            .with(Attribute.GOLD, targetGold)
            .with(Attribute.DEAD, true)
            .finish();

        state.getBoard().putUnit(targetTank);
    }

    protected PlayerRuleContext makeLootContext(State state, Tank subjectTank, String targetPosition) {
        return new ContextBuilder(state, subjectTank.getPlayerRef())
            .withTarget(new Position(targetPosition))
            .finish();
    }

    /**
     * Get the range of positions that will be shown to players as lootable locations
     */
    protected Set<Position> getLootablePositions(IPlayerRule rule, PlayerRuleContext context) {
        EnumeratedLogFieldSpec<Position> positionsSpec = (EnumeratedLogFieldSpec<Position>) rule.getFieldSpecs(context).stream()
            .filter((spec) -> spec.getAttribute().equals(Attribute.TARGET_POSITION))
            .findAny().get();

        return positionsSpec.getValueDescriptors().stream()
            .map((descriptor) -> descriptor.getValue())
            .collect(Collectors.toSet());
    }

    /**
     * Check if the rule can be applied to a specific location
     */
    protected boolean canApply(IPlayerRule rule, String targetPosition) {
        PlayerRuleContext context = makeLootContext(state, subjectTank, targetPosition);
        boolean canApplyRule = rule.canApply(context).isEmpty();
        Set<Position> lootablePositions = getLootablePositions(rule, context);

        assertEquals(canApplyRule, lootablePositions.contains(new Position(targetPosition)),
            "Expected the lootable position range to match canApplyConditional");

        return canApplyRule;
    }

    /**
     * Assert that the rule can be applied and apply it
     */
    protected void apply(IPlayerRule rule, String targetPosition) {
        assertTrue(canApply(rule, targetPosition));
        rule.apply(makeLootContext(state, subjectTank, targetPosition));
    }

    /**
     * Trigger the looting start of day rules
     */
    protected void startNewDay() {
        TickRules.SET_PLAYER_CAN_LOOT.apply(state, subjectTank);

        if(targetTank != null) {
            TickRules.CLEAR_ONLY_LOOTABLE_BY.apply(state, targetTank);
        }
    }
}
