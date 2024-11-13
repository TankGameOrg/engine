package pro.trevor.tankgame.rule.definition.player.stage;

import pro.trevor.tankgame.rule.definition.player.PlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;

import java.util.function.BiConsumer;

public class PlayerRuleConsumerStage implements IPlayerRuleStage {

    private final BiConsumer<PlayerRuleContext, PlayerRule> consumer;

    public PlayerRuleConsumerStage(BiConsumer<PlayerRuleContext, PlayerRule> consumer) {
        this.consumer = consumer;
    }

    @Override
    public Object apply(PlayerRuleContext context, PlayerRule rule) {
        consumer.accept(context, rule);
        return null;
    }
}
