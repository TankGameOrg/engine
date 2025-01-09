package pro.trevor.tankgame.state.board;

import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.Position;

public interface IElement extends IJsonObject {
    char toBoardCharacter();
    Position getPosition();
    void setPosition(Position position);
}
