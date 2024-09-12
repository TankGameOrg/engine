package pro.trevor.tankgame.e2e;

import java.util.Optional;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.state.attribute.Attribute;

public class LinkedAttributeList<T> {
    private Attribute<T> attribute;
    private T value;
    private Optional<LinkedAttributeList<?>> next;

    public LinkedAttributeList(Attribute<T> attribute, T value) {
        this.attribute = attribute;
        this.value = value;
        this.next = Optional.empty();
    }

    private LinkedAttributeList(Attribute<T> attribute, T value, LinkedAttributeList<?> next) {
        this.attribute = attribute;
        this.value = value;
        this.next = Optional.of(next);
    }

    public <E> LinkedAttributeList<E> with(Attribute<E> attribute, E value) {
        return new LinkedAttributeList<E>(attribute, value, this);
    }

    private void addAttributeTo(LogEntry logEntry) {
        logEntry.put(attribute, value);
    }

    public LogEntry buildLogEntry() {
        LogEntry logEntry = new LogEntry();

        Optional<LinkedAttributeList<?>> optionalNode = Optional.of(this);
        while(optionalNode.isPresent()) {
            optionalNode.get().addAttributeTo(logEntry);
            optionalNode = optionalNode.get().next;
        }

        return logEntry;
    }
}
