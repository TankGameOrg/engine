package rule.impl;

import rule.annotation.RuleClass;
import rule.annotation.RuleTickFunction;
import state.State;
import state.board.unit.Tank;

@RuleClass(version = 3)
public class Version3 {

    @RuleTickFunction(id = "testrule")
    public static void rule(Tank t, State s) {
        System.out.println("We made it to function call land!");
    }

}
