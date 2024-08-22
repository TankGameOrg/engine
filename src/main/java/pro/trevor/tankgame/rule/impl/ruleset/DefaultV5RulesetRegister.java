package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.definition.player.TimedPlayerConditionRule;
import pro.trevor.tankgame.rule.impl.shared.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.RulesetType;

import java.util.function.Function;

@RulesetType(name = "default-v5-experimental")
public class DefaultV5RulesetRegister extends BaseRulesetRegister implements IRulesetRegister {

    private static final Function<State, Long> TIMEOUT = (s) -> (long) (1 * 60);

    @Override
    public void registerEnforcerRules(Ruleset ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));
        invariants.put(GenericTank.class, new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 3));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.RANGE, 0));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.GOLD, 0));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.ACTION_POINTS, 0));
        invariants.put(GenericTank.class, new MaximumEnforcer<>(Attribute.ACTION_POINTS, Attribute.MAX_ACTION_POINTS, 3));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.BOUNTY, 0));
        invariants.put(BasicWall.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));
        invariants.put(Council.class, new MinimumEnforcer<>(Attribute.COFFER, 0));
    }

    @Override
    public void registerTickRules(Ruleset ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(GenericTank.class, TickRules.GetDistributeGoldToTanksRule());
        tickRules.put(GenericTank.class, TickRules.GetGrantActionPointsOnTickRule(1));
        tickRules.put(GenericTank.class, TickRules.GetHealTanksInHealthPoolRule());
        tickRules.put(GenericTank.class, TickRules.CLEAR_ONLY_LOOTABLE_BY);
        tickRules.put(GenericTank.class, TickRules.SET_PLAYER_CAN_LOOT);

        tickRules.put(Board.class, TickRules.INCREMENT_DAY_ON_TICK);
        tickRules.put(Board.class, TickRules.GOLD_MINE_REMAINDER_GOES_TO_COFFER);
        tickRules.put(Council.class, TickRules.GetCouncilBaseIncomeRule(1, 3));
        tickRules.put(Council.class, new MetaTickActionRule<>((s, c) -> Attribute.CAN_BOUNTY.to(c, true)));
    }

    @Override
    public void registerPlayerRules(Ruleset ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        playerRules.add(new TimedPlayerConditionRule(PlayerRules.SHOOT_V5, TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1), TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.getShareGoldWithTaxRule(1), TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.buyActionWithGold(3, 1), TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.getUpgradeRangeRule(Attribute.GOLD, 5), TIMEOUT));
        playerRules.add(PlayerRules.LOOT_GOLD_FROM_DEAD_TANK);

        playerRules.add(PlayerRules.getCofferCostStimulusRule(3));
        playerRules.add(PlayerRules.getRuleCofferCostGrantLife(15, 3));
        playerRules.add(PlayerRules.getRuleCofferCostBounty(1, 5));
    }

    @Override
    public void registerConditionalRules(Ruleset ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(GenericTank.class, ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule());
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);

        conditionalRules.put(Board.class, ConditionalRules.TANK_WIN_CONDITION);
    }
}
