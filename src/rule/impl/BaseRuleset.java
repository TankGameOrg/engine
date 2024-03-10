package rule.impl;

import rule.definition.RulesetDescription;

/**
 * This is a basic empty ruleset so that versions need not implement functions that will be empty.
 */
public abstract class BaseRuleset implements IRuleset {
    @Override
    public void registerEnforcerRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerMetaEnforcerRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerTickRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerMetaTickRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerMetaConditionalRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {

    }

    @Override
    public void registerMetaPlayerRules(RulesetDescription ruleset) {

    }
}
