package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
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
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.util.RulesetType;

import java.util.function.Function;

@RulesetType(name = "default-v5-experimental")
public class DefaultV5RulesetRegister extends BaseRulesetRegister implements IRulesetRegister {

    private static final Function<State, Long> TIMEOUT = (s) -> (long) (1 * 60); // 60 seconds

    @Override
    public void registerEnforcerRules(Ruleset ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));
        invariants.put(GenericTank.class, new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 20));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.RANGE, 0));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.GOLD, 0));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.ACTION_POINTS, 0));
        invariants.put(GenericTank.class, new MaximumEnforcer<>(Attribute.ACTION_POINTS, Attribute.MAX_ACTION_POINTS, 2));
        invariants.put(GenericTank.class, new MinimumEnforcer<>(Attribute.BOUNTY, 0));

        invariants.put(BasicWall.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));

        invariants.put(Player.class, new MinimumEnforcer<>(Attribute.POWER, 0));
    }

    @Override
    public void registerTickRules(Ruleset ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(GenericTank.class, TickRules.GetGrantActionPointsOnTickRule(1));
        tickRules.put(GenericTank.class, TickRules.DISTRIBUTE_GOLD_TO_TANKS);
        tickRules.put(GenericTank.class, TickRules.HEAL_TANK_IN_HEAL_POOL);
        tickRules.put(GenericTank.class, TickRules.DAMAGE_TANK_IN_LAVA);
        tickRules.put(GenericTank.class, TickRules.RELEASE_SPEED_MODIFICATIONS);
        tickRules.put(GenericTank.class, TickRules.CLEAR_ONLY_LOOTABLE_BY);
        tickRules.put(GenericTank.class, TickRules.SET_PLAYER_CAN_LOOT);

        tickRules.put(Board.class, TickRules.INCREMENT_DAY_ON_TICK);
        tickRules.put(Player.class, TickRules.DEAD_PLAYERS_GAIN_POWER);
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

        playerRules.add(PlayerRules.getSpawnWallWithCostRule(2, 2));
        playerRules.add(PlayerRules.getSpawnLavaWithCostRule(3, 2));
        playerRules.add(PlayerRules.getSmiteRule(4, 2));
        playerRules.add(PlayerRules.getHealRule(4, 2));
        playerRules.add(PlayerRules.getSlowRule(2, 1));
        playerRules.add(PlayerRules.getHastenRule(2, 1));
    }

    @Override
    public void registerConditionalRules(Ruleset ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(GenericTank.class, ConditionalRules.HANDLE_TANK_ON_ZERO_DURABILITY);
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);

        conditionalRules.put(Board.class, ConditionalRules.TEAM_WIN_CONDITION);
    }
}
