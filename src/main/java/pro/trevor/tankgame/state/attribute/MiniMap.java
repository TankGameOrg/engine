package pro.trevor.tankgame.state.attribute;

import java.util.*;

public class MiniMap implements Map<Attribute<?>, Object> {

    public static class Node {
        private final Attribute<?> key;
        private Object value;
        private Node next;

        public Node(Attribute<?> key, Object value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public Attribute<?> getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object exchangeValue(Object value) {
            Object output = this.value;
            this.value = value;
            return output;
        }

        public Entry<Attribute<?>, Object> getEntry() {
            return Map.entry(key, value);
        }

        public Node next() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

    private Node first;

    public MiniMap() {
        first = null;
    }

    @Override
    public int size() {
        int count = 0;
        Node current = first;
        while (current != null) {
            current = current.next;
            ++count;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (Objects.isNull(key) || !(key instanceof Attribute<?>)) {
            return false;
        }

        Node current = first;
        while (current != null) {
            if (current.getKey().equals(key)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (Objects.isNull(value)) {
            return false;
        }

        Node current = first;
        while (current != null) {
            if (current.getValue().equals(value)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    @Override
    public Object get(Object key) {
        if (Objects.isNull(key) || !(key instanceof Attribute<?>)) {
            return false;
        }

        Node current = first;
        while (current != null) {
            if (current.getKey().equals(key)) {
                return current.getValue();
            }
            current = current.next;
        }

        return null;
    }

    @Override
    public Object put(Attribute<?> key, Object value) {
        if (Objects.isNull(key)) {
            return false;
        }

        Node current = first;
        Node prev = first;
        while (current != null) {
            if (current.getKey().equals(key)) {
                return current.exchangeValue(value);
            }
            prev = current;
            current = current.next;
        }

        if (prev == null) {
            first = new Node(key, value);
        } else {
            prev.next = new Node(key, value);
        }

        return null;
    }

    @Override
    public Object remove(Object key) {
        if (Objects.isNull(key)) {
            return false;
        }

        Node current = first;
        Node prev = first;
        while (current != null) {
            if (current.getKey().equals(key)) {
                Node next = current.next;
                prev.setNext(next);
                return current.getValue();
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    @Override
    public void putAll(Map<? extends Attribute<?>, ?> m) {
        for (Map.Entry<? extends Attribute<?>, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        first = null;
    }

    @Override
    public Set<Attribute<?>> keySet() {
        Set<Attribute<?>> set = new HashSet<>();
        Node current = first;
        while (current != null) {
            set.add(current.getKey());
            current = current.next;
        }
        return set;
    }

    @Override
    public Collection<Object> values() {
        List<Object> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.getValue());
            current = current.next;
        }
        return list;
    }

    @Override
    public Set<Entry<Attribute<?>, Object>> entrySet() {
        Set<Entry<Attribute<?>, Object>> set = new HashSet<>();
        Node current = first;
        while (current != null) {
            set.add(current.getEntry());
            current = current.next;
        }
        return set;
    }
}
