package pro.trevor.tankgame.rule.definition.enforcer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;

public class MaximumEnforcerTest {
    @Test
    public void underMaximum() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 5);

        MaximumEnforcer<AttributeObject,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, 7);
        max.enforce(null, attrObj);
        assertEquals(5, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMaximumWithAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 5);
        Attribute.MAX_DURABILITY.to(attrObj, 7); // Note: This test uses MAX_DURABILITY as max_DURABILITY

        MaximumEnforcer<AttributeObject,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 10);
        max.enforce(null, attrObj);
        assertEquals(5, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMaximumWithoutAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 5);

        MaximumEnforcer<AttributeObject,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 7);
        max.enforce(null, attrObj);
        assertEquals(5, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMaximum() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 8);

        MaximumEnforcer<AttributeObject,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, 4);
        max.enforce(null, attrObj);
        assertEquals(4, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMaximumWithAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 8);
        Attribute.MAX_DURABILITY.to(attrObj, 6); // Note: This test uses MAX_DURABILITY as max_DURABILITY

        MaximumEnforcer<AttributeObject,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 10);
        max.enforce(null, attrObj);
        assertEquals(6, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMaximumWithoutAttributeBound() {
        AttributeObject attrObj = new AttributeObject();
        Attribute.DURABILITY.to(attrObj, 11);

        MaximumEnforcer<AttributeObject,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 9);
        max.enforce(null, attrObj);
        assertEquals(9, Attribute.DURABILITY.unsafeFrom(attrObj));
    }
}
