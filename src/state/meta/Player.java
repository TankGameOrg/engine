package state.meta;

import rule.type.IMetaTickElement;

import java.util.Objects;

public record Player(String name) implements IMetaTickElement {

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

}
