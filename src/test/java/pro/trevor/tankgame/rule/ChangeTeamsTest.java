package pro.trevor.tankgame.rule;

public class ChangeTeamsTest {
    // Rule doesn't allow for repeated team names

    // Teams = A, B, C
        // 1
        // Starting with A
        // Can join B
        // Can join C
        // Cannot join A
        // Join B
        // Cannot join A, B or C

        // 2
        // Starting with A
        // Cannot join "foo"

        // 3
        // On team B, not betrayer
        // 8 people in the game
        // 3 people on team A
        // Can join team A
        
        // 4
        // On team B, not betrayer
        // 8 people in the game
        // 4 people on team A
        // Cannot join team A

        // 5
        // Not on team, not betrayer
        // Can join team A
        
        // 6
        // Not on team, is betrayer
        // Cannot join team A

        // 7
        // On team A, not betrayer
        // 8 people in the game
        // 0 people dead
        // Can join team B

        // 8
        // On team A, not betrayer
        // 8 people in the game
        // 2 people dead
        // Can join team B

        // 9
        // On team A, not betrayer
        // 8 people in the game
        // 4 people dead
        // Can join team B

        // 10
        // On team A, not betrayer
        // 8 people in the game
        // 6 people dead
        // Cannot join team B

        // 11
        // On team A, not betrayer
        // 5 people in the game
        // 3 people dead
        // Cannot join team B

        // 12
        // On team A, not betrayer
        // 8 people in the game
        // 2 people dead
        // Subject is Councilor
        // Can join team B

        // 13
        // On team A, not betrayer
        // 8 people in the game
        // 2 people dead
        // Subject is Senator
        // Can join team B
}
