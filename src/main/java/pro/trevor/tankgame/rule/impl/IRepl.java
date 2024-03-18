package pro.trevor.tankgame.rule.impl;

import java.io.InputStream;

public interface IRepl {

    void initialize(InputStream initialState, InputStream moves);
    void handleTick();
    void handleLine();
    boolean isDone();

}
