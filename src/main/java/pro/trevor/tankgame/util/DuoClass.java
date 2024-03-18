package pro.trevor.tankgame.util;

import java.util.Objects;

public class DuoClass<T, U> {

    private final Class<T> leftClass;
    private final Class<U> rightClass;

    public DuoClass(Class<T> leftClass, Class<U> rightClass) {
        this.leftClass = leftClass;
        this.rightClass = rightClass;
    }

    public Class<T> getLeftClass() {
        return leftClass;
    }

    public Class<U> getRightClass() {
        return rightClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuoClass<?, ?> duoClass = (DuoClass<?, ?>) o;
        return Objects.equals(leftClass, duoClass.leftClass) && Objects.equals(rightClass, duoClass.rightClass);
    }

    @Override
    public int hashCode() {
        return leftClass.hashCode() ^ rightClass.hashCode();
    }
}
