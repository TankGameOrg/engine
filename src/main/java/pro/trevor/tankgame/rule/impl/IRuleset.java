package pro.trevor.tankgame.rule.impl;

import pro.trevor.tankgame.rule.definition.RulesetDescription;

public interface IRuleset {

    void registerEnforcerRules(RulesetDescription ruleset);

    void registerMetaEnforcerRules(RulesetDescription ruleset);

    void registerTickRules(RulesetDescription ruleset);

    void registerMetaTickRules(RulesetDescription ruleset);

    void registerConditionalRules(RulesetDescription ruleset);

    void registerMetaConditionalRules(RulesetDescription ruleset);

    void registerPlayerRules(RulesetDescription ruleset);

    void registerMetaPlayerRules(RulesetDescription ruleset);

    static RulesetDescription getRuleset(IRuleset ruleset) {
        RulesetDescription rulesetDescription = new RulesetDescription();
        ruleset.registerEnforcerRules(rulesetDescription);
        ruleset.registerMetaEnforcerRules(rulesetDescription);
        ruleset.registerTickRules(rulesetDescription);
        ruleset.registerMetaTickRules(rulesetDescription);
        ruleset.registerConditionalRules(rulesetDescription);
        ruleset.registerMetaConditionalRules(rulesetDescription);
        ruleset.registerPlayerRules(rulesetDescription);
        ruleset.registerMetaPlayerRules(rulesetDescription);
        return rulesetDescription;
    }
}
