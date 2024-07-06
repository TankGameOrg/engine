package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.impl.util.BaseRuleset;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.impl.shared.rule.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.rule.TickRules;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.RulesetType;

import static pro.trevor.tankgame.rule.impl.shared.rule.TickRules.INCREMENT_DAY_ON_TICK;

@RulesetType(name = "default-v3")
public class Ruleset extends BaseRuleset implements IRuleset {

    @Override
    public void registerEnforcerRules(RulesetDescription ruleset) {
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
    public void registerTickRules(RulesetDescription ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(Tank.class, TickRules.GetDistributeGoldToTanksRule());
        tickRules.put(Tank.class, TickRules.GetGrantActionPointsOnTickRule(1));
        tickRules.put(Board.class, TickRules.GOLD_MINE_REMAINDER_GOES_TO_COFFER);
        tickRules.put(Board.class, INCREMENT_DAY_ON_TICK);
        tickRules.put(Council.class, new MetaTickActionRule<>((s, c) -> Attribute.CAN_BOUNTY.to(c, true)));
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(Tank.class, ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule());
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);
        conditionalRules.put(Board.class, ConditionalRules.TANK_WIN_CONDITION);
    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();
        playerRules.put(GenericTank.class, PlayerRules.BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT);
        playerRules.put(Tank.class, PlayerRules.GetUpgradeRangeRule(Attribute.GOLD, 8));
        playerRules.put(Tank.class, PlayerRules.GetShareGoldWithTaxRule(1));
        playerRules.put(Tank.class, PlayerRules.GetMoveRule(Attribute.ACTION_POINTS, 1));
        playerRules.put(GenericTank.class, PlayerRules.SHOOT_V3);

        playerRules.put(Council.class, PlayerRules.GetCofferCostStimulusRule(3));
        playerRules.put(Council.class, PlayerRules.GetRuleCofferCostGrantLife(15, 3));
        playerRules.put(Council.class, PlayerRules.GetRuleCofferCostBounty(1, 5));
    }
}
