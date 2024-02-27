package state.board;

public class NullUnit implements IUnit {

    public NullUnit() {
    }

    @Override
    public Position getPosition() {
        throw new Error("Attempted to get position of null unit");
    }
}
