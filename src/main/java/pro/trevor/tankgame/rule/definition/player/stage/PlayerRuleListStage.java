package pro.trevor.tankgame.rule.definition.player.stage;

import pro.trevor.tankgame.rule.definition.player.PlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PlayerRuleListStage implements IPlayerRuleStage {

    private final List<BiConsumer<PlayerRuleContext, PlayerRule>> modifiers;

    public PlayerRuleListStage() {
        this.modifiers = new ArrayList<>();
    }

    public void addModifier(BiConsumer<PlayerRuleContext, PlayerRule> modifier) {
        modifiers.add(modifier);
    }

    public void addModifiers(List<BiConsumer<PlayerRuleContext, PlayerRule>> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    @Override
    public Object apply(PlayerRuleContext context, PlayerRule rule) {
        for (BiConsumer<PlayerRuleContext, PlayerRule> modifier : modifiers) {
            modifier.accept(context, rule);
        }
        return null;
    }
}
