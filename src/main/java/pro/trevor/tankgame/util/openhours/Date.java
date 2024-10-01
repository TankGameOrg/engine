package pro.trevor.tankgame.util.openhours;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.Calendar;

@JsonType(name = "Date")
public record Date(int month, int day) implements IJsonObject {

    public static final String MONTH = "month";
    public static final String DAY = "day";

    public Date(JSONObject json) {
        this(json.getInt(MONTH), json.getInt(DAY));
    }

    public Date(String string) {
        this(Date.monthFromString(string), Date.dayFromString(string));
    }

    private static int monthFromString(String string) {
        return Integer.parseInt(string.substring(0, string.indexOf('/')));
    }

    private static int dayFromString(String string) {
        return Integer.parseInt(string.substring(string.indexOf('/') + 1));
    }

    public boolean isDay(Calendar calendar) {
        return (calendar.get(Calendar.MONTH) + 1) == month && calendar.get(Calendar.DAY_OF_MONTH) == day;
    }

    @Override
    public String toString() {
        return month + "/" + day;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(MONTH, month);
        json.put(DAY, day);
        json.put("class", Codec.typeFromClass(getClass()));
        return json;
    }
}