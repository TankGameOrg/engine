package pro.trevor.tankgame.util.range;

import pro.trevor.tankgame.util.IJsonObject;

public interface TypeRange<T> extends IJsonObject {

    Class<T> getBoundClass();

    String getName();

}
