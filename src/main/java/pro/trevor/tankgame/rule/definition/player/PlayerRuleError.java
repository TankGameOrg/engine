package pro.trevor.tankgame.rule.definition.player;

import java.util.Optional;

public class PlayerRuleError {
    public static enum Category {
        NOT_APPLICABLE,
        GENERIC,
        INSUFFICIENT_DATA,
        INSUFFICENT_RESOURCES,
        COOLDOWN,
        FATAL,
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

    public static PlayerRuleError fatal(String format, Object... formatArgs) {
        return new PlayerRuleError(Category.FATAL, format, formatArgs);
    }

    public static PlayerRuleError cooldown(long cooldownEnd, String format, Object... formatArgs) {
        return new PlayerRuleError(Category.COOLDOWN, cooldownEnd, format, formatArgs);
    }

    Category category;
    String message;
    Optional<Long> errorExpirationTime;

    public PlayerRuleError(Category category, String format, Object... formatArgs) {
        this.category = category;
        this.message = String.format(format, formatArgs);
        errorExpirationTime = Optional.empty();
    }

    public PlayerRuleError(Category category, long expirationTime, String format, Object... formatArgs) {
        this.category = category;
        this.message = String.format(format, formatArgs);
        this.errorExpirationTime = Optional.of(expirationTime);
    }

    public Category getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public Optional<Long> getErrorExpirationTime() {
        return errorExpirationTime;
    }

    public String toString() {
        return String.format("PlayerRuleError %s: %s", category, message);
    }
}
