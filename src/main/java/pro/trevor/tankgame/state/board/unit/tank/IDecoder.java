package pro.trevor.tankgame.state.board.unit.tank;

public interface IDecoder<T, U> {

    T fromSource(U source);

}
