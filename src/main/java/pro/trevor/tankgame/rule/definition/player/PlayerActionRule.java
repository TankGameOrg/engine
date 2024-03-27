package pro.trevor.tankgame.rule.definition.player;

import netscape.javascript.JSObject;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.IQuadConsumer;
import pro.trevor.tankgame.util.ITriPredicate;

import java.util.Optional;

public class PlayerActionRule<T extends IPlayerElement, U, V> implements IPlayerRule<T, U, V> {

    private final String name;
    private final ITriPredicate<T, U, State> predicate;
    private final IQuadConsumer<T, U, Optional<V>, State> consumer;


    public PlayerActionRule(String name, ITriPredicate<T, U, State> predicate, IQuadConsumer<T, U, Optional<V>, State> consumer) {
        this.name = name;
        this.predicate = predicate;
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject, U target, Optional<V> meta) {
        if (canApply(state, subject, target)) {
            consumer.accept(subject, target, meta, state);
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name);

            if (subject instanceof IJsonObject subjectJson) {
                error.put("subject", subjectJson.toJson());
            } else {
                error.put("subject", subject.getPlayer());
            }

            if (target instanceof IJsonObject targetJson) {
                error.put("target", targetJson.toJson());
            } else {
                error.put("target", target.toString());
            }

            System.out.println(error.toString(2));
            throw new Error(String.format("Failed to apply `%s` to `%s` and `%s`", name, subject, target));
        }
    }

    @Override
    public boolean canApply(State state, T subject, U target) {
        return predicate.test(subject, target, state);
    }

    @Override
    public String name() {
        return name;
    }
}
