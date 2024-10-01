package pro.trevor.tankgame.util.openhours;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@JsonType(name = "OpenHours")
public class OpenHours implements IJsonObject {

    private static final String SCHEDULES = "schedules";
    private static final String RESOLVED = "resolved";
    private static final String IS_GAME_OPEN = "isGameOpen";
    private static final String CURRENT_TIME = "currentTime";

    private final List<Schedule> schedules;

    public OpenHours(JSONObject json) {
        this.schedules = new ArrayList<>();
        JSONArray schedulesArray = json.getJSONArray(SCHEDULES);
        for (int i = 0; i < schedulesArray.length(); i++) {
            this.schedules.add(new Schedule(schedulesArray.getJSONObject(i)));
        }

        // Do not parse the resolved section
    }

    public OpenHours(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public OpenHours() {
        this.schedules = List.of();
    }

    public long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    public String getCurrentTimeString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getCurrentTimeMs());
        return new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)).toString();
    }

    public boolean isGameOpen() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getCurrentTimeMs());
        for (Schedule schedule : schedules) {
            if (schedule.isWithinBounds(calendar)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        JSONArray schedules = new JSONArray();
        this.schedules.forEach((s) -> schedules.put(s.toJson()));
        json.put(SCHEDULES, schedules);

        JSONObject resolved = new JSONObject();
        resolved.put(IS_GAME_OPEN, isGameOpen());
        resolved.put(CURRENT_TIME, getCurrentTimeString());
        json.put(RESOLVED, resolved);

        json.put("class", Codec.typeFromClass(getClass()));

        return json;
    }
}
