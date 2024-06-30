package pro.trevor.tankgame.util;

import java.util.List;

public interface IGatherable {

    <T> List<T> gather(Class<T> type);

    List<Object> gatherAll();

}
