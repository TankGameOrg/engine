package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.unit.Tank;
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
            System.out.println("\nERROR STATE\n");
            System.out.println(state.getBoard().toUnitString());
            System.out.println(state.getBoard().toFloorString());
            for (Tank tank : state.getBoard().gatherUnits(Tank.class)) {
                System.out.println(tank.toString());
            }
            System.out.println(state.toJsonObject().toString(2));
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
