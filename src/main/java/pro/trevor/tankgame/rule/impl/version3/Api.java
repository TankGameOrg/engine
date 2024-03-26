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
    public int getVersion() {
        return 3;
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
                    String location = json.getString(JsonKeys.LOCATION);
                    Position position = positionFromString(location);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, Position.class, Void.class, Ruleset.Rules.MOVE)
                            .apply(state, tank, position, Optional.empty());
                }
                case Ruleset.Rules.SHOOT -> {
                    String location = json.getString(JsonKeys.LOCATION);
                    Position position = positionFromString(location);
                    boolean hit = json.getBoolean(JsonKeys.HIT);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, Position.class, Boolean.class, Ruleset.Rules.SHOOT)
                            .apply(state, tank, position, Optional.of(hit));

                }
                case Ruleset.Rules.DONATE -> {
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.QUANTITY);
                    Tank subjectTank = getTank(subject);
                    Tank targetTank = getTank(target);
                    getRule(Tank.class, Tank.class, Integer.class, Ruleset.Rules.DONATE)
                            .apply(state, subjectTank, targetTank, Optional.of(quantity));
                }
                case Ruleset.Rules.BUY_ACTION -> {
                    int quantity = json.getInt(JsonKeys.QUANTITY);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, Tank.class, Integer.class, Ruleset.Rules.BUY_ACTION)
                            .apply(state, tank, tank, Optional.of(quantity));
                }
                case Ruleset.Rules.UPGRADE_RANGE -> {
                    Tank tank = getTank(subject);
                    getRule(Tank.class, Tank.class, Void.class, Ruleset.Rules.UPGRADE_RANGE)
                            .apply(state, tank, tank, Optional.empty());
                }

                case Ruleset.Rules.STIMULUS -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    getMetaRule(Council.class, Tank.class, Void.class, Ruleset.Rules.STIMULUS)
                            .apply(state, state.getCouncil(), getTank(target), Optional.empty());
                }
                case Ruleset.Rules.BOUNTY -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.QUANTITY);
                    getMetaRule(Council.class, Tank.class, Integer.class, Ruleset.Rules.BOUNTY)
                            .apply(state, state.getCouncil(), getTank(target), Optional.of(quantity));
                }
                case Ruleset.Rules.GRANT_LIFE -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    getMetaRule(Council.class, Tank.class, Void.class, Ruleset.Rules.GRANT_LIFE)
                            .apply(state, state.getCouncil(), getTank(target), Optional.empty());
                }
                default -> throw new Error("Unexpected action: " + action);
            }
        }

        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }

    @Override
    public void printStateJson(boolean humanReadable) {
        System.out.println(state.toJson().toString(humanReadable ? 2 : 0));
    }

    @Override
    public void printPossibleMovesJson(boolean humanReadable) {
        JSONArray output = new JSONArray();
        Map<Pair<?, ?>, List<IPlayerRule<?, ?, ?>>> applicable = applicablePlayerRules(state, ruleset);
        for (Pair<?, ?> key : applicable.keySet()) {

            List<IPlayerRule<?, ?, ?>> rules = applicable.get(key);
            if (rules.isEmpty()) {
                continue;
            }
            for (IPlayerRule<?, ?, ?> rule : rules) {
                JSONObject pairRulesJson = new JSONObject();
                pairRulesJson.put("rules", rule.name());
                if (key.left() instanceof IJsonObject left && key.right() instanceof IJsonObject right) {
                    pairRulesJson.put("subject", left.toShortJson());
                    pairRulesJson.put("target", right.toShortJson());
                }
                output.put(pairRulesJson);
            }

        }
        System.out.println(output.toString(humanReadable ? 2 : 0));
    }


    private <T extends IPlayerElement, U, V> IPlayerRule<T, U, V> getRule(Class<T> t, Class<U> u, Class<V> v, String name) {
        List<IPlayerRule<T, U, ?>> rules = ruleset.getPlayerRules().getExact(t, u);
        if (rules.isEmpty()) {
            throw new Error(String.format("No rule for `%s/%s`", t.getSimpleName(), u.getSimpleName()));
        }

        List<IPlayerRule<T, U, ?>> namedRules = rules.stream().filter(r -> r.name().equals(name)).toList();
        if (namedRules.isEmpty()) {
            throw new Error(String.format("No rule named `%s`", name));
        }

        return (IPlayerRule<T, U, V>) namedRules.getFirst();
    }

    private <T extends IPlayerElement, U, V> IPlayerRule<T, U, V> getMetaRule(Class<T> t, Class<U> u, Class<V> v, String name) {
        List<IPlayerRule<T, U, ?>> rules = ruleset.getMetaPlayerRules().getExact(t, u);
        if (rules.isEmpty()) {
            throw new Error(String.format("No rule for `%s/%s`", t.getSimpleName(), u.getSimpleName()));
        }

        List<IPlayerRule<T, U, ?>> namedRules = rules.stream().filter(r -> r.name().equals(name)).toList();
        if (namedRules.isEmpty()) {
            throw new Error(String.format("No rule named `%s`", name));
        }

        return (IPlayerRule<T, U, V>) namedRules.getFirst();
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
        for (Class<?> c : ruleset.getMetaEnforcerRules().keySet()) {
            state.getMetaElements().forEach((x) -> ruleset.getMetaEnforcerRules().enforceRules(state, x));
        }
    }

    private static void applyConditionals(State state, RulesetDescription ruleset) {
        for (Class<?> c : ruleset.getConditionalRules().keySet()) {
            state.getBoard().gather(c).forEach((x) -> ruleset.getConditionalRules().applyRules(state, x));
        }
        for (Class<?> c : ruleset.getMetaConditionalRules().keySet()) {
            state.getMetaElements().forEach((x) -> ruleset.getMetaConditionalRules().applyRules(state, x));
        }
    }

    private static Map<Pair<?, ?>, List<IPlayerRule<?, ?, ?>>> applicablePlayerRules(State state, RulesetDescription ruleset) {
        Map<Pair<?, ?>, List<IPlayerRule<?, ?, ?>>> applicable = new HashMap<>();

        for (DuoClass<?, ?> c : ruleset.getPlayerRules().keySet()) {
            List<IPlayerRule<?, ?, ?>> rules = ruleset.getPlayerRules().get(c.getLeftClass(), c.getRightClass());
            for (IPlayerRule<?, ?, ?> rule : rules) {
                IPlayerRule<Object, Object, Object> aRule = (IPlayerRule<Object, Object, Object>) rule; // Always works
                List<Pair<?, ?>> pairs =  state.getBoard().gather(c.getLeftClass()).stream()
                        .flatMap( // For each LHS class, flatten the map of
                                (x) -> state.getBoard().gather(c.getRightClass()).stream() // objects obtained on board
                                        .map((y) -> new Pair<>(x, y) ) // and map into pairs with all objects on board
                                        .filter((p) -> aRule.canApply(state, p.left(), p.right()))) // that can apply the rule
                        .collect(Collectors.toList());
                for (Pair<?, ?> pair : pairs) {
                    if (applicable.containsKey(pair)) {
                        applicable.get(pair).add(rule);
                    } else {
                        List<IPlayerRule<?, ?, ?>> ruleList = new ArrayList<>();
                        ruleList.add(rule);
                        applicable.put(pair, ruleList);
                    }
                }
            }

        }
        for (DuoClass<?, ?> c : ruleset.getMetaPlayerRules().keySet()) {
            List<IPlayerRule<?, ?, ?>> rules = ruleset.getMetaPlayerRules().get(c.getLeftClass(), c.getRightClass());
            for (IPlayerRule<?, ?, ?> rule : rules) {
                IPlayerRule<Object, Object, Object> aRule = (IPlayerRule<Object, Object, Object>) rule; // Always works
                List<Pair<?, ?>> pairs =  state.getMetaElements(c.getLeftClass()).stream()
                        .flatMap( // For each LHS class, flatten the map of
                                (x) -> state.getBoard().gather(c.getRightClass()).stream() // objects obtained on board
                                        .map((y) -> new Pair<>(x, y) ) // and map into pairs with all objects on board
                                        .filter((p) -> aRule.canApply(state, p.left(), p.right()))) // that can apply the rule
                        .collect(Collectors.toList());
                for (Pair<?, ?> pair : pairs) {
                    if (applicable.containsKey(pair)) {
                        applicable.get(pair).add(rule);
                    } else {
                        List<IPlayerRule<?, ?, ?>> ruleList = new ArrayList<>();
                        ruleList.add(rule);
                        applicable.put(pair, ruleList);
                    }
                }
            }
        }
        return applicable;
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
        public static final String LOCATION = "location";
        public static final String TARGET = "target";
        public static final String QUANTITY = "quantity";
        public static final String HIT = "hit";

    }
}
