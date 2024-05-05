package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.floor.StandardFloor;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.range.VariableTypeRange;

import java.util.*;

import org.json.*;
import pro.trevor.tankgame.util.range.DiscreteTypeRange;
import pro.trevor.tankgame.util.range.TypeRange;

public class ApiV3 implements IApi {
    protected final RulesetDescription ruleset;
    protected State state;

    protected static final String COUNCIL = "Council";

    public ApiV3() {
        this.ruleset = IRuleset.getRuleset(new Ruleset());
    }

    protected ApiV3(IRuleset ruleset) {
        this.ruleset = IRuleset.getRuleset(ruleset);
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

    protected static IUnit unitFromJson(JSONObject json, Position position) {
        String type = json.getString("type");
        switch (type) {
            case "tank" -> {
                return new Tank(json);
            }
            case "wall" -> {
                return new BasicWall(json);
            }
            case "empty" -> {
                return new EmptyUnit(position);
            }
            default -> throw new Error("Unhandled unit type " + type);
        }
    }

    protected static IFloor floorFromJson(JSONObject json, Position position) {
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
        state = new State(new Board(boardWidth, boardHeight), new Council());
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

    @Override
    public void ingestAction(JSONObject json) {
        if (!state.isRunning()) {
            throw new Error("The game is over; no actions can be submitted");
        }

        if (json.keySet().contains(JsonKeys.DAY)) {
            applyTick(state, ruleset);
        } else {
            String subject = json.getString(JsonKeys.SUBJECT);
            String action = json.getString(JsonKeys.ACTION);

            switch (action) {
                case PlayerRules.ActionKeys.MOVE -> {
                    String positionString = json.getString(JsonKeys.TARGET);
                    Position position = new Position(positionString);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.MOVE).apply(state, tank, position);
                }
                case PlayerRules.ActionKeys.SHOOT -> {
                    String location = json.getString(JsonKeys.TARGET);
                    Position position = new Position(location);
                    boolean hit = json.getBoolean(JsonKeys.HIT);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.SHOOT).apply(state, tank, position, hit);

                }
                case PlayerRules.ActionKeys.DONATE -> {
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.DONATION);
                    Tank subjectTank = getTank(subject);
                    Tank targetTank = getTank(target);
                    getRule(Tank.class, PlayerRules.ActionKeys.DONATE).apply(state, subjectTank, targetTank, quantity);
                }
                case PlayerRules.ActionKeys.BUY_ACTION -> {
                    int quantity = json.getInt(JsonKeys.GOLD);
                    Tank subjectTank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.BUY_ACTION).apply(state, subjectTank, quantity);
                }
                case PlayerRules.ActionKeys.UPGRADE_RANGE -> {
                    Tank subjectTank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.UPGRADE_RANGE).apply(state, subjectTank);
                }

                case PlayerRules.ActionKeys.STIMULUS -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, PlayerRules.ActionKeys.STIMULUS).apply(state, state.getCouncil(), targetTank);
                }
                case PlayerRules.ActionKeys.BOUNTY -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.BOUNTY);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, PlayerRules.ActionKeys.BOUNTY).apply(state, state.getCouncil(), targetTank, quantity);
                }
                case PlayerRules.ActionKeys.GRANT_LIFE -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, PlayerRules.ActionKeys.GRANT_LIFE).apply(state, state.getCouncil(), targetTank);
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

    @Override
    public JSONObject getPossibleActions(String player) {
        JSONObject actions = new JSONObject();
        actions.put("error", false);
        actions.put("type", "possible_actions");
        actions.put("player", player);

        JSONArray actionsArray = new JSONArray();
        List<IPlayerRule<?>> rules;
        Class<?> type;
        Object subject;

        if (player.equals(COUNCIL)) {
            type = Council.class;
            rules = ruleset.getMetaPlayerRules().get(type);
            subject = state.getCouncil();
        } else if (state.getPlayers().contains(player)) {
            type = Tank.class;
            rules = ruleset.getPlayerRules().get(type);
            Optional<Tank> tank = state.getBoard().gather(Tank.class).stream()
                    .filter((t) -> t.getPlayer().equals(player))
                    .findFirst();
            if (tank.isPresent()) {
                subject = tank.get();
            } else {
                actions.put("actions", actionsArray);
                return actions;
            }
        } else {
            throw new Error("Unknown player: " + player);
        }

        assert type.isInstance(subject);

        for (IPlayerRule<?> rule : rules) {
            JSONObject actionJson = new JSONObject();
            actionJson.put("rule", rule.name());
            actionJson.put("subject", type.getSimpleName().toLowerCase());

            // find all states of each parameter
            JSONArray fields = new JSONArray();
            for (TypeRange<?> field : rule.parameters()) {
                if (field instanceof VariableTypeRange<?,?> variableField) {
                    VariableTypeRange<Object, ?> genericField = (VariableTypeRange<Object, ?>) variableField;
                    genericField.generate(state, subject);
                }
                fields.put(field.toJson());
            }
            actionJson.put("fields", fields);
            actionsArray.put(actionJson);
        }

        actions.put("actions", actionsArray);
        return actions;
    }

    private Set<List<Object>> allPermutations(IPlayerRule<?> rule, State state, Object subject) {
        IPlayerRule<Object> genericRule = (IPlayerRule<Object>) rule;
        DiscreteTypeRange<?>[] parameters = new DiscreteTypeRange<?>[rule.parameters().length];
        for (int i = 0; i < rule.parameters().length; ++i) {
            TypeRange<?> parameter = rule.parameters()[i];
            if (parameter instanceof DiscreteTypeRange<?> discreteParameter) {
                parameters[i] = discreteParameter;
            } else {
                throw new Error(String.format("Given parameter `%s` is not discrete", parameter.getName()));
            }
        }
        return allPermutations(genericRule, parameters, state, subject);
    }

    private Set<List<Object>> allPermutations(IPlayerRule<Object> rule, DiscreteTypeRange<?>[] discreteParameters, State state, Object subject, Object... permutation) {
        if (permutation.length == discreteParameters.length) {
            if (rule.canApply(state, subject, permutation)) {
                return new HashSet<>(List.of(List.of(permutation)));
            }
        } else {
            DiscreteTypeRange<?> currentParameter = discreteParameters[permutation.length];
            if (currentParameter instanceof VariableTypeRange<?,?> variableRange) {
                VariableTypeRange<Object, ?> genericRange = (VariableTypeRange<Object, ?>) variableRange;
                genericRange.generate(state, subject);
            }
            Set<List<Object>> output = new HashSet<>();
            for (Object possibleValue : currentParameter.getElements()) {
                Object[] newPermutation = new Object[permutation.length + 1];
                System.arraycopy(permutation, 0, newPermutation, 0, permutation.length);
                newPermutation[permutation.length] = possibleValue;
                output.addAll(allPermutations(rule, discreteParameters, state, subject, newPermutation));
            }
            return output;
        }
        return new HashSet<>(0);
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
        public static final String GOLD = "gold";
        public static final String DONATION = "donation";
        public static final String BOUNTY = "bounty";

    }
}
