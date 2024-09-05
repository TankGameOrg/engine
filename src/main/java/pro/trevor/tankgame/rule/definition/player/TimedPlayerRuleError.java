package pro.trevor.tankgame.rule.definition.player;

public class TimedPlayerRuleError extends PlayerRuleError {
    private long errorExpirationTime;

    public TimedPlayerRuleError(Category category, long expirationTime, String format, Object... formatArgs) {
        super(category, format, formatArgs);
        this.errorExpirationTime = expirationTime;
    }

    /**
     * Get a unix timestamp of when the error is no longer applciable i.e. cooldown end
     */
    public long getErrorExpirationTime() {
        return errorExpirationTime;
    }
}
