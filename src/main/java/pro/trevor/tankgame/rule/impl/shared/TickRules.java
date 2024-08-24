package pro.trevor.tankgame.rule.impl.shared;

import static pro.trevor.tankgame.util.Util.findAllConnectedMines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.AbstractFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.floor.HealthPool;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.LootBox;
import pro.trevor.tankgame.state.meta.Council;

public class TickRules {
    public static final MetaTickActionRule<Board> INCREMENT_DAY_ON_TICK = new MetaTickActionRule<>(
            (s, n) -> s.put(Attribute.TICK, s.getOrElse(Attribute.TICK, 0) + 1));

    public static <T extends GenericTank> TickActionRule<T> GetDistributeGoldToTanksRule() {
        return new TickActionRule<T>(
                (s, t) -> {
                    if (!t.get(Attribute.DEAD).orElse(false) && t.has(Attribute.GOLD)) {
                        if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                            Set<Position> mines = new HashSet<>();
                            findAllConnectedMines(mines, s, t.getPosition());
                            int tanks = (int) mines.stream().filter(
                                    (p) -> s.getBoard().getUnit(p).orElse(null) instanceof GenericTank tank
                                            && !tank.get(Attribute.DEAD).orElse(false))
                                    .count();
                            int goldToGain = mines.size() / tanks;
                            t.put(Attribute.GOLD, t.getUnsafe(Attribute.GOLD) + goldToGain);
                        }
                    }
                });
    }

    public static <T extends GenericElement & ITickElement> TickActionRule<T> GetGrantActionPointsOnTickRule(
            int amount) {
        return new TickActionRule<T>(
                (s, t) -> {
                    if (!t.get(Attribute.DEAD).orElse(false) && t.has(Attribute.ACTION_POINTS)) {
                        t.put(Attribute.ACTION_POINTS, t.getUnsafe(Attribute.ACTION_POINTS) + amount);
                    }
                });
    }

    public static TickActionRule<GenericTank> GetHealTanksInHealthPoolRule() {
        return new TickActionRule<>(
                (s, t) -> {
                    if (t.get(Attribute.DEAD).orElse(false) || !t.has(Attribute.DURABILITY))
                        return;
                    if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof HealthPool healthPool) {
                        t.put(Attribute.DURABILITY, t.getUnsafe(Attribute.DURABILITY) + healthPool.getRegenAmount());
                    }
                });
    }

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
                    findAllConnectedMines(thisMine, s, p);
                    allMines.add(thisMine);
                }

                for (Set<Position> mine : allMines) {
                    int tanks = (int) mine.stream().filter(
                            (p) -> s.getBoard().getUnit(p).orElse(null) instanceof GenericTank tank
                                    && !tank.get(Attribute.DEAD).orElse(false))
                            .count();
                    int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);

                    s.getCouncil().put(Attribute.COFFER, s.getCouncil().getOrElse(Attribute.COFFER, 0) + goldToGain);
                }
            });

    public static final MetaTickActionRule<Council> ARMISTICE_VIA_COUNCIL = new MetaTickActionRule<>(
            (s, c) -> {
                int totalCouncilMembers = c.getCouncillors().size() + c.getSenators().size();
                c.put(Attribute.ARMISTICE_COUNT, c.getOrElse(Attribute.ARMISTICE_COUNT, 0) + totalCouncilMembers);
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

    public static TickActionRule<GenericTank> SET_PLAYER_CAN_LOOT = new TickActionRule<>((state, tank) -> {
        if(!tank.getOrElse(Attribute.DEAD, false)) {
            tank.put(Attribute.PLAYER_CAN_LOOT, true);
        }
    });

    public static TickActionRule<GenericTank> CLEAR_ONLY_LOOTABLE_BY = new TickActionRule<>((state, tank) -> {
        if(tank.has(Attribute.ONLY_LOOTABLE_BY)) {
            tank.remove(Attribute.ONLY_LOOTABLE_BY);
        }
    });

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
}
