package pro.trevor.tankgame.e2e;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.PlayerRef;

import static pro.trevor.tankgame.e2e.EndToEndTestUtils.readFile;

public class EndToEndTester {

    private final Api api;

    public EndToEndTester(IRulesetRegister rulesetRegister, JSONObject initialState, JSONArray moves) {
        this.api = new Api(rulesetRegister);
        api.setState((State) Codec.decodeJson(initialState));
        for (int i = 0; i < moves.length(); ++i) {
            api.ingestAction(new LogEntry(moves.getJSONObject(i)));
        }
    }

    public EndToEndTester(IRulesetRegister rulesetRegister, String initialStatePath, String movesPath) {
        this(rulesetRegister, new JSONObject(readFile(initialStatePath)), new JSONArray(readFile(movesPath)));
    }

    public State getState() {
        return api.getState();
    }

    public Council getCouncil() {
        return api.getState().getCouncil();
    }

    public Board getBoard() {
        return api.getState().getBoard();
    }

    public Tank getTankByPlayerName(String player) {
        return (Tank) api.getState().getBoard().getPlayerElement(new PlayerRef(player)).get();
    }

    public IFloor getFloorAtPosition(Position position) {
        return getBoard().getFloor(position).get();
    }

    public IUnit getUnitAtPosition(Position position) {
        return getBoard().getUnit(position).get();
    }

}
