package pro.trevor.tankgame.state.board;

import pro.trevor.tankgame.util.IJsonObject;

public interface IElement extends IJsonObject {
    char toBoardCharacter();
    Position getPosition();
    void setPosition(Position position);
}
