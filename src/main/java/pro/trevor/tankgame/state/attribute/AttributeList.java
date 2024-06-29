package pro.trevor.tankgame.state.attribute;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonType(name = "AttributeList")
public class AttributeList<T extends IJsonObject> implements IJsonObject {

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
            elements.add((T) array.get(i));
        }
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean contains(T element) {
        return elements.contains(element);
    }

    public T get(int index) {
        return elements.get(index);
    }

    public boolean add(T element) {
        return elements.add(element);
    }

    public void add(T element, int index) {
        elements.add(index, element);
    }

    public T remove(int index) {
        return elements.remove(index);
    }

    public boolean remove(Object element) {
        return elements.remove(element);
    }

    public void clear() {
        elements.clear();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        elements.forEach((e) -> array.put(e.toJson()));
        json.put("elements", array);
        return json;
    }
}
