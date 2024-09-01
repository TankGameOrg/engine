package pro.trevor.tankgame.rule.definition.player;

public class PlayerRuleError {
    public static enum Category {
        NOT_APPLICABLE,
        GENERIC,
        INSUFFICIENT_DATA,
        INSUFFICENT_RESOURCES,
        COOLDOWN,
    }

    // For each category define a static helper to make life easier

    /**
     * The current action is not applicable to the current player's role i.e. tank, councilor, senator
     * @param format The message to go with the error as a format string
     * @param formatArgs The arguments for the message format string
     */
    public static PlayerRuleError notApplicable(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.NOT_APPLICABLE, format, formatArgs);
    }

    /**
     * Any error that doesn't fit into one of the other categories
     * @param format The message to go with the error as a format string
     * @param formatArgs The arguments for the message format string
     */
    public static PlayerRuleError generic(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.GENERIC, format, formatArgs);
    }

    /**
     * The context did not include enough information to apply the action or complete all canApply checks
     * @param format The message to go with the error as a format string
     * @param formatArgs The arguments for the message format string
     */
    public static PlayerRuleError insufficientData(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.INSUFFICIENT_DATA, format, formatArgs);
    }

    /**
     * The client needs to aquire more resources to perform this action
     * @param format The message to go with the error as a format string
     * @param formatArgs The arguments for the message format string
     */
    public static PlayerRuleError insufficientResources(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.INSUFFICENT_RESOURCES, format, formatArgs);
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
