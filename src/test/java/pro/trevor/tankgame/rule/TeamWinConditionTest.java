package pro.trevor.tankgame.rule;

public class TeamWinConditionTest {

    // 1
    // 4 Living Tanks
    // 3 on team A
    // 1 on team B
    // Cannot apply

    // 2
    // 4 living tanks
    // 4 on team A
    // Can apply
    // winner is team A

    // 3
    // 1 living tank
    // 1 on team A
    // Can apply
    // winner is team A

    // 4
    // 0 living tanks
    // Cannot apply
    // EXAMPLE WHERE TWO TANKS, ON OPPOSING TEAMS, WITH 1 LIFE EACH GET HIT BY A DAMAGING TICK ACTION
    // DON'T KNOW HOW GAME ENDS    
}
