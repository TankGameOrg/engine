package pro.trevor.tankgame.rule.definition.player.stage;

import pro.trevor.tankgame.rule.definition.player.PlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class PlayerRuleReducerListStage<T> implements IPlayerRuleStage {

    private final List<BiFunction<PlayerRuleContext, PlayerRule, T>> modifiers;
    private final BiFunction<T, T, T> reducer;
    private final T initialValue;

    public PlayerRuleReducerListStage(String identifier, BiFunction<T, T, T> reducer, T initialValue) {
        this.modifiers = new ArrayList<>();
        this.reducer = reducer;
        this.initialValue = initialValue;
    }

    public void addModifier(BiFunction<PlayerRuleContext, PlayerRule, T> modifier) {
        modifiers.add(modifier);
    }

    public void addModifiers(List<BiFunction<PlayerRuleContext, PlayerRule, T>> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    @Override
    public T apply(PlayerRuleContext context, PlayerRule rule) {
        T current  = initialValue;
        for (BiFunction<PlayerRuleContext, PlayerRule, T> modifier : modifiers) {
            current = reducer.apply(current, modifier.apply(context, rule));
        }
        return current;
    }
}
