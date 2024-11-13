package pro.trevor.tankgame.rule.impl.shared.player;

import pro.trevor.tankgame.rule.definition.actions.LogFieldHelpers;
import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;
import pro.trevor.tankgame.rule.definition.player.PlayerConditionRuleImpl;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.rule.definition.player.conditional.PredicateHelpers;
import pro.trevor.tankgame.rule.definition.player.conditional.RuleCondition;
import pro.trevor.tankgame.rule.definition.player.conditional.RulePredicateStream;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Tank;

import java.util.List;

import static pro.trevor.tankgame.util.Util.canMoveTo;

public class Move extends PlayerConditionRuleImpl {

    private final Attribute<Integer> attribute;
    private final Integer cost;

    public Move(Attribute<Integer> attribute, int cost) {
        super("move", "Move your tank new a new position",
                new RuleCondition(Helper.RulePredicate.PLAYER_TANK_IS_ALIVE_PREDICATE),
                new RuleCondition(
                        new RulePredicateStream<>(PredicateHelpers::getTank)
                                .filter(PredicateHelpers.minimum(attribute, cost)),
                        new RulePredicateStream<>(PredicateHelpers::getTank)
                                .filter(PredicateHelpers::hasLogEntry)
                                .filter((context, tank) -> {
                                    Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
                                    return canMoveTo(context.getState(), tank.getPosition(), target, tank.getOrElse(Attribute.SPEED, 1));
                                    }, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Tank cannot move to target position"))));
        this.attribute = attribute;
        this.cost = cost;
    }

    @Override
    public void apply(PlayerRuleContext context) {
        super.apply(context);
        Tank tank = PredicateHelpers.getTank(context).getValue();
        tank.put(attribute, tank.getUnsafe(attribute) - cost);
        context.getState().getBoard().putUnit(new EmptyUnit(tank.getPosition()));
        tank.setPosition(PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION));
        context.getState().getBoard().putUnit(tank);
    }

    @Override
    public List<LogFieldSpec<?>> getFieldSpecs(PlayerRuleContext context) {
        return List.of(LogFieldHelpers.getMovablePositionsSpec(context));
    }
}
