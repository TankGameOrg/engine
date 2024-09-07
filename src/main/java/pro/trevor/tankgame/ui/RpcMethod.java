package pro.trevor.tankgame.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/**
 * Mark a method as callable by an external client when passed to the CLI
 *
 * @note The annotated methods must accept and return a JSONObject
 */
public @interface RpcMethod {
}
