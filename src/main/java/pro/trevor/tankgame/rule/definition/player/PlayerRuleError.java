package pro.trevor.tankgame.rule.definition.player;

public class PlayerRuleError {
    public static enum Category {
        NOT_APPLICABLE,         // The current action is not applicable to the current player's role i.e. tank, councilor, senator
        GENERIC,                // Any error that doesn't fit into one of the other categories
        INSUFFICIENT_DATA,      // The context did not include enough information to apply the action or complete all canApply checks
        INSUFFICENT_RESOURCES,  // The client needs to aquire more resources to perform this action
        COOLDOWN,               // The action is currently on cooldown
    }

    private Category category;
    private String message;

    public PlayerRuleError(Category category, String format, Object... formatArgs) {
        this.category = category;
        this.message = String.format(format, formatArgs);
    }

    public Category getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return String.format("PlayerRuleError %s: %s", category, message);
    }
}
