package pro.trevor.tankgame.rule.definition.player;

public class PlayerRuleError {
    public static enum Category {
        NOT_APPLICABLE,
        GENERIC,
        INSUFFICIENT_DATA,
        INSUFFICENT_RESOURCES,
    }

    // For each category define a static helper to make life easier

    public static PlayerRuleError notApplicable(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.NOT_APPLICABLE, format, formatArgs);
    }

    public static PlayerRuleError generic(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.GENERIC, format, formatArgs);
    }

    public static PlayerRuleError insufficientData(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.INSUFFICIENT_DATA, format, formatArgs);
    }

    public static PlayerRuleError insufficientResources(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.INSUFFICENT_RESOURCES, format, formatArgs);
    }

    Category category;
    String message;

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
