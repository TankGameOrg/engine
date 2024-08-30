package pro.trevor.tankgame.rule.definition.player;

import java.util.List;

import pro.trevor.tankgame.rule.definition.range.TypeRange;

public interface IPlayerRule {
    void apply(PlayerRuleContext context);
    List<PlayerRuleError> canApply(PlayerRuleContext context);

    String name();
    TypeRange<?>[] parameters();
}
