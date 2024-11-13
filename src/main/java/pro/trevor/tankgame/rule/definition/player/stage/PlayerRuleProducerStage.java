package pro.trevor.tankgame.rule.definition.player.stage;

import pro.trevor.tankgame.rule.definition.player.PlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;

import java.util.function.BiFunction;

public class PlayerRuleProducerStage<T> implements IPlayerRuleStage {

    private final BiFunction<PlayerRuleContext, PlayerRule, T> producer;

    public PlayerRuleProducerStage(BiFunction<PlayerRuleContext, PlayerRule, T> producer) {
        this.producer = producer;
    }

    @Override
    public T apply(PlayerRuleContext context, PlayerRule rule) {
        return producer.apply(context, rule);
    }
}
