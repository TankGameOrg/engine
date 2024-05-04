package pro.trevor.tankgame.rule.impl.util;

import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.impl.IRuleset;

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
