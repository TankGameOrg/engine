package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.definition.player.TimedPlayerConditionRule;
import pro.trevor.tankgame.rule.impl.shared.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.LootTables;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.board.unit.LootBox;
import pro.trevor.tankgame.util.RulesetType;

import java.util.function.Function;

@RulesetType(name = "default-v5-experimental")
public class DefaultV5RulesetRegister extends BaseRulesetRegister implements IRulesetRegister {

    private static final Function<PlayerRuleContext, Long> TIMEOUT = (s) -> (long) (15); // 15 seconds

    @Override
    public void registerEnforcerRules(Ruleset ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 20));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.RANGE, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.GOLD, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.ACTION_POINTS, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Attribute.ACTION_POINTS, Attribute.MAX_ACTION_POINTS, 2));
        invariants.put(Tank.class, new MinimumEnforcer<>(Attribute.BOUNTY, 0));

        invariants.put(BasicWall.class, new MinimumEnforcer<>(Attribute.DURABILITY, 0));

        invariants.put(Player.class, new MinimumEnforcer<>(Attribute.POWER, 0));
    }

    @Override
    public void registerTickRules(Ruleset ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(Tank.class, TickRules.GetGrantActionPointsOnTickRule(1));
        tickRules.put(Tank.class, TickRules.DISTRIBUTE_GOLD_TO_TANKS);
        tickRules.put(Tank.class, TickRules.HEAL_TANK_IN_HEAL_POOL);
        tickRules.put(Tank.class, TickRules.DAMAGE_TANK_IN_LAVA);
        tickRules.put(Tank.class, TickRules.RELEASE_SPEED_MODIFICATIONS);
        tickRules.put(Tank.class, TickRules.CLEAR_ONLY_LOOTABLE_BY);
        tickRules.put(Tank.class, TickRules.SET_PLAYER_CAN_LOOT);
        tickRules.put(GenericElement.class, TickRules.DECAY_TIMEBOUND_ELEMENT);
        tickRules.put(Board.class, TickRules.spawnLootBoxInRandomSpace(4, 4, 2));

        tickRules.put(Board.class, TickRules.INCREMENT_DAY_ON_TICK);
        tickRules.put(Player.class, TickRules.DEAD_PLAYERS_GAIN_POWER);
    }

    @Override
    public void registerPlayerRules(Ruleset ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        playerRules.add(new TimedPlayerConditionRule(PlayerRules.PROPOSED_SHOOT_V5, TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1), TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.getShareGoldWithTaxRule(1), TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.buyActionWithGold(3, 1), TIMEOUT));
        playerRules.add(new TimedPlayerConditionRule(PlayerRules.getUpgradeRangeRule(Attribute.GOLD, 5), TIMEOUT));
        playerRules.add(PlayerRules.getLootRule(LootTables.V5_LOOT_BOX_LOOT));

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
        conditionalRules.put(Tank.class, ConditionalRules.HANDLE_TANK_ON_ZERO_DURABILITY);
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);
        conditionalRules.put(LootBox.class, ConditionalRules.DESTORY_EMPTY_LOOT_BOXES);

        conditionalRules.put(Board.class, ConditionalRules.TEAM_WIN_CONDITION);
    }
}
