package pro.trevor.tankgame.rule.impl;

import pro.trevor.tankgame.rule.definition.RulesetDescription;

public interface IRuleset {

    void registerEnforcerRules(RulesetDescription ruleset);

    void registerTickRules(RulesetDescription ruleset);

    void registerConditionalRules(RulesetDescription ruleset);

    void registerPlayerRules(RulesetDescription ruleset);

    static RulesetDescription getRuleset(IRuleset ruleset) {
        RulesetDescription rulesetDescription = new RulesetDescription();
        ruleset.registerEnforcerRules(rulesetDescription);
        ruleset.registerTickRules(rulesetDescription);
        ruleset.registerConditionalRules(rulesetDescription);
        ruleset.registerPlayerRules(rulesetDescription);
        return rulesetDescription;
    }
}
