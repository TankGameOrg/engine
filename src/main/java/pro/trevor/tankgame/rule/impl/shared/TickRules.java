package pro.trevor.tankgame.rule.impl.shared;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.Priority;
import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.AbstractFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.floor.HealthPool;
import pro.trevor.tankgame.state.board.floor.Lava;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.LootBox;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.util.Random;
import pro.trevor.tankgame.util.Util;

public class TickRules {
    public static final MetaTickActionRule<Board> INCREMENT_DAY_ON_TICK = new MetaTickActionRule<>(
            (s, n) -> s.put(Attribute.TICK, s.getOrElse(Attribute.TICK, 0) + 1));

    public static final TickActionRule<Tank> DISTRIBUTE_GOLD_TO_TANKS = new TickActionRule<>(
            (s, t) -> {
                if (!t.get(Attribute.DEAD).orElse(false) && t.has(Attribute.GOLD)) {
                    if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                        Set<Position> mines = new HashSet<>();
                        Util.findAllConnectedMines(mines, s, t.getPosition());
                        int tanks = (int) mines.stream().filter(
                                        (p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank
                                                && !tank.get(Attribute.DEAD).orElse(false))
                                .count();
                        int goldToGain = mines.size() / tanks;
                        t.put(Attribute.GOLD, t.getUnsafe(Attribute.GOLD) + goldToGain);
                    }
                }
            });

    public static <T extends GenericElement> TickActionRule<T> GetGrantActionPointsOnTickRule(int amount) {
        return new TickActionRule<T>(
                (s, t) -> {
                    if (!t.get(Attribute.DEAD).orElse(false) && t.has(Attribute.ACTION_POINTS)) {
                        t.put(Attribute.ACTION_POINTS, t.getUnsafe(Attribute.ACTION_POINTS) + amount);
                    }
                });
    }

    public static final TickActionRule<Tank> HEAL_TANK_IN_HEAL_POOL = new TickActionRule<>(
            (s, t) -> {
                if (t.get(Attribute.DEAD).orElse(false) || !t.has(Attribute.DURABILITY))
                    return;
                if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof HealthPool healthPool) {
                    t.put(Attribute.DURABILITY, t.getUnsafe(Attribute.DURABILITY) + healthPool.getRegenAmount());
                }
            });

    public static final TickActionRule<Tank> DAMAGE_TANK_IN_LAVA = new TickActionRule<>(
            (s, t) -> {
                if (t.get(Attribute.DEAD).orElse(false) || !t.has(Attribute.DURABILITY))
                    return;
                if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof Lava lava) {
                    t.put(Attribute.DURABILITY, t.getUnsafe(Attribute.DURABILITY) - lava.getDamage());
                }
            });

    public static final TickActionRule<Tank> RELEASE_SPEED_MODIFICATIONS = new TickActionRule<>(
            (s, t) -> {
                Optional<Integer> previousSpeed = t.get(Attribute.PREVIOUS_SPEED);
                if (previousSpeed.isPresent()) {
                    t.put(Attribute.SPEED, previousSpeed.get());
                    t.remove(Attribute.SLOWED);
                    t.remove(Attribute.HASTENED);
                    t.remove(Attribute.PREVIOUS_SPEED);
                }
            }
    );

    public static TickActionRule<Tank> SET_PLAYER_CAN_LOOT = new TickActionRule<>((state, tank) -> {
        if(!tank.getOrElse(Attribute.DEAD, false)) {
            tank.put(Attribute.PLAYER_CAN_LOOT, true);
        }
    });

    public static TickActionRule<Tank> CLEAR_ONLY_LOOTABLE_BY = new TickActionRule<>((state, tank) -> {
        if(tank.has(Attribute.ONLY_LOOTABLE_BY)) {
            tank.remove(Attribute.ONLY_LOOTABLE_BY);
        }
    });

    public static final MetaTickActionRule<Player> DEAD_PLAYERS_GAIN_POWER = new MetaTickActionRule<>(
            (s, p) -> {
                if (s.getCouncil().isPlayerOnCouncil(p.toRef())) {
                    p.put(Attribute.POWER, p.getOrElse(Attribute.POWER, 0) + 1);
                }
            }
    );

    public static final MetaTickActionRule<Board> GOLD_MINE_REMAINDER_GOES_TO_COFFER = new MetaTickActionRule<>(
            (s, b) -> {
                List<Position> mines = b.gatherFloors(GoldMine.class).stream().map(AbstractFloor::getPosition)
                        .toList();
                List<Set<Position>> allMines = new ArrayList<>();

                for (Position p : mines) {
                    if (allMines.stream().flatMap(Collection::stream).anyMatch(p::equals)) {
                        continue;
                    }
                    Set<Position> thisMine = new HashSet<>();
                   Util.findAllConnectedMines(thisMine, s, p);
                    allMines.add(thisMine);
                }

                for (Set<Position> mine : allMines) {
                    int tanks = (int) mine.stream().filter(
                            (p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank
                                    && !tank.get(Attribute.DEAD).orElse(false))
                            .count();
                    int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);

                    s.getCouncil().put(Attribute.COFFER, s.getCouncil().getOrElse(Attribute.COFFER, 0) + goldToGain);
                }
            });

    public static final MetaTickActionRule<Council> ARMISTICE_VIA_COUNCIL = new MetaTickActionRule<>(
            (s, c) -> {
                int totalCouncilMembers = c.getCouncillors().size() + c.getSenators().size();
                c.put(Attribute.ARMISTICE, c.getOrElse(Attribute.ARMISTICE, 0) + totalCouncilMembers);
            });

    public static MetaTickActionRule<Council> GetCouncilBaseIncomeRule(int goldPerCouncilor, int goldPerSenator) {
        if (goldPerCouncilor < 0)
            throw new Error("Illegal goldPerCouncilor value: " + goldPerCouncilor);
        if (goldPerSenator < 0)
            throw new Error("Illegal goldPerSenator value: " + goldPerSenator);

        return new MetaTickActionRule<>(
                (s, c) -> {
                    int councilorCount = c.getCouncillors().size();
                    int senatorCount = c.getSenators().size();

                    int income = (goldPerCouncilor * councilorCount) + (goldPerSenator * senatorCount);

                    c.put(Attribute.COFFER, c.getOrElse(Attribute.COFFER, 0) + income);
                });
    }

    /**
     * This rule counts down the DAYS_REMAINING for any element that has the attribute and removes the element
     * when it reaches zero
     */
    public static final TickActionRule<GenericElement> DECAY_TIMEBOUND_ELEMENT = new TickActionRule<>((state, element) -> {
        if(!element.has(Attribute.DAYS_REMAINING)) return;

        int remaining = element.getUnsafe(Attribute.DAYS_REMAINING);
        --remaining;

        if(remaining > 0) {
            element.put(Attribute.DAYS_REMAINING, remaining);
            return;
        }

        if(element instanceof IUnit) {
            state.getBoard().putUnit(new EmptyUnit(element.getPosition()));
        } else {
            state.getBoard().putFloor(new WalkableFloor(element.getPosition()));
        }
    });

    public static MetaTickActionRule<Board> spawnInRandomEmptySpace(int spawnedPerDay, Predicate<State> canSpawnToday, BiPredicate<State, Position> isSpawnable, BiConsumer<State, Position> spawn, Priority priority) {
        return new MetaTickActionRule<>((state, board) -> {
            if(!canSpawnToday.test(state)) return;

            List<Position> spawnableLocations = new ArrayList<>(
                board.getAllPositions().stream()
                    .filter((position) -> board.isEmpty(position) && isSpawnable.test(state, position))
                    .toList());

            if(spawnableLocations.isEmpty()) return;

            int remainingToSpawn = spawnedPerDay;
            while(remainingToSpawn-- > 0 && spawnableLocations.size() > 0) {
                int index = state.getUnsafe(Attribute.RANDOM).nextInt(spawnableLocations.size());
                Position spawnLocation = spawnableLocations.remove(index);
                spawn.accept(state, spawnLocation);
            }
        }, priority);
    }

    public static MetaTickActionRule<Board> spawnLootBoxInRandomSpace(int daysBetweenSpawn, int daysRemaining, int spawnedPerDay) {
        Predicate<State> canSpawnToday = (state) -> state.getUnsafe(Attribute.TICK) % daysBetweenSpawn == 1;

        BiPredicate<State, Position> isSpawnable = (state, position) -> {
            return state.getBoard()
                .gather(Tank.class).stream()
                .filter((tank) -> !tank.getOrElse(Attribute.DEAD, false) && tank.getPosition().distanceFrom(position) <= 2)
                .findAny().isEmpty();
        };

        BiConsumer<State, Position> spawnLootBox = (state, position) -> {
            LootBox lootBox = new LootBox(position);
            lootBox.put(Attribute.DAYS_REMAINING, daysRemaining);
            state.getBoard().putUnit(lootBox);
        };

        return spawnInRandomEmptySpace(spawnedPerDay, canSpawnToday, isSpawnable, spawnLootBox, Priority.LOWER);
    }
}
