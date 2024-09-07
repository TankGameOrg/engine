package pro.trevor.tankgame.rule.definition.actions;

import java.util.List;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;

public class PossibleAction {
    private String ruleName;
    private String description;
    private List<PlayerRuleError> errors;
    private List<LogFieldSpec<?>> fieldSpecs;

    public PossibleAction(String ruleName, String description, List<PlayerRuleError> errors, List<LogFieldSpec<?>> fieldSpecs) {
        this.ruleName = ruleName;
        this.errors = errors;
        this.fieldSpecs = fieldSpecs;
        this.description = description;
    }

    public String getRuleName() {
        return ruleName;
    }

    public List<PlayerRuleError> getErrors() {
        return errors;
    }

    public List<LogFieldSpec<?>> getFieldSpecs() {
        return fieldSpecs;
    }

    public String getDescription() {
        return description;
    }
}
