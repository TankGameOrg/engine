package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.impl.shared.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.RulesetType;

import static pro.trevor.tankgame.rule.impl.shared.TickRules.INCREMENT_DAY_ON_TICK;

@RulesetType(name = "default-v3")
public class DefaultV3RulesetRegister extends BaseRulesetRegister implements IRulesetRegister {

    @Override
    public void registerEnforcerRules(Ruleset ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Attribute.DURABILITY, 3));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.RANGE, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.GOLD, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.ACTION_POINTS, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Attribute.ACTION_POINTS, 5));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.BOUNTY, 0));
        invariants.put(BasicWall.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));

        invariants.put(Council.class, new MinimumEnforcer<>(Attribute.COFFER, 0));
    }

    @Override
    public void registerTickRules(Ruleset ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(Tank.class, TickRules.GetGrantActionPointsOnTickRule(1));
        tickRules.put(Tank.class, TickRules.DISTRIBUTE_GOLD_TO_TANKS);
        tickRules.put(Board.class, TickRules.GOLD_MINE_REMAINDER_GOES_TO_COFFER);
        tickRules.put(Board.class, INCREMENT_DAY_ON_TICK);
        tickRules.put(Council.class, new MetaTickActionRule<>((s, c) -> c.put(Attribute.CAN_BOUNTY, true)));
    }

    @Override
    public void registerConditionalRules(Ruleset ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(Tank.class, ConditionalRules.HANDLE_TANK_ON_ZERO_DURABILITY);
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);
        conditionalRules.put(Board.class, ConditionalRules.TANK_WIN_CONDITION);
    }

    @Override
    public void registerPlayerRules(Ruleset ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();
        playerRules.add(PlayerRules.BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT);
        playerRules.add(PlayerRules.getUpgradeRangeRule(Attribute.GOLD, 8));
        playerRules.add(PlayerRules.getShareGoldWithTaxToCofferRule(1));
        playerRules.add(PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1));
        playerRules.add(PlayerRules.SHOOT_V3);

        playerRules.add(PlayerRules.getCofferCostStimulusRule(3));
        playerRules.add(PlayerRules.getRuleCofferCostGrantLife(15, 3));
        playerRules.add(PlayerRules.getRuleCofferCostBounty(1, 5));
    }
}
