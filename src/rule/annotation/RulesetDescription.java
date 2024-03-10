package rule.annotation;

import rule.definition.ApplicableRuleset;
import rule.definition.enforcer.EnforcerRuleset;
import rule.definition.player.PlayerRuleset;

public class RulesetDescription {

    private final EnforcerRuleset enforcer;
    private final ApplicableRuleset conditional;
    private final ApplicableRuleset tick;
    private final PlayerRuleset player;

    public RulesetDescription() {
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
