package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;

public interface IRulesetRegister {

    void registerEnforcerRules(Ruleset ruleset);

    void registerTickRules(Ruleset ruleset);

    void registerConditionalRules(Ruleset ruleset);

    void registerPlayerRules(Ruleset ruleset);

    static Ruleset getRuleset(IRulesetRegister rulesetRegister) {
        Ruleset ruleset = new Ruleset(rulesetRegister::getPlayerObject);
        rulesetRegister.registerEnforcerRules(ruleset);
        rulesetRegister.registerTickRules(ruleset);
        rulesetRegister.registerConditionalRules(ruleset);
        rulesetRegister.registerPlayerRules(ruleset);
        return ruleset;
    }

    Object getPlayerObject(State state, PlayerRef playerRef);
}
