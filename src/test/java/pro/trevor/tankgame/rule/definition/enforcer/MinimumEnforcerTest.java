package pro.trevor.tankgame.rule.definition.enforcer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;

public class MinimumEnforcerTest {
    @Test
    public void overMinimum() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 3);

        MinimumEnforcer<AttributeObject,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMinimumWithAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 3);
        Attribute.MAX_DURABILITY.to(attrObj, 1); // Note: This test uses MAX_DURABILITY as MIN_DURABILITY

        MinimumEnforcer<AttributeObject,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 5);
        min.enforce(null, attrObj);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMinimumWithoutAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 3);

        MinimumEnforcer<AttributeObject,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMinimum() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 0);

        MinimumEnforcer<AttributeObject,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(1, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMinimumWithAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 0);
        Attribute.MAX_DURABILITY.to(attrObj, 1); // Note: This test uses MAX_DURABILITY as MIN_DURABILITY

        MinimumEnforcer<AttributeObject,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, -1);
        min.enforce(null, attrObj);
        assertEquals(1, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMinimumWithoutAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 0);

        MinimumEnforcer<AttributeObject,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(1, Attribute.DURABILITY.unsafeFrom(attrObj));
    }
}
