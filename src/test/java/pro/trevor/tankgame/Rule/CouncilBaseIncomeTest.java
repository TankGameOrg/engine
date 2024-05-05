package pro.trevor.tankgame.Rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.TestUtil.TestUtilities;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.impl.shared.TickRules;
import pro.trevor.tankgame.state.meta.Council;

public class CouncilBaseIncomeTest 
{
    // Test Cases
    // incomePerCouncilor = {zero, positive}
    // incomePerSenator = {zero, positive}

    @ParameterizedTest()
    @CsvSource({
        "0,  0,  0,  0",
        "0,  1,  0,  1",
        "0,  0,  1,  3",
        "0,  1,  1,  4",
        "0,  2,  0,  2",
        "0,  0,  2,  6",
        "0,  2,  2,  8",
        "15, 3,  1,  21",
        "9,  0,  0,  9",
    })
    public void TestCouncilBaseIncome(int startingCoffer, int numCouncilors, int numSenators, int expectedCoffer)
    {
        Council c = TestUtilities.BuildTestCouncil(startingCoffer, numCouncilors, numSenators);
        MetaTickActionRule<Council> rule = TickRules.GetCouncilBaseIncomeRule(1, 3);

        rule.apply(TestUtilities.DUMMY_STATE, c);

        assertEquals(expectedCoffer, c.getCoffer());
    }

    @Test
    public void NegativeCouncilorIncome()
    {
        assertThrows(Error.class, () -> TickRules.GetCouncilBaseIncomeRule(-5, 0));
    }

    @Test
    public void NegativeSenatorIncome()
    {
        assertThrows(Error.class, () -> TickRules.GetCouncilBaseIncomeRule(0, -5));
    }

    @ParameterizedTest()
    @CsvSource({
        "0,  0,  0,  0,  0", // 0 income, no change to coffer
        "0,  0,  1,  0,  0",
        "0,  0,  0,  1,  0",
        "0,  0,  1,  1,  0",
        "1,  0,  0,  0,  0", // Income only for councilors
        "1,  0,  1,  0,  1",
        "1,  0,  0,  1,  0",
        "1,  0,  1,  1,  1",
        "0,  1,  0,  0,  0", // Income only for senators
        "0,  1,  1,  0,  0",
        "0,  1,  0,  1,  1",
        "0,  1,  1,  1,  1",
    })
    public void CouncilsWithZeroBaseIncome(int coucilorIncome, int senatorIncome, int numCouncilors, int numSenators, int expectedCoffer)
    {
        Council c = TestUtilities.BuildTestCouncil(0, numCouncilors, numSenators);
        MetaTickActionRule<Council> rule = TickRules.GetCouncilBaseIncomeRule(coucilorIncome, senatorIncome);

        rule.apply(TestUtilities.DUMMY_STATE, c);

        assertEquals(expectedCoffer, c.getCoffer());
    }

    @Test
    public void CouncilBaseIncomeMultipleApplications()
    {
        Council c = TestUtilities.BuildTestCouncil(5, 12, 3);
        MetaTickActionRule<Council> rule = TickRules.GetCouncilBaseIncomeRule(1, 3);

        rule.apply(TestUtilities.DUMMY_STATE, c);
        assertEquals(26, c.getCoffer());

        rule.apply(TestUtilities.DUMMY_STATE, c);
        assertEquals(47, c.getCoffer());

        rule.apply(TestUtilities.DUMMY_STATE, c);
        assertEquals(68, c.getCoffer());
    }
}
