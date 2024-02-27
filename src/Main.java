import rule.impl.ApplicableRuleset;
import rule.impl.IConditionalRule;
import rule.impl.PlayerAction;
import rule.impl.enforcer.EnforcerRuleset;
import rule.impl.enforcer.MinimumEnforcer;
import rule.impl.ConditionalRule;
import state.board.Position;
import state.State;
import state.board.Tank;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EnforcerRuleset enforcerRuleset = new EnforcerRuleset();
        enforcerRuleset.put(Tank.class, new MinimumEnforcer<>(Tank::getDurability, Tank::setDurability, 0));

        Tank t = new Tank(new Position(0, 0), 3, 0, 3, 2);
        State s = new State(11, 11);


        // Test IEnforceable
        s.getBoard().putUnit(t);
        t.setDurability(-1);
        System.out.println(t);
        enforcerRuleset.enforceRules(s, t);
        System.out.println(t);


        // Test IApplicableRule
        ApplicableRuleset applicableRuleset = new ApplicableRuleset();
        applicableRuleset.put(Tank.class, new ConditionalRule<>((x, y) -> x.getDurability() == 0, (x, y) -> System.out.println("DEAD")));

        for (Tank tank : s.getBoard().gather(Tank.class)) {
            applicableRuleset.applyRules(s, tank);
        }


        // Test IConditionalRule
        ApplicableRuleset possiblePlayerActions = new ApplicableRuleset();
        possiblePlayerActions.put(Tank.class, new PlayerAction<>((x, y) -> x.getGold() == 3, (x, y) -> {
            x.setActions(x.getActions() + 1);
            x.setGold(x.getGold()-3);
        }));

        List<IConditionalRule<Tank>> possibleActions = possiblePlayerActions.applicableConditionalRules(Tank.class, s, t);
        System.out.println("Applicable actions: " + possibleActions.size());
        t.setGold(3);
        possibleActions = possiblePlayerActions.applicableConditionalRules(Tank.class, s, t);
        System.out.println("Applicable actions: " + possibleActions.size());
        System.out.println(t);
        for (IConditionalRule<Tank> action  : possibleActions) {
            action.apply(s, t);
        }
        System.out.println(t);

    }
}