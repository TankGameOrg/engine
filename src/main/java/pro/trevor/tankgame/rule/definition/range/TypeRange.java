package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.util.IJsonObject;

public interface TypeRange<T> extends IJsonObject {

    Class<T> getBoundClass();
    String getJsonDataType();
    String getName();

}
