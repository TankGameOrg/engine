package state.meta;

import rule.type.IMetaElement;

import java.util.Objects;

public record Player(String name) implements IMetaElement {

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
