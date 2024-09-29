package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;

public class Ruleset implements Cloneable {

    private EnforcerRuleset enforcer;
    private ApplicableRuleset conditional;
    private ApplicableRuleset tick;
    private PlayerRuleset player;

    public Ruleset() {
        this.enforcer = new EnforcerRuleset();
        this.conditional = new ApplicableRuleset();
        this.tick = new ApplicableRuleset();
        this.player = new PlayerRuleset();
    }

    private Ruleset(EnforcerRuleset enforcer, ApplicableRuleset conditional, ApplicableRuleset tick, PlayerRuleset player) {
        this.enforcer = enforcer;
        this.conditional = conditional;
        this.tick = tick;
        this.player = player;
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

    @Override
    public Ruleset clone() {
        try {
            Ruleset clone = (Ruleset) super.clone();
            clone.enforcer = this.enforcer.clone();
            clone.conditional = this.conditional.clone();
            clone.tick = this.tick.clone();
            clone.player = this.player.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
