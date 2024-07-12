package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;

public class Ruleset {

    private final EnforcerRuleset enforcer;
    private final ApplicableRuleset conditional;
    private final ApplicableRuleset tick;
    private final PlayerRuleset player;

    public Ruleset() {
        this.enforcer = new EnforcerRuleset();
        this.conditional = new ApplicableRuleset();
        this.tick = new ApplicableRuleset();
        this.player = new PlayerRuleset();
    }

    public EnforcerRuleset getEnforcerRules() {
        return enforcer;
    }

    public ApplicableRuleset getConditionalRules() {
        return conditional;
    }

    public ApplicableRuleset getTickRules() {
        return tick;
    }

    public PlayerRuleset getPlayerRules() {
        return player;
    }
}
