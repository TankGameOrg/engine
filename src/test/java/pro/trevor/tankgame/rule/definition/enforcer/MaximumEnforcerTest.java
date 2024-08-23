package pro.trevor.tankgame.rule.definition.enforcer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class MaximumEnforcerTest {
    @Test
    public void underMaximum() {
        AttributeContainer attrObj = new AttributeContainer();
        attrObj.put(Attribute.DURABILITY, 5);

        MaximumEnforcer<AttributeContainer,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, 7);
        max.enforce(null, attrObj);
        assertEquals(5, attrObj.getUnsafe(Attribute.DURABILITY));
    }

    @Test
    public void underMaximumWithAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        attrObj.put(Attribute.DURABILITY, 5);
        attrObj.put(Attribute.MAX_DURABILITY, 7); // Note: This test uses MAX_DURABILITY as max_DURABILITY

        MaximumEnforcer<AttributeContainer,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 10);
        max.enforce(null, attrObj);
        assertEquals(5, attrObj.getUnsafe(Attribute.DURABILITY));
    }

    @Test
    public void underMaximumWithoutAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        attrObj.put(Attribute.DURABILITY, 5);

        MaximumEnforcer<AttributeContainer,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 7);
        max.enforce(null, attrObj);
        assertEquals(5, attrObj.getUnsafe(Attribute.DURABILITY));
    }

    @Test
    public void overMaximum() {
        AttributeContainer attrObj = new AttributeContainer();
        attrObj.put(Attribute.DURABILITY, 8);

        MaximumEnforcer<AttributeContainer,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, 4);
        max.enforce(null, attrObj);
        assertEquals(4, attrObj.getUnsafe(Attribute.DURABILITY));
    }

    @Test
    public void overMaximumWithAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        attrObj.put(Attribute.DURABILITY, 8);
        attrObj.put(Attribute.MAX_DURABILITY, 6); // Note: This test uses MAX_DURABILITY as max_DURABILITY

        MaximumEnforcer<AttributeContainer,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 10);
        max.enforce(null, attrObj);
        assertEquals(6, attrObj.getUnsafe(Attribute.DURABILITY));
    }

    @Test
    public void overMaximumWithoutAttributeBound() {
        AttributeContainer attrObj = new AttributeContainer();
        attrObj.put(Attribute.DURABILITY, 11);

        MaximumEnforcer<AttributeContainer,Integer> max = new MaximumEnforcer<>(Attribute.DURABILITY, Attribute.MAX_DURABILITY, 9);
        max.enforce(null, attrObj);
        assertEquals(9, attrObj.getUnsafe(Attribute.DURABILITY));
    }
}
