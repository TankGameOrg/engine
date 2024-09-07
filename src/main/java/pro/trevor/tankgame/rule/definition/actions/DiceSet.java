package pro.trevor.tankgame.rule.definition.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceSet<T> {
    public static class Die<T> {
        private List<T> sides;
        private String name;

        public Die(String name, List<T> sides) {
            this.name = name;
            this.sides = sides;
        }

        public String getName() {
            return name;
        }

        public List<T> getSides() {
            return sides;
        }

        public T roll(Random random) {
            return sides.get(random.nextInt(sides.size()));
        }
    }

    // The UI defines icons based off these names so they need to match die.js
    public static final Die<Boolean> HIT_DIE = new Die<>("hit die", List.of(true, false));
    public static final Die<Integer> D4 = new Die<>("d4", List.of(1, 2, 3, 4));
    public static final Die<Integer> D6 = new Die<>("d6", List.of(1, 2, 3, 4, 5, 6));

    private int numDice;
    private Die<T> die;

    public DiceSet(int numDice, Die<T> die) {
        this.numDice = numDice;
        this.die = die;
    }

    public int getNumDice() {
        return numDice;
    }

    public Die<T> getDie() {
        return die;
    }

    public List<Die<T>> expandDice() {
        List<Die<T>> dice = new ArrayList<>();
        for(int i = 0; i < numDice; ++i) {
            dice.add(die);
        }
        return dice;
    }
}
