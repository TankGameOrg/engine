package state.board.unit;

public interface IDurable {

    int getDurability();

    void setDurability(int durability);

    void handleDestruction();

}
