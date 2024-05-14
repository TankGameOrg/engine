package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.ApplicableRuleset;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.impl.util.BaseRuleset;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.impl.shared.range.TankRange;
import pro.trevor.tankgame.rule.impl.shared.rule.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.shared.rule.TickRules;
import pro.trevor.tankgame.state.attribute.Attributes;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.range.DiscreteIntegerRange;

import static pro.trevor.tankgame.util.Util.*;

public class Ruleset extends BaseRuleset implements IRuleset {

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

        tickRules.put(Tank.class, TickRules.GetDistributeGoldToTanksRule());
        tickRules.put(Tank.class, TickRules.GetGrantActionPointsOnTickRule(1));
    }

    @Override
    public void registerMetaTickRules(RulesetDescription ruleset) {
        ApplicableRuleset metaTickRules = ruleset.getMetaTickRules();

        metaTickRules.put(Board.class, TickRules.GOLD_MINE_REMAINDER_GOES_TO_COFFER);
        metaTickRules.put(Council.class, new MetaTickActionRule<>((s, c) -> {
            c.setCanBounty(true);
            s.setTick(s.getTick() + 1);
        }));
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();
        conditionalRules.put(Tank.class, ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule());
        conditionalRules.put(BasicWall.class, ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY);
    }

    @Override
    public void registerMetaConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset metaConditionalRules = ruleset.getMetaConditionalRules();

        metaConditionalRules.put(Board.class, ConditionalRules.TANK_WIN_CONDITION);
    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();
        playerRules.put(Tank.class, PlayerRules.BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT);
        playerRules.put(Tank.class, PlayerRules.GetUpgradeRangeRule(Attributes.GOLD, 8));
        playerRules.put(Tank.class, PlayerRules.GetShareGoldWithTaxRule(1));
        playerRules.put(Tank.class, PlayerRules.GetMoveRule(Attributes.ACTION_POINTS, 1));
        playerRules.put(Tank.class, PlayerRules.SHOOT_V3);
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
                }, new TankRange<Council>("target"), new DiscreteIntegerRange("bounty", 1, 5)));
    }
}
