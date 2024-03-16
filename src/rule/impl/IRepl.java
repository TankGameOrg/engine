package rule.impl;

import state.State;

import java.io.InputStream;

public interface IRepl {

    void initialize(InputStream input);
    void handleTick();
    void handleLine();

}
