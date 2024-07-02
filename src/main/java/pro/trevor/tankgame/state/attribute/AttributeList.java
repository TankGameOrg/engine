package pro.trevor.tankgame.state.attribute;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.util.IGatherable;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;
import pro.trevor.tankgame.util.Util;

import java.util.*;

@JsonType(name = "AttributeList")
public class AttributeList<T> implements Collection<T>, IJsonObject, IGatherable {

    private final List<T> elements;

    public AttributeList() {
        this.elements = new ArrayList<>();
    }

    public AttributeList(Collection<T> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public AttributeList(JSONObject json) {
        this.elements = new ArrayList<>();
        JSONArray array = json.optJSONArray("elements");
        for (int i = 0; i < array.length(); ++i) {
            Object fromJson = array.get(i);
            if (fromJson instanceof JSONObject jsonObject) {
                elements.add((T) Codec.decodeJson(jsonObject));
            } else {
                // Assume we are working with a primitive or String
                elements.add((T) fromJson);
            }
        }
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public int indexOf(T element) {
        return elements.indexOf(element);
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    public T get(int index) {
        return elements.get(index);
    }

    public boolean add(T element) {
        return elements.add(element);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(elements).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return elements.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c);
    }

    public void add(T element, int index) {
        elements.add(index, element);
    }

    public T remove(int index) {
        return elements.remove(index);
    }

    public void clear() {
        elements.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <U> U[] toArray(U[] a) {
        return elements.toArray(a);
    }

    @Override
    public String toString() {
        return Util.toString(elements);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        elements.forEach((e) -> {
            if (e instanceof IJsonObject jsonObject) {
                array.put(jsonObject.toJson());
            } else {
                array.put(e);
            }
        });
        json.put("elements", array);
        return json;
    }

    @Override
    public <U> List<U> gather(Class<U> type) {
        List<U> result = new ArrayList<>();
        for (Object value : elements) {
            if (value instanceof IGatherable gatherable) {
                result.addAll(gatherable.gather(type));
            }
            if (type.isAssignableFrom(value.getClass())) {
                result.add((U) value);
            }
        }
        return result;
    }

    @Override
    public List<Object> gatherAll() {
        List<Object> result = new ArrayList<>();
        for (Object value : elements) {
            result.add(value);
            if (value instanceof IGatherable gatherable) {
                result.addAll(gatherable.gatherAll());
            }
        }
        return result;
    }
}
