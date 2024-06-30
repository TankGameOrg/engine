package pro.trevor.tankgame.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

/**
 * Annotate classes in this package with JsonType to define a json-compatible class. This class is picked up at runtime
 * by ReflectionUtil. The name field is used to place the class in a map for use in the Codec. This map is used to
 * facilitate constructing the annotated class. To avoid breaking changes, the name field should stay constant between
 * versions.
 *
 * @note The annotated type must extend IJsonObject and have a constructor accepting a single JSONObject.
 */
public @interface JsonType {
    String name();
}
