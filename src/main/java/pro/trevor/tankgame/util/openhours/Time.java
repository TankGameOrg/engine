package pro.trevor.tankgame.util.openhours;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "Time")
public record Time(int hour, int minute) implements IJsonObject {

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    public Time(JSONObject json) {
        this(json.getInt(HOUR), json.getInt(MINUTE));
    }

    public Time(String string) {
        this(Time.hourFromString(string), Time.minuteFromString(string));
    }

    private static int hourFromString(String string) {
        int hour = Integer.parseInt(string.substring(0, string.indexOf(':')));
        if (string.endsWith("pm")) {
            hour += 12;
        }
        return hour;
    }

    private static int minuteFromString(String string) {
        int colonIndex = string.indexOf(':');
        return Integer.parseInt(string.substring(colonIndex + 1, colonIndex + 3));
    }

    public boolean isBefore(Time other) {
        return hour < other.hour || hour == other.hour && minute < other.minute;
    }

    public boolean isBetween(Time first, Time second) {
        return first.isBefore(this) && this.isBefore(second);
    }

    @Override
    public String toString() {
        boolean am = hour < 12;
        int hourMod = hour % 12;
        if (hourMod == 0) {
            hourMod = 12;
        }
        return String.format("%d:%02d%s", hourMod, minute, am ? "am" : "pm");
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(HOUR, hour);
        json.put(MINUTE, minute);
        json.put("class", Codec.typeFromClass(getClass()));
        return json;
    }
}
