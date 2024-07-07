package pro.trevor.tankgame.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

/**
 * Annotate classes in this package with RulesetType to define a ruleset. This ruleset is picked up at runtime by
 * ReflectionUtil. The name field is used to place the ruleset in a map within the CLI. This map is then used to
 * facilitate a commend in which the user selects which ruleset to use.
 *
 * @note The annotated type must extend IRuleset and have a constructor accepting no arguments.
 */
public @interface RulesetType {
    String name();
}
