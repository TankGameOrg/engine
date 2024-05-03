package pro.trevor.tankgame.state.board.attribute;

public interface IDecoder<T, U> {

    T fromSource(U source);

}
