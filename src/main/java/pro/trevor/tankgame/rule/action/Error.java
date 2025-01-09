package pro.trevor.tankgame.rule.action;

import java.util.Objects;

public record Error(Type type, String message) {

    public static final Error NONE = new Error(null, "No error");

    public enum Type {
        PRECONDITION,
        INSUFFICIENT_RESOURCE,
        OTHER
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Error(Type otherType, String otherMessage))) return false;
        return type == otherType && Objects.equals(message, otherMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message);
    }
}
