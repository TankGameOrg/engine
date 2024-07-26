package pro.trevor.tankgame.rule.definition;

public enum Priority {
    HIGHEST(0),
    HIGHER(1),
    HIGH(2),
    DEFAULT(3),
    LOW(4),
    LOWER(5),
    LOWEST(6);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int numericValue() {
        return priority;
    }
}
