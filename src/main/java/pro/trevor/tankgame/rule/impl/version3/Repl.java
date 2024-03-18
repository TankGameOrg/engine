package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.IRepl;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.Wall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.DuoClass;
import pro.trevor.tankgame.util.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.*;

public class Repl implements IRepl {
    private final RulesetDescription ruleset;
    private State state;
    private JSONArray movesJson;
    private int element;

    private static final String COUNCIL = "Council";

    public Repl(InputStream initialState, InputStream moves) {
        this.ruleset = IRuleset.getRuleset(new Ruleset());
        this.element = 0;
        initialize(initialState, moves);
    }

    @Override
    public void initialize(InputStream initialState, InputStream moves) {
        Scanner scanner = new Scanner(initialState);
        int width = scanner.nextInt();
        int height = scanner.nextInt();
        int playerCount = scanner.nextInt();
        scanner.nextLine(); // seek to the next line

        this.state = new State(width, height);

        for (int y = 0; y < height; ++y) {
            String line = scanner.nextLine();
            for (int x = 0; x < width; ++x) {
                char element = line.charAt(x * 2);
                switch (element) {
                    case 'W':
                        state.getBoard().putUnit(new Wall(new Position(x, y)));
                        break;
                    case 'G':
                        state.getBoard().putFloor(new GoldMine(new Position(x, y)));
                        break;
                    case 'T':
                    case '_':
                    default:
                        break;
                }
            }
        }

        Map<String, Position> players = new HashMap<>();

        for (int i = 0; i < playerCount; ++i) {
            String name = scanner.next();
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            state.putPlayer(name);
            players.put(name, new Position(x, y));
        }

        for (String player : players.keySet()) {
            state.getBoard().putUnit(new Tank(player, players.get(player), 0, 0, 3, 2, 0, false));
        }


        System.out.println("\n START STATE \n");
        System.out.println(state.getBoard().toUnitString());
        System.out.println(state.getBoard().toFloorString());

        BufferedReader reader = new BufferedReader(new InputStreamReader(moves));
        Stream<String> lines = reader.lines();
        String file = lines.reduce((l, r) -> l + r).orElse("EMPTY");
        this.movesJson = new JSONArray(file);
    }

    @Override
    public void handleTick() {
        applyTick(state, ruleset);
        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }

    @Override
    public void handleLine() {
        if (isDone()) {
            return;
        }

        JSONObject object = movesJson.getJSONObject(element);

        if (object.keySet().contains(JsonKeys.DAY)) {
            handleTick();
        } else {
            handleJsonLine(object);
        }

        element += 1;
    }

    @Override
    public boolean isDone() {
        return element >= movesJson.length();
    }

    private void handleJsonLine(JSONObject object) {
        String subject = object.getString(JsonKeys.SUBJECT);
        String action = object.getString(JsonKeys.ACTION);

        switch (action) {
            case Ruleset.Rules.MOVE -> {
                String location = object.getString(JsonKeys.LOCATION);
                Position position = positionFromString(location);
                Tank tank = getTank(subject);
                getRule(Tank.class, Position.class, Void.class, Ruleset.Rules.MOVE)
                        .apply(state, tank, position, Optional.empty());
            }
            case Ruleset.Rules.SHOOT -> {
                String location = object.getString(JsonKeys.LOCATION);
                Position position = positionFromString(location);
                boolean hit = object.getBoolean(JsonKeys.HIT);
                Tank tank = getTank(subject);
                getRule(Tank.class, Position.class, Boolean.class, Ruleset.Rules.SHOOT)
                        .apply(state, tank, position, Optional.of(hit));

            }
            case Ruleset.Rules.DONATE -> {
                String target = object.getString(JsonKeys.TARGET);
                int quantity = object.getInt(JsonKeys.QUANTITY);
                Tank subjectTank = getTank(subject);
                Tank targetTank = getTank(target);
                getRule(Tank.class, Tank.class, Integer.class, Ruleset.Rules.DONATE)
                        .apply(state, subjectTank, targetTank, Optional.of(quantity));
            }
            case Ruleset.Rules.BUY_ACTION -> {
                int quantity = object.getInt(JsonKeys.QUANTITY);
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
                String target = object.getString(JsonKeys.TARGET);
                getMetaRule(Council.class, Tank.class, Void.class, Ruleset.Rules.STIMULUS)
                        .apply(state, state.getCouncil(), getTank(target), Optional.empty());
            }
            case Ruleset.Rules.BOUNTY -> {
                assert subject.equals(COUNCIL);
                String target = object.getString(JsonKeys.TARGET);
                int quantity = object.getInt(JsonKeys.QUANTITY);
                getMetaRule(Council.class, Tank.class, Integer.class, Ruleset.Rules.BOUNTY)
                        .apply(state, state.getCouncil(), getTank(target), Optional.of(quantity));
            }
            case Ruleset.Rules.GRANT_LIFE -> {
                assert subject.equals(COUNCIL);
                String target = object.getString(JsonKeys.TARGET);
                getMetaRule(Council.class, Tank.class, Void.class, Ruleset.Rules.GRANT_LIFE)
                        .apply(state, state.getCouncil(), getTank(target), Optional.empty());
            }

            default -> throw new Error("Unexpected action: " + action);
        }

        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);

//        Map<Pair<?, ?>, List<IPlayerRule<?, ?, ?>>> applicable = applicablePlayerRules(state, ruleset);
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
