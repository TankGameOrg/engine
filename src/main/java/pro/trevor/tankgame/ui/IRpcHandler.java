package pro.trevor.tankgame.ui;

public interface IRpcHandler {
    /**
     * Indicate the rpc handler can accept more requests from the client
     */
    boolean canProcessRequests();
}
