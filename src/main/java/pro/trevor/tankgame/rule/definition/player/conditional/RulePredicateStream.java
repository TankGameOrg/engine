package pro.trevor.tankgame.rule.definition.player.conditional;

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
    public RulePredicateStream<T> filter(BiFunction<PlayerRuleContext, T, Result<Void, PlayerRuleError>> predicate) {
        return filterInternal((context, prevResult) -> predicate.apply(context, prevResult.getValue()));
    }

    /**
     * Return a stream with any values that pass the filter function (didn't result in an error)
     * @param predicate A function that returns an error if a value should not continue
     */
    public RulePredicateStream<T> filter(Function<PlayerRuleContext, Result<Void, PlayerRuleError>> predicate) {
        return filterInternal((context, prevResult) -> predicate.apply(context));
    }

    /**
     * Return a stream with any values where the filter function returned true
     * @param predicate A function that returns false if a value should not continue
     * @param error The error to pass along if the predicate returns false
     */
    public RulePredicateStream<T> filter(BiPredicate<PlayerRuleContext, T> predicate, PlayerRuleError error) {
        return filterInternal((context, prevResult) -> predicate.test(context, prevResult.getValue()) ? Result.ok() : Result.error(error));
    }

    /**
     * Return a stream with any values where the filter function returned true
     * @param predicate A function that returns false if a value should not continue
     * @param error The error to pass along if the predicate returns false
     */
    public RulePredicateStream<T> filter(Predicate<PlayerRuleContext> predicate, PlayerRuleError error) {
        return filterInternal((context, prevResult) -> predicate.test(context) ? Result.ok() : Result.error(error));
    }

    private RulePredicateStream<T> filterInternal(BiFunction<PlayerRuleContext, Result<T, PlayerRuleError>, Result<Void, PlayerRuleError>> predicate) {
        return new RulePredicateStream<>((context) -> {
            Result<T, PlayerRuleError> result = function.apply(context);
            if(result.isError()) {
                return result;
            }

            Result<Void, PlayerRuleError> optionalError = predicate.apply(context, result);
            if(optionalError.isError()) {
                return Result.error(optionalError.getError());
            }

            return result;
        });
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
                return Result.error(result.getError());
            }

            return mapFunction.apply(context, result.getValue());
        });
    }

    /**
     * Create a predicate with any errors from this stream
     *
     * The final value of the stream will be discarded
     */
    public Result<Void, PlayerRuleError> test(PlayerRuleContext context) {
        Result<T, PlayerRuleError> result = function.apply(context);
        if(result.isError()) {
            return Result.error(result.getError());
        }

        return Result.ok();
    }
}
