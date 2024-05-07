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

        playerRules.put(Tank.class, PlayerRules.SHOOT_V4);
    }
}
