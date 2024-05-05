package pro.trevor.tankgame.rule.impl.version4;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.version3.ApiV3;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.ArmisticeCouncil;

public class ApiV4 extends ApiV3 implements IApi {

    public ApiV4() {
        super(new Ruleset());
    }

    @Override
    public void ingestState(JSONObject json) {
        int tick = json.getInt("day");
        boolean running = json.getBoolean("running");
        String winner = json.getString("winner");
        JSONObject council = json.getJSONObject("council");
        JSONArray councillors = council.getJSONArray("council");
        JSONArray senators = council.getJSONArray("senate");
        JSONObject board = json.getJSONObject("board");
        JSONArray unitBoard = board.getJSONArray("unit_board");
        JSONArray floorBoard = board.getJSONArray("floor_board");
        assert unitBoard.length() == floorBoard.length();
        assert unitBoard.getJSONArray(0).length() == floorBoard.getJSONArray(0).length();
        int boardWidth = unitBoard.length();
        int boardHeight = unitBoard.getJSONArray(0).length();
        int armisticeCap = json.getInt("armistice_vote_cap");
        int armisticeCount = json.getInt("armistice_vote_count");
        state = new State(new Board(boardWidth, boardHeight), new ArmisticeCouncil(armisticeCap, armisticeCount));
        state.setTick(tick);
        state.setRunning(running);
        state.setWinner(winner);
        state.getCouncil().getCouncillors().addAll(councillors.toList().stream().map(Object::toString).toList());
        state.getCouncil().getSenators().addAll(senators.toList().stream().map(Object::toString).toList());
        state.getCouncil().setCoffer(council.getInt("coffer"));
        for (int i = 0; i < unitBoard.length(); ++i) {
            JSONArray unitBoardRow = unitBoard.getJSONArray(i);
            JSONArray floorBoardRow = floorBoard.getJSONArray(i);
            for (int j = 0; j < unitBoardRow.length(); ++j) {
                Position position = new Position(j, i);
                JSONObject unitJson = unitBoardRow.getJSONObject(j);
                JSONObject floorJson = floorBoardRow.getJSONObject(j);
                state.getBoard().putUnit(unitFromJson(unitJson, position));
                state.getBoard().putFloor(floorFromJson(floorJson, position));
                if (unitJson.getString("type").equals("tank")) {
                    state.putPlayer(unitJson.getString("name"));
                }
            }
        }
    }
}
