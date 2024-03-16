import rule.definition.RulesetDescription;
import rule.definition.player.IPlayerRule;
import rule.impl.IRuleset;
import rule.impl.version3.Ruleset;
import state.board.Position;
import state.State;
import state.board.floor.GoldMine;
import state.board.unit.*;
import util.DuoClass;
import util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static util.LineOfSight.hasLineOfSight;

public class Main {

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

    private static void handleTick(State state, RulesetDescription ruleset) {
        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);

        handlePlayerRules(state, ruleset);

        applyTick(state, ruleset);
    }

    public static void main(String[] args) {

        RulesetDescription ruleset = IRuleset.getRuleset(new Ruleset());

        State s = new State(11, 11, new HashSet<>());
        Tank t = new Tank(new Position(0, 0), 3, 0, 3, 2);
        s.getBoard().putUnit(new Tank(new Position(0, 1), 3, 0, 3, 2));
        s.getBoard().putFloor(new GoldMine(new Position(0, 0)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 1)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 2)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 3)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 4)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 5)));


        // Test enforcer rules
        s.getBoard().putUnit(t);
        t.setDurability(-1);
        System.out.println(t.toInfoString());
        ruleset.getEnforcerRules().enforceRules(s, t);
        System.out.println(t.toInfoString());
        assert t.getActions() == 0;


        // Test tick rules
        for (Tank tank : s.getBoard().gatherUnits(Tank.class)) {
            ruleset.getTickRules().applyRules(s, tank);
        }

        System.out.println(t.toInfoString());
        assert t.getGold() == 3;


        // Test conditional rules
        for (Tank tank : s.getBoard().gatherUnits(Tank.class)) {
            ruleset.getConditionalRules().applyRules(s, tank);
        }

        System.out.println(s.getCouncil().getCouncillors());
        assert s.getCouncil().getCouncillors().contains(t.getPlayers()[0]);
        assert !s.getCouncil().getSenators().contains(t.getPlayers()[0]);

        System.out.println(t.toInfoString());
        t.setDead(false);
        t.setDurability(1);


        // Test player rules
        System.out.println(ruleset.getPlayerRules().getExactRulesForSubject(Tank.class));
        t.setGold(0);
        List<IPlayerRule<Tank, Tank>> possibleActions = ruleset.getPlayerRules().applicableRules(Tank.class, Tank.class, s, t, t);
        System.out.println("Applicable self actions (should be 0): " + possibleActions.size());
        t.setGold(3);
        possibleActions = ruleset.getPlayerRules().applicableSelfRules(Tank.class, s, t);
        System.out.println("Applicable self actions (should be 1): " + possibleActions.size());
        System.out.println(t.toInfoString());
        for (IPlayerRule<Tank, Tank> action  : possibleActions) {
            action.apply(s, t, t);
        }
        System.out.println(t.toInfoString());


        Position zero = new Position(0, 0);

        // horizontal, no interrupt
        System.out.printf("line of sight (expect true): %b\n", hasLineOfSight(s, zero, new Position(5,0)));
        // vertical, yes interrupt
        System.out.printf("line of sight (expect false): %b\n", hasLineOfSight(s, zero, new Position(0,5)));
        // diagonal, check corners, yes interrupt
        System.out.printf("line of sight (expect false): %b\n", hasLineOfSight(s, zero, new Position(1,1)));
        // adjacent horizontal, implicit no interrupt
        System.out.printf("line of sight (expect true): %b\n", hasLineOfSight(s, zero, new Position(0,1)));

        // corner case, yes interrupt
        s.getBoard().putUnit(new Tank(new Position(5, 1), 0, 0, 3, 2));
        System.out.printf("line of sight (expect false): %b\n", hasLineOfSight(s, zero, new Position(5,2)));

        System.out.println(s.getBoard().toUnitString());
        System.out.println(s.getBoard().toFloorString());
        s.getCouncil().setCoffer(20);
        System.out.println(s.getCouncil());

        handleTick(s, ruleset);
    }
}