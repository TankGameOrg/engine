package pro.trevor.tankgame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SampleTest 
{
    @Test
    public void BasicTest()
    {
        assertEquals(2, 1 + 1, "1 + 1 should equal 2");
        assertFalse(1 == 2, "1 does not equal 2");
    }


    /*
    @Test
    public void FailingTestExample()
    {
        assertTrue(1 == 2, "This should fail");
    }
    */
}
