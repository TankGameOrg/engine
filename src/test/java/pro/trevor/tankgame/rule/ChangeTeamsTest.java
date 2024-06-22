package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.rule.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestUtilities;

public class ChangeTeamsTest {

    @Test
    public void NormalChangeTeamTest()
    {
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").finish();
        State state = TestUtilities.generateBoard(3, 3, tank); // Only 1 tank in the game

        assertFalse(changeTeamRule.canApply(state, tank, "A"));
        assertTrue(changeTeamRule.canApply(state, tank, "B"));
        assertTrue(changeTeamRule.canApply(state, tank, "C"));

        changeTeamRule.apply(state, tank, "B");

        assertTrue(Attribute.BETRAYER.unsafeFrom(tank));
        assertTrue(Attribute.TEAM.unsafeFrom(tank) == "B");
        assertFalse(changeTeamRule.canApply(state, tank, "A"));
        assertFalse(changeTeamRule.canApply(state, tank, "B"));
        assertFalse(changeTeamRule.canApply(state, tank, "C"));
    }

    @Test
    public void IllegalTeamNameTest()
    {
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").finish();
        State state = TestUtilities.generateBoard(3, 3, tank); // Only 1 tank in the game

        assertFalse(changeTeamRule.canApply(state, tank, "foo"));
        assertThrows(Error.class, () -> changeTeamRule.apply(state, tank, "foo"));
    }

    @Test
    public void RepeatedTeamNamesTest()
    {
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("A");

        assertThrows(Error.class, () -> PlayerRules.GetChangeTeamRule(teams));
    }

    @Test
    public void NotOnTeamChangeTeamTest()
    {
        List<String> teams = new ArrayList<>();
        teams.add("A");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);
        Tank tank = TankBuilder.buildV3Tank().finish();
        State state = TestUtilities.generateBoard(3, 3, tank); // Only 1 tank in the game

        assertTrue(changeTeamRule.canApply(state, tank, "A"));
    }

    @Test
    public void NotOnTeamFalseBetrayerChangeTeamTest()
    {
        List<String> teams = new ArrayList<>();
        teams.add("A");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.BETRAYER, false).finish();
        State state = TestUtilities.generateBoard(3, 3, tank); // Only 1 tank in the game

        assertTrue(changeTeamRule.canApply(state, tank, "A"));
    }

    @Test
    public void NotOnTeamTrueBetrayerChangeTeamTest()
    {
        List<String> teams = new ArrayList<>();
        teams.add("A");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.BETRAYER, true).finish();
        State state = TestUtilities.generateBoard(3, 3, tank); // Only 1 tank in the game

        assertFalse(changeTeamRule.canApply(state, tank, "A"));
    }

    @Test
    public void Below50PercentOnTeamTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank tank1 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").at(new Position(0,0)).finish();

        Tank tank2 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(1,0)).finish();
        Tank tank3 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(2,0)).finish();
        Tank tank4 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(0,1)).finish();

        Tank tank5 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(1,1)).finish();
        Tank tank6 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(2,1)).finish();
        Tank tank7 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(0,2)).finish();
        Tank tank8 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, tank1, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        // ACT/ASSERT
        assertTrue(changeTeamRule.canApply(state, tank1, "A"));
    }
    
    @Test
    public void Exact50PercentOnTeamTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank tank1 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").at(new Position(0,0)).finish();

        Tank tank2 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(1,0)).finish();
        Tank tank3 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(2,0)).finish();
        Tank tank4 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(0,1)).finish();
        Tank tank5 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(1,1)).finish();

        Tank tank6 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(2,1)).finish();
        Tank tank7 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(0,2)).finish();
        Tank tank8 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, tank1, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        // ACT/ASSERT
        assertFalse(changeTeamRule.canApply(state, tank1, "A"));
    }

    @Test
    public void Over50PercentOnTeamTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank tank1 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").at(new Position(0,0)).finish();

        Tank tank2 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(1,0)).finish();
        Tank tank3 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(2,0)).finish();
        Tank tank4 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").at(new Position(0,1)).finish();

        Tank tank5 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(1,1)).finish();
        Tank tank6 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(0,2)).finish();
        Tank tank7 = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, tank1, tank2, tank3, tank4, tank5, tank5, tank6, tank7);

        // ACT/ASSERT
        changeTeamRule.apply(state, tank7, "A");
        assertFalse(changeTeamRule.canApply(state, tank1, "A")); // tank7 can join when 3/7 are on team, but tank1 cannot join once it's 4/7
    }

    @Test
    public void ZeroPercentDeadTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(0,0)).finish();
        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(1,1)).finish();

        Tank tank6   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(2,1)).finish();
        Tank tank7   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(0,2)).finish();
        Tank tank8   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        // ACT
        boolean canJoinB = changeTeamRule.canApply(state, subject, "B");

        // ASSERT
        assertTrue(canJoinB);
    }

    @Test
    public void Below50PercentDeadTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(0,0)).finish();
        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, true).at(new Position(1,1)).finish();

        Tank tank6   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(2,1)).finish();
        Tank tank7   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(0,2)).finish();
        Tank tank8   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        // ACT
        boolean canJoinB = changeTeamRule.canApply(state, subject, "B");

        // ASSERT
        assertTrue(canJoinB);
    }

    @Test
    public void Exact50PercentDeadTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(0,0)).finish();
        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, true).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, true).at(new Position(1,1)).finish();

        Tank tank6   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(2,1)).finish();
        Tank tank7   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(0,2)).finish();
        Tank tank8   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        // ACT
        boolean canJoinB = changeTeamRule.canApply(state, subject, "B");

        // ASSERT
        assertTrue(canJoinB);
    }

    @Test
    public void Over50PercentDeadTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(0,0)).finish();

        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, true).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(1,1)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5);

        // ACT
        boolean canJoinB = changeTeamRule.canApply(state, subject, "B");

        // ASSERT
        assertFalse(canJoinB);
    }

    @Test
    public void JoinCompletelyDeadTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(0,0)).finish();

        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(1,1)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5);

        // ACT
        boolean canJoinC = changeTeamRule.canApply(state, subject, "C");

        // ASSERT
        assertTrue(canJoinC);
    }

    @Test
    public void SubjectIsCouncilorTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, true).at(new Position(0,0)).finish();
        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(1,1)).finish();

        Tank tank6   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(2,1)).finish();
        Tank tank7   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(0,2)).finish();
        Tank tank8   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        // ACT
        boolean canJoinB = changeTeamRule.canApply(state, subject, "B");

        // ASSERT
        assertTrue(canJoinB);
    }

    @Test
    public void BetrayerFromLifeTest()
    {
        // Arrange
        List<String> teams = new ArrayList<>();
        teams.add("A");
        teams.add("B");
        teams.add("C");
        PlayerActionRule<GenericTank> changeTeamRule = PlayerRules.GetChangeTeamRule(teams);

        Tank subject = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(0,0)).finish();
        Tank tank2   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(1,0)).finish();
        Tank tank3   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "A").with(Attribute.DEAD, false).at(new Position(2,0)).finish();

        Tank tank4   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(0,1)).finish();
        Tank tank5   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "B").with(Attribute.DEAD, false).at(new Position(1,1)).finish();

        Tank tank6   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, true).at(new Position(2,1)).finish();
        Tank tank7   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(0,2)).finish();
        Tank tank8   = TankBuilder.buildV3Tank().with(Attribute.TEAM, "C").with(Attribute.DEAD, false).at(new Position(1,2)).finish();
        State state = TestUtilities.generateBoard(3, 3, subject, tank2, tank3, tank4, tank5, tank5, tank6, tank7, tank8);

        ConditionalRule<GenericTank> killTankOnZeroDurabilityRule = ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule();

        // ACT
        changeTeamRule.apply(state, subject, "B");
        boolean betrayerStatusBeforeDeath = Attribute.BETRAYER.unsafeFrom(subject);
        Attribute.DURABILITY.to(subject, 0);
        killTankOnZeroDurabilityRule.apply(state, subject);
        boolean betrayerStatusAfterDeath = Attribute.BETRAYER.unsafeFrom(subject);

        // ASSERT
        assertTrue(Attribute.DEAD.unsafeFrom(subject));
        assertTrue(betrayerStatusBeforeDeath);
        assertTrue(betrayerStatusAfterDeath);
        assertFalse(changeTeamRule.canApply(state, subject, "C"));
    }

    // TODO:
    // On team A, not betrayer
    // 8 people in the game
    // 2 people dead
    // Subject is Senator
    // Can join team B
}
