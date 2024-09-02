package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.util.Result;

/**
 * A tool for composing a series of conditionals together to create predicates
 */
public class RulePredicateStream<T> implements IRulePredicate {
    Function<PlayerRuleContext, Result<T, PlayerRuleError>> function;

    /**
     * Construct a stream with a function that gets the starting value
     */
    public RulePredicateStream(Function<PlayerRuleContext, Result<T, PlayerRuleError>> function) {
        this.function = function;
    }

    /**
     * Construct a rule predicate stream with no value
     */
    public static RulePredicateStream<Void> empty() {
        return new RulePredicateStream<>((context) -> Result.ok());
    }

    /**
     * Return a stream with any values that pass the filter function (didn't result in an error)
     * @param predicate A function that returns an error if a value should not continue
     */
    public RulePredicateStream<T> filter(BiFunction<PlayerRuleContext, T, Optional<PlayerRuleError>> predicate) {
        return new RulePredicateStream<>((context) -> {
            Result<T, PlayerRuleError> result = function.apply(context);
            if(result.isError()) {
                return result;
            }

            Optional<PlayerRuleError> optionalError = predicate.apply(context, result.getValue());
            if(optionalError.isPresent()) {
                return Result.error(optionalError.get());
            }

            return result;
        });
    }

    /**
     * Return a stream with any values that pass the filter function (didn't result in an error)
     * @param predicate A function that returns an error if a value should not continue
     */
    public RulePredicateStream<T> filter(Function<PlayerRuleContext, Optional<PlayerRuleError>> predicate) {
        return filter((context, value) -> predicate.apply(context));
    }

    /**
     * Return a stream with any values where the filter function returned true
     * @param predicate A function that returns false if a value should not continue
     * @param error The error to pass along if the predicate returns false
     */
    public RulePredicateStream<T> filter(BiPredicate<PlayerRuleContext, T> predicate, PlayerRuleError error) {
        return filter((context, value) -> predicate.test(context, value) ? Optional.empty() : Optional.of(error));
    }

    /**
     * Return a stream with any values where the filter function returned true
     * @param predicate A function that returns false if a value should not continue
     * @param error The error to pass along if the predicate returns false
     */
    public RulePredicateStream<T> filter(Predicate<PlayerRuleContext> predicate, PlayerRuleError error) {
        return filter((context, value) -> predicate.test(context) ? Optional.empty() : Optional.of(error));
    }

    /**
     * Return a stream where the elements have been replaced with the ones returned by the map function
     * @param <E> The return type for the map function
     * @param mapFunction A function that maps the values from the stream to values for the new stream
     */
    public <E> RulePredicateStream<E> map(BiFunction<PlayerRuleContext, T, Result<E, PlayerRuleError>> mapFunction) {
        return new RulePredicateStream<>((context) -> {
            Result<T, PlayerRuleError> result = function.apply(context);
            if(result.isError()) {
                return (Result<E, PlayerRuleError>) result;
            }

            return mapFunction.apply(context, result.getValue());
        });
    }

    /**
     * Create a predicate with any errors from this stream
     *
     * The final value of the stream will be discarded
     */
    public Optional<PlayerRuleError> test(PlayerRuleContext context) {
        Result<T, PlayerRuleError> result = function.apply(context);
        if(result.isError()) {
            return Optional.of(result.getError());
        }

        return Optional.empty();
    }
}
