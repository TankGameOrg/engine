package rule.definition;

import rule.definition.ApplicableRuleset;
import rule.definition.enforcer.EnforcerRuleset;
import rule.definition.player.PlayerRuleset;

public class RulesetDescription {

    private final EnforcerRuleset enforcer;
    private final EnforcerRuleset metaEnforcer;
    private final ApplicableRuleset conditional;
    private final ApplicableRuleset metaConditional;
    private final ApplicableRuleset tick;
    private final ApplicableRuleset metaTick;
    private final PlayerRuleset player;
    private final PlayerRuleset metaPlayer;

    public RulesetDescription() {
        this.enforcer = new EnforcerRuleset();
        this.metaEnforcer = new EnforcerRuleset();
        this.conditional = new ApplicableRuleset();
        this.metaConditional = new ApplicableRuleset();
        this.tick = new ApplicableRuleset();
        this.metaTick = new ApplicableRuleset();
        this.player = new PlayerRuleset();
        this.metaPlayer = new PlayerRuleset();
    }

    public EnforcerRuleset getEnforcerRules() {
        return enforcer;
    }

    public EnforcerRuleset getMetaEnforcerRules() {
        return metaEnforcer;
    }

    public ApplicableRuleset getConditionalRules() {
        return conditional;
    }

    public ApplicableRuleset getMetaConditionalRules() {
        return metaConditional;
    }

    public ApplicableRuleset getTickRules() {
        return tick;
    }

    public ApplicableRuleset getMetaTickRules() {
        return metaTick;
    }

    public PlayerRuleset getPlayerRules() {
        return player;
    }

    public PlayerRuleset getMetaPlayerRules() {
        return metaPlayer;
    }
}
