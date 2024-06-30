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
import pro.trevor.tankgame.rule.definition.range.UnitRange;
import pro.trevor.tankgame.rule.definition.range.DiscreteIntegerRange;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.impl.shared.rule.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.rule.TickRules;
import pro.trevor.tankgame.rule.impl.util.BaseRuleset;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.meta.ArmisticeCouncil;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.RulesetType;

import java.util.function.Function;

import static pro.trevor.tankgame.util.Util.toType;

@RulesetType(name = "version4")
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

        invariants.put(Council.class, new MinimumEnforcer<>(Council::getCoffer, Council::setCoffer, 0));
    }

    @Override
    public void registerTickRules(RulesetDescription ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        tickRules.put(Tank.class, TickRules.GetDistributeGoldToTanksRule());
        tickRules.put(Tank.class, TickRules.GetGrantActionPointsOnTickRule(1));

        tickRules.put(Board.class, TickRules.INCREMENT_DAY_ON_TICK);
        tickRules.put(Board.class, TickRules.GOLD_MINE_REMAINDER_GOES_TO_COFFER);
        tickRules.put(Council.class, TickRules.GetCouncilBaseIncomeRule(1, 3));
        tickRules.put(ArmisticeCouncil.class, TickRules.ARMISTICE_VIA_COUNCIL);
        tickRules.put(Council.class, new MetaTickActionRule<>((s, c) -> c.setCanBounty(true)));
    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.GetMoveRule(Attribute.ACTION_POINTS, 1), TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.SHOOT_V4, TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.GetShareGoldWithTaxRule(1), TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.BuyActionWithGold(3, 1), TIMEOUT));
        playerRules.put(Tank.class, new TimedPlayerActionRule<>(PlayerRules.GetUpgradeRangeRule(Attribute.GOLD, 5), TIMEOUT));

        playerRules.put(Council.class, PlayerRules.GetCofferCostStimulusRule(3));
        playerRules.put(Council.class, PlayerRules.GetRuleCofferCostGrantLife(15));
        playerRules.put(Council.class, new PlayerActionRule<>(PlayerRules.ActionKeys.BOUNTY,
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
                },
                UnitRange.ALL_LIVING_TANKS,
                new DiscreteIntegerRange("bounty", 1, 5)));
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(Tank.class, ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule());
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);

        conditionalRules.put(ArmisticeCouncil.class, ConditionalRules.ARMISTICE_COUNCIL_WIN_CONDITION);
        conditionalRules.put(Board.class, ConditionalRules.TANK_WIN_CONDITION);
    }
}
