package rule.impl;

import rule.annotation.RuleClass;
import rule.annotation.RuleFunction;
import rule.annotation.RuleType;
import state.State;
import state.board.unit.Tank;

@RuleClass(version = 3)
public class Version3 {

    @RuleFunction(id = "myrule", type = RuleType.TICK)
    public static void rule(Tank t, State s) {
        System.out.println("We made it to function call land!");
    }

}
