package pro.trevor.tankgame.rule.impl.shared.rule;

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
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.ArmisticeCouncil;
import pro.trevor.tankgame.state.meta.Council;

public class TickRules {
    public static final MetaTickActionRule<Board> INCREMENT_DAY_ON_TICK = new MetaTickActionRule<>(
            (s, n) -> {
                s.setTick(s.getTick() + 1);
            });

    public static <T extends GenericTank> TickActionRule<T> GetDistributeGoldToTanksRule() {
        return new TickActionRule<T>(
                (s, t) -> {
                    if (!Attribute.DEAD.from(t).orElse(false) && Attribute.GOLD.in(t)) {
                        if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                            Set<Position> mines = new HashSet<>();
                            findAllConnectedMines(mines, s, t.getPosition());
                            int tanks = (int) mines.stream().filter(
                                    (p) -> s.getBoard().getUnit(p).orElse(null) instanceof GenericTank tank
                                            && !Attribute.DEAD.from(tank).orElse(false))
                                    .count();
                            int goldToGain = mines.size() / tanks;
                            Attribute.GOLD.to(t, Attribute.GOLD.unsafeFrom(t) + goldToGain);
                        }
                    }
                });
    }

    public static <T extends GenericElement & ITickElement> TickActionRule<T> GetGrantActionPointsOnTickRule(
            int amount) {
        return new TickActionRule<T>(
                (s, t) -> {
                    if (!Attribute.DEAD.from(t).orElse(false) && Attribute.ACTION_POINTS.in(t)) {
                        Attribute.ACTION_POINTS.to(t, Attribute.ACTION_POINTS.unsafeFrom(t) + amount);
                    }
                });
    }

    public static TickActionRule<GenericTank> GetHealTanksInHealthPoolRule() {
        return new TickActionRule<>(
                (s, t) -> {
                    if (Attribute.DEAD.from(t).orElse(false) || !Attribute.DURABILITY.in(t))
                        return;
                    if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof HealthPool healthPool) {
                        Attribute.DURABILITY.to(t, Attribute.DURABILITY.unsafeFrom(t) + healthPool.getRegenAmount());
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
                                    && !Attribute.DEAD.from(tank).orElse(false))
                            .count();
                    int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);
                    s.getCouncil().setCoffer(s.getCouncil().getCoffer() + goldToGain);
                }
            });

    public static final MetaTickActionRule<ArmisticeCouncil> ARMISTICE_VIA_COUNCIL = new MetaTickActionRule<>(
            (s, c) -> {
                int totalCouncilMembers = c.getCouncillors().size() + c.getSenators().size();
                c.setArmisticeVoteCount(c.getArmisticeVoteCount() + totalCouncilMembers);
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

                    c.setCoffer(c.getCoffer() + income);
                });
    }
}
