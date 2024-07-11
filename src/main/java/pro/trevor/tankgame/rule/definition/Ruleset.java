package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.List;
import java.util.function.BiFunction;

public class Ruleset {

    private final BiFunction<State, PlayerRef, Object> playerDereferencer;
    private final EnforcerRuleset enforcer;
    private final ApplicableRuleset conditional;
    private final ApplicableRuleset tick;
    private final PlayerRuleset player;

    public Ruleset(BiFunction<State, PlayerRef, Object> playerDereferencer) {
        this.playerDereferencer = playerDereferencer;
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

    public Object getPlayerObject(State state, PlayerRef playerRef) {
        return playerDereferencer.apply(state, playerRef);
    }
}
