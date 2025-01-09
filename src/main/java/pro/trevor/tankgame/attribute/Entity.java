package pro.trevor.tankgame.attribute;

import java.util.stream.Stream;

public interface Entity {

    Stream<Object> gather();
    default <T> Stream<T> gather(Class<T> type) {
        return  gather().filter(type::isInstance).map(type::cast);
    }

}
