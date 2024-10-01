package pro.trevor.tankgame.util.openhours;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;

@JsonType(name = "Schedule")
public class Schedule implements IJsonObject {

    private static final String DAYS = "daysOfWeek";
    private static final String HOLIDAYS = "holidays";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";


    private final List<Day> days;
    private final List<Date> holidays;
    private final Time begin;
    private final Time end;

    public Schedule(JSONObject json) {
        this.days = new ArrayList<>();
        JSONArray jsonDays = json.getJSONArray(DAYS);
        for (int i = 0; i < jsonDays.length(); i++) {
            this.days.add(Day.fromName(jsonDays.getString(i)));
        }

        this.holidays = new ArrayList<>();
        JSONArray jsonHolidays = json.getJSONArray(HOLIDAYS);
        for (int i = 0; i < jsonHolidays.length(); i++) {
            this.holidays.add(new Date(jsonHolidays.getString(i)));
        }

        this.begin = new Time(json.getString(START_TIME));
        this.end = new Time(json.getString(END_TIME));
    }

    public Schedule(List<Day> days, List<Date> holidays, Time begin, Time end) {
        this.days = days;
        this.holidays = holidays;
        this.begin = begin;
        this.end = end;
    }

    public boolean isWithinBounds(Calendar calendar) {
        Day day = Day.fromNumber(calendar.get(Calendar.DAY_OF_WEEK));
        Time time = new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        return holidays.stream().noneMatch((d) -> d.isDay(calendar)) && days.contains(day) && time.isBetween(begin, end);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(START_TIME, begin.toString());
        json.put(END_TIME, end.toString());

        JSONArray days = new JSONArray();
        this.days.forEach((d) -> days.put(d.toString()));
        json.put(DAYS, days);

        JSONArray holidays = new JSONArray();
        this.holidays.forEach((d) -> holidays.put(d.toString()));
        json.put(HOLIDAYS, holidays);

        json.put("class", Codec.typeFromClass(getClass()));

        return json;
    }
}
