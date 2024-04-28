package pro.trevor.tankgame.state.board.unit.tank.status;

import org.json.JSONObject;

public abstract class DurationStatus implements IDurationStatus {

    protected String name;
    protected int duration;

    public DurationStatus(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public DurationStatus(JSONObject json) {
        assert json.get("type").equals("status");
        this.name = json.getString("name");
        this.duration = json.getInt("duration");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "status");
        output.put("name", name);
        output.put("duration", duration);
        return output;
    }
}
