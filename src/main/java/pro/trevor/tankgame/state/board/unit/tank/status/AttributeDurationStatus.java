package pro.trevor.tankgame.state.board.unit.tank.status;

import pro.trevor.tankgame.state.board.unit.tank.IAttribute;

public abstract class AttributeDurationStatus<E extends Enum<E> & IAttribute, T> implements IAttributeStatus<E, T>, IDurationStatus {

    protected E attribute;
    protected int duration;

    public AttributeDurationStatus(E attribute, int duration) {
        this.attribute = attribute;
        this.duration = duration;
    }

    @Override
    public E attributeEffected() {
        return attribute;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void decrementDuration() {
        duration = duration - 1;
    }
}
