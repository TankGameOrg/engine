package pro.trevor.tankgame.rule.impl.version4;

import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.util.BaseRuleset;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.util.LineOfSight;

public class Ruleset extends BaseRuleset implements IRuleset {

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        playerRules.put(Tank.class, PlayerRules.SpendActionToShootWithDeathHandle(LineOfSight::hasLineOfSightV4,
            (s, t, d) -> {
                t.setGold(t.getGold() + d.getBounty());
                switch (d.getGold()) {
                    case 0 -> {}
                    case 1 -> t.setGold(t.getGold() + 1);
                    default -> {
                        // Tax is target tank gold * 0.25 rounded up
                        int tax = (d.getGold() + 3) / 4;
                        t.setGold(t.getGold() + d.getGold() - tax);
                        s.getCouncil().setCoffer(s.getCouncil().getCoffer() + tax);
                    }
                }
        }));
    }
}
