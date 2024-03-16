package rule.impl.version3;

import rule.definition.RulesetDescription;
import rule.definition.player.IPlayerRule;
import rule.impl.IRepl;
import rule.impl.IRuleset;
import state.State;
import state.board.Position;
import state.board.floor.GoldMine;
import state.board.unit.Tank;
import state.board.unit.Wall;
import util.DuoClass;
import util.Pair;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Repl implements IRepl {
    private final RulesetDescription ruleset;
    private State state;
    private InputStream input;

    public Repl(InputStream inputStream) {
        this.ruleset = IRuleset.getRuleset(new Ruleset());
        initialize(inputStream);
    }

    @Override
    public void initialize(InputStream input) {
        this.input = input;

        Scanner scanner = new Scanner(input);
        int width = scanner.nextInt();
        int height = scanner.nextInt();
        int playerCount = scanner.nextInt();
        scanner.nextLine(); // seek to the next line

        this.state = new State(width, height);

        for (int y = 0; y < height; ++y) {
            String line = scanner.nextLine();
            System.out.println(line);
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
            state.getBoard().putUnit(new Tank(state.getPlayer(player).get(), players.get(player), 0, 0, 3, 2, 0, false));
        }

        System.out.println(state.getBoard().toUnitString());
        System.out.println(state.getBoard().toFloorString());

        handleTick();
    }

    @Override
    public void handleTick() {
        applyTick(state, ruleset);
        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }

    @Override
    public void handleLine() {
        handlePlayerRules(state, ruleset);
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

    private static void handlePlayerRules(State state, RulesetDescription ruleset) {
        Map<Pair<?, ?>, List<IPlayerRule<?, ?>>> applicable = new HashMap<>();

        for (DuoClass<?, ?> c : ruleset.getPlayerRules().keySet()) {
            List<IPlayerRule<?, ?>> rules = ruleset.getPlayerRules().get(c.getLeftClass(), c.getRightClass());
            for (IPlayerRule<?, ?> rule : rules) {
                IPlayerRule<Object, Object> aRule = (IPlayerRule<Object, Object>) rule; // Always works
                List<Pair<?, ?>> pairs =  state.getBoard().gather(c.getLeftClass()).stream()
                        .flatMap( // For each LHS class, flatten the map of
                                (x) -> state.getBoard().gather(c.getRightClass()).stream() // objects obtained on board
                                        .map((y) -> new Pair<>(x, y) ) // and map into pairs with all objects on board
                                        .filter((p) -> aRule.canApply(state, p.left(), p.right()))) // that can apply the rule
                        .collect(Collectors.toList());
                for (Pair<?, ?> pair : pairs) {
                    System.out.printf("%s can %s to %s\n", pair.left(), rule.name(), pair.right());
                    if (applicable.containsKey(pair)) {
                        applicable.get(pair).add(rule);
                    } else {
                        List<IPlayerRule<?, ?>> ruleList = new ArrayList<>();
                        ruleList.add(rule);
                        applicable.put(pair, ruleList);
                    }
                }
            }

        }
        for (DuoClass<?, ?> c : ruleset.getMetaPlayerRules().keySet()) {
            List<IPlayerRule<?, ?>> rules = ruleset.getMetaPlayerRules().get(c.getLeftClass(), c.getRightClass());
            for (IPlayerRule<?, ?> rule : rules) {
                IPlayerRule<Object, Object> aRule = (IPlayerRule<Object, Object>) rule; // Always works
                List<Pair<?, ?>> pairs =  state.getMetaElements(c.getLeftClass()).stream()
                        .flatMap( // For each LHS class, flatten the map of
                                (x) -> state.getBoard().gather(c.getRightClass()).stream() // objects obtained on board
                                        .map((y) -> new Pair<>(x, y) ) // and map into pairs with all objects on board
                                        .filter((p) -> aRule.canApply(state, p.left(), p.right()))) // that can apply the rule
                        .collect(Collectors.toList());
                for (Pair<?, ?> pair : pairs) {
                    System.out.printf("%s can %s to %s\n", pair.left(), rule.name(), pair.right());
                    if (applicable.containsKey(pair)) {
                        applicable.get(pair).add(rule);
                    } else {
                        List<IPlayerRule<?, ?>> ruleList = new ArrayList<>();
                        ruleList.add(rule);
                        applicable.put(pair, ruleList);
                    }
                }
            }
        }
    }

    private static void applyTick(State state, RulesetDescription ruleset) {
        for (Class<?> c : ruleset.getTickRules().keySet()) {
            state.getBoard().gather(c).forEach((x) -> ruleset.getTickRules().applyRules(state, x));
        }
        for (Class<?> c : ruleset.getMetaTickRules().keySet()) {
            state.getMetaElements(c).forEach((x) -> ruleset.getMetaTickRules().applyRules(state, x));
        }
    }
}
