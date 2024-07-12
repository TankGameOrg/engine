package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.Ruleset;

public interface IRulesetRegister {

    void registerEnforcerRules(Ruleset ruleset);

    void registerTickRules(Ruleset ruleset);

    void registerConditionalRules(Ruleset ruleset);

    void registerPlayerRules(Ruleset ruleset);

    static Ruleset getRuleset(IRulesetRegister rulesetRegister) {
        Ruleset ruleset = new Ruleset();
        rulesetRegister.registerEnforcerRules(ruleset);
        rulesetRegister.registerTickRules(ruleset);
        rulesetRegister.registerConditionalRules(ruleset);
        rulesetRegister.registerPlayerRules(ruleset);
        return ruleset;
    }
}
