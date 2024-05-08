package pro.trevor.tankgame.rule.impl.version4;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.definition.player.TimedPlayerActionRule;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.impl.shared.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.rule.impl.util.BaseRuleset;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.rule.impl.version3.range.TankRange;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.meta.ArmisticeCouncil;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.range.DiscreteIntegerRange;

import java.util.function.Function;

import static pro.trevor.tankgame.util.Util.toType;

public class Ruleset extends BaseRuleset implements IRuleset {

    private static final Function<State, Long> TIMEOUT = (s) -> (long) (5 * 60);

    @Override
    public void registerEnforcerRules(RulesetDescription ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getDurability, Tank::setDurability, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Tank::getDurability, Tank::setDurability, 3));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getRange, Tank::setRange, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getGold, Tank::setGold, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getActions, Tank::setActions, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Tank::getActions, Tank::setActions, 5));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getBounty, Tank::setBounty, 0));
        invariants.put(BasicWall.class, new MinimumEnforcer<>(BasicWall::getDurability, BasicWall::setDurability, 0));
    }

    @Override
    public void registerMetaEnforcerRules(RulesetDescription ruleset) {
        EnforcerRuleset invariants = ruleset.getMetaEnforcerRules();
        invariants.put(Council.class, new MinimumEnforcer<>(Council::getCoffer, Council::setCoffer, 0));
    }

    @Override
    public void registerTickRules(RulesetDescription ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(Tank.class, TickRules.DISTRIBUTE_GOLD_TO_TANKS_RULE);
    }

    @Override
    public void registerMetaTickRules(RulesetDescription ruleset) {
        ApplicableRuleset metaTickRules = ruleset.getMetaTickRules();

        metaTickRules.put(Board.class, TickRules.INCREMENT_DAY_ON_TICK);
        metaTickRules.put(Board.class, TickRules.GOLD_MINE_REMAINDER_GOES_TO_COFFER);
        metaTickRules.put(Council.class, TickRules.GetCouncilBaseIncomeRule(1, 3));
        metaTickRules.put(ArmisticeCouncil.class, TickRules.ARMISTICE_VIA_COUNCIL);
        metaTickRules.put(Council.class, new MetaTickActionRule<>((s, c) -> c.setCanBounty(true)));
    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        // Player Actions
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.SPEND_ACTION_TO_MOVE, TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.SHOOT_V4, TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.GetShareGoldWithTaxRule(1), TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.BuyActionWithGold(3, 1), TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.GetUpgradeRangeWithGoldRule(5), TIMEOUT));
    }

    @Override
    public void registerMetaPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset metaPlayerRules = ruleset.getMetaPlayerRules();

        metaPlayerRules.put(Council.class, PlayerRules.GetCofferCostStimulusRule(3));
        metaPlayerRules.put(Council.class, PlayerRules.GetRuleCofferCostGrantLife(15));
        metaPlayerRules.put(Council.class, new PlayerActionRule<>(PlayerRules.ActionKeys.BOUNTY,
                (s, c, n) -> {
                    Tank t = toType(n[0], Tank.class);
                    return !t.isDead() && c.canBounty();
                },
                (s, c, n) -> {
                    Tank t = toType(n[0], Tank.class);
                    int bounty = toType(n[1], Integer.class);
                    assert c.getCoffer() >= bounty;
                    t.setBounty(t.getBounty() + bounty);
                    c.setCoffer(c.getCoffer() - bounty);
                    c.setCanBounty(false);
                }, new TankRange<Council>("target"), new DiscreteIntegerRange("bounty", 1, 5))
        );
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(Tank.class, ConditionalRules.KILL_OR_DESTROY_TANK_ON_ZERO_DURABILITY);
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);
    }

    @Override
    public void registerMetaConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset metaConditionalRules = ruleset.getMetaConditionalRules();
        metaConditionalRules.put(ArmisticeCouncil.class, ConditionalRules.ARMISTICE_COUNCIL_WIN_CONDITION);
        metaConditionalRules.put(Board.class, ConditionalRules.TANK_WIN_CONDITION);
    }
}
