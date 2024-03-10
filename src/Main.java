import rule.definition.RulesetDescription;
import rule.definition.player.IPlayerRule;
import rule.impl.IRuleset;
import rule.impl.Version3;
import state.board.Position;
import state.State;
import state.board.floor.GoldMine;
import state.board.unit.*;

import java.util.*;

import static util.LineOfSight.hasLineOfSight;

public class Main {

    public static void main(String[] args) {

        RulesetDescription ruleset = IRuleset.getRuleset(new Version3());

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
        assert s.getCouncil().getCouncillors().contains(t.getPlayers());
        assert !s.getCouncil().getSenators().contains(t.getPlayers());

        System.out.println(t.toInfoString());
        t.setDead(false);
        t.setDurability(1);


        // Test player rules
        System.out.println(ruleset.getPlayerRules().getAllRulesForSubject(Tank.class));
        t.setGold(0);
        List<IPlayerRule<Tank, Tank>> possibleActions = ruleset.getPlayerRules().applicableSelfRules(Tank.class, s, t);
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
    }
}