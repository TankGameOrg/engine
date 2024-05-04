package pro.trevor.tankgame.rule.impl.shared;

import static pro.trevor.tankgame.util.Util.findAllConnectedMines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.AbstractPositionedFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.meta.ArmisticeCouncil;
import pro.trevor.tankgame.state.meta.Council;

public class TickRules 
{
    public static final TickActionRule<Tank> DISTRIBUTE_GOLD_TO_TANKS_RULE = new TickActionRule<>(
        (s, t) -> {
            if (!t.isDead()) {
                t.setActions(t.getActions() + 1);
                if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                    Set<Position> mines = new HashSet<>();
                    findAllConnectedMines(mines, s, t.getPosition());
                    int tanks = (int) mines.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank && !tank.isDead()).count();
                    int goldToGain = mines.size() / tanks;
                    t.setGold(t.getGold() + goldToGain);
                }
            }
        }
    );

    public static final MetaTickActionRule<Board> GOLD_MINE_REMAINDER_GOES_TO_COFFER = new MetaTickActionRule<>(
        (s, b) -> {
            List<Position> mines = b.gatherFloors(GoldMine.class).stream().map(AbstractPositionedFloor::getPosition).toList();
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
                int tanks = (int) mine.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank && !tank.isDead()).count();
                int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);
                s.getCouncil().setCoffer(s.getCouncil().getCoffer() + goldToGain);
            }
        }
    );

    public static final MetaTickActionRule<ArmisticeCouncil> ARMISTICE_VIA_COUNCIL = new MetaTickActionRule<>(
        (s, c) -> {
            int totalCouncillors = c.getCouncillors().size() + c.getSenators().size();
            c.setArmisticeVotesRemaining(c.getArmisticeVotesRemaining() - totalCouncillors);
        }
    );
}
