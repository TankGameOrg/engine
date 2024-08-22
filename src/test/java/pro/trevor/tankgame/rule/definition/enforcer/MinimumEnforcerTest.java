package pro.trevor.tankgame.rule.definition.enforcer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class MinimumEnforcerTest {
    @Test
    public void overMinimum() {
        AttributeContainer attrObj = new AttributeContainer();
        Attribute.DURABILITY.to(attrObj, 3);

        MinimumEnforcer<AttributeContainer,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMinimumWithAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        Attribute.DURABILITY.to(attrObj, 3);
        Attribute.MAX_DURABILITY.to(attrObj, 1); // Note: This test uses MAX_DURABILITY as MIN_DURABILITY

        MinimumEnforcer<AttributeContainer,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 5);
        min.enforce(null, attrObj);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void overMinimumWithoutAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        Attribute.DURABILITY.to(attrObj, 3);

        MinimumEnforcer<AttributeContainer,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMinimum() {
        AttributeContainer attrObj = new AttributeContainer();
        Attribute.DURABILITY.to(attrObj, 0);

        MinimumEnforcer<AttributeContainer,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(1, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMinimumWithAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        Attribute.DURABILITY.to(attrObj, 0);
        Attribute.MAX_DURABILITY.to(attrObj, 1); // Note: This test uses MAX_DURABILITY as MIN_DURABILITY

        MinimumEnforcer<AttributeContainer,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, -1);
        min.enforce(null, attrObj);
        assertEquals(1, Attribute.DURABILITY.unsafeFrom(attrObj));
    }

    @Test
    public void underMinimumWithoutAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        Attribute.DURABILITY.to(attrObj, 0);

        MinimumEnforcer<AttributeContainer,Integer> min = new MinimumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 1);
        min.enforce(null, attrObj);
        assertEquals(1, Attribute.DURABILITY.unsafeFrom(attrObj));
    }
}
