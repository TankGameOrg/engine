package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.floor.StandardFloor;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.Wall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.DuoClass;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import org.json.*;

public class Api implements IApi {
    private final RulesetDescription ruleset;
    private State state;

    private static final String COUNCIL = "Council";

    public Api() {
        this.ruleset = IRuleset.getRuleset(new Ruleset());
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public JSONObject getRules() {
        JSONObject rules = new JSONObject();
        rules.put("type", "rules");
        JSONArray playerRules = ruleset.getPlayerRules().toJsonRequirements();
        JSONArray metaRules = ruleset.getMetaPlayerRules().toJsonRequirements();
        metaRules.forEach(playerRules::put);
        rules.put("rules", playerRules);
        return rules;
    }

    private static IUnit unitFromJson(JSONObject json, Position position) {
        String type = json.getString("type");
        switch (type) {
            case "tank" -> {
                return new Tank(json.getString("name"), position, json.getInt("actions"),
                        json.getInt("gold"), json.getInt("health"), json.getInt("range"),
                        json.getInt("bounty"), json.getBoolean("dead"));
            }
            case "wall" -> {
                return new Wall(position, json.getInt("health"));
            }
            case "empty" -> {
                return new EmptyUnit(position);
            }
            default -> throw new Error("Unhandled unit type " + type);
        }
    }

    private static IFloor floorFromJson(JSONObject json, Position position) {
        String type = json.getString("type");
        switch (type) {
            case "gold_mine" -> {
                return new GoldMine(position);
            }
            case "empty" -> {
                return new StandardFloor(position);
            }
            default -> throw new Error("Unhandled floor type " + type);
        }
    }

    @Override
    public void ingestState(JSONObject json) {
        int tick = json.getInt("day");
        JSONObject council = json.getJSONObject("council");
        JSONArray councillors = council.getJSONArray("council");
        JSONArray senators = council.getJSONArray("senate");
        JSONObject board = json.getJSONObject("board");
        JSONArray unitBoard = board.getJSONArray("unit_board");
        JSONArray floorBoard = board.getJSONArray("floor_board");
        assert unitBoard.length() == floorBoard.length();
        assert unitBoard.getJSONArray(0).length() == floorBoard.getJSONArray(0).length();
        state = new State(unitBoard.length(), unitBoard.getJSONArray(0).length());
        state.setTick(tick);
        state.getCouncil().getCouncillors().addAll(councillors.toList().stream().map(Object::toString).toList());
        state.getCouncil().getSenators().addAll(senators.toList().stream().map(Object::toString).toList());
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

    @Override
    public void ingestAction(JSONObject json) {
        if (json.keySet().contains(JsonKeys.DAY)) {
            applyTick(state, ruleset);
        } else {
            String subject = json.getString(JsonKeys.SUBJECT);
            String action = json.getString(JsonKeys.ACTION);

            switch (action) {
                case Ruleset.Rules.MOVE -> {
                    String positionString = json.getString(JsonKeys.POSITION);
                    Position position = positionFromString(positionString);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, Ruleset.Rules.MOVE).apply(state, tank, position);
                }
                case Ruleset.Rules.SHOOT -> {
                    String location = json.getString(JsonKeys.POSITION);
                    Position position = positionFromString(location);
                    boolean hit = json.getBoolean(JsonKeys.HIT);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, Ruleset.Rules.SHOOT).apply(state, tank, position, hit);

                }
                case Ruleset.Rules.DONATE -> {
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.QUANTITY);
                    Tank subjectTank = getTank(subject);
                    Tank targetTank = getTank(target);
                    getRule(Tank.class, Ruleset.Rules.DONATE).apply(state, subjectTank, targetTank, quantity);
                }
                case Ruleset.Rules.BUY_ACTION -> {
                    int quantity = json.getInt(JsonKeys.QUANTITY);
                    Tank subjectTank = getTank(subject);
                    getRule(Tank.class, Ruleset.Rules.BUY_ACTION).apply(state, subjectTank, quantity);
                }
                case Ruleset.Rules.UPGRADE_RANGE -> {
                    Tank subjectTank = getTank(subject);
                    getRule(Tank.class, Ruleset.Rules.UPGRADE_RANGE).apply(state, subjectTank);
                }

                case Ruleset.Rules.STIMULUS -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, Ruleset.Rules.STIMULUS).apply(state, state.getCouncil(), targetTank);
                }
                case Ruleset.Rules.BOUNTY -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.QUANTITY);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, Ruleset.Rules.BOUNTY).apply(state, state.getCouncil(), targetTank, quantity);
                }
                case Ruleset.Rules.GRANT_LIFE -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, Ruleset.Rules.GRANT_LIFE).apply(state, state.getCouncil(), targetTank);
                }
                default -> throw new Error("Unexpected action: " + action);
            }
        }

        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }

    @Override
    public JSONObject getStateJson() {
        return state.toJson().put("error", false);
    }


    private <T extends IPlayerElement> IPlayerRule<T> getRule(Class<T> t, String name) {
        List<IPlayerRule<T>> rules = ruleset.getPlayerRules().getExact(t);
        if (rules.isEmpty()) {
            throw new Error(String.format("No rule for `%s`", t.getSimpleName()));
        }

        List<IPlayerRule<T>> namedRules = rules.stream().filter(r -> r.name().equals(name)).toList();
        if (namedRules.isEmpty()) {
            throw new Error(String.format("No rule named `%s`", name));
        }

        return namedRules.getFirst();
    }

    private <T extends IPlayerElement> IPlayerRule<T> getMetaRule(Class<T> t, String name) {
        List<IPlayerRule<T>> rules = ruleset.getMetaPlayerRules().getExact(t);
        if (rules.isEmpty()) {
            throw new Error(String.format("No rule for `%s`", t.getSimpleName()));
        }

        List<IPlayerRule<T>> namedRules = rules.stream().filter(r -> r.name().equals(name)).toList();
        if (namedRules.isEmpty()) {
            throw new Error(String.format("No rule named `%s`", name));
        }

        return namedRules.getFirst();
    }

    private Tank getTank(String player) {
        return state.getBoard().gatherUnits(Tank.class).stream()
                .filter(t -> t.getPlayer().equals(player)).toList().getFirst();
    }

    private static Position positionFromString(String string) {
        int x = string.substring(0, 1).charAt(0) - 'A';
        int y = Integer.parseInt(string.substring(1)) - 1;
        return new Position(x, y);
    }

    private static void enforceInvariants(State state, RulesetDescription ruleset) {
        for (Class<?> c : ruleset.getEnforcerRules().keySet()) {
            state.getBoard().gather(c).forEach((x) -> ruleset.getEnforcerRules().enforceRules(state, x));
        }
        state.getMetaElements().forEach((x) -> ruleset.getMetaEnforcerRules().enforceRules(state, x));
    }

    private static void applyConditionals(State state, RulesetDescription ruleset) {
        for (Class<?> c : ruleset.getConditionalRules().keySet()) {
            state.getBoard().gather(c).forEach((x) -> ruleset.getConditionalRules().applyRules(state, x));
        }
        state.getMetaElements().forEach((x) -> ruleset.getMetaConditionalRules().applyRules(state, x));
    }

    private static void applyTick(State state, RulesetDescription ruleset) {
        for (Class<?> c : ruleset.getTickRules().keySet()) {
            state.getBoard().gather(c).forEach((x) -> ruleset.getTickRules().applyRules(state, x));
        }
        for (Class<?> c : ruleset.getMetaTickRules().keySet()) {
            state.getMetaElements(c).forEach((x) -> ruleset.getMetaTickRules().applyRules(state, x));
        }
    }

    private static class JsonKeys {
        public static final String DAY = "day";
        public static final String SUBJECT = "subject";
        public static final String ACTION = "action";
        public static final String POSITION = "position";
        public static final String TARGET = "target";
        public static final String QUANTITY = "quantity";
        public static final String HIT = "hit";

    }
}
