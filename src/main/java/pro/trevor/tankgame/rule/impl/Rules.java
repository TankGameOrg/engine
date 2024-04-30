package pro.trevor.tankgame.rule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.version3.Ruleset.PlayerAction;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.AbstractPositionedFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.Wall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.range.TankRange;

import static pro.trevor.tankgame.util.Util.*;

public class Rules {
    
    public static TickActionRule<Tank> DISTRIBUTE_GOLD_TO_TANKS_RULE = new TickActionRule<>((s, t) -> {
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
    });

    public static MetaTickActionRule<Board> GOLD_MINE_REMAINDER_GOES_TO_COFFER = new MetaTickActionRule<>((s, b) -> {
        List<Position> mines = b.gatherFloors(GoldMine.class).stream()
                .map(AbstractPositionedFloor::getPosition).toList();
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
    });

    public static ConditionalRule<Wall> DESTROY_WALL_ON_ZERO_DURABILITY = new ConditionalRule<>((s, w) -> w.getDurability() == 0, 
                                                                                      (s, w) -> {
        s.getBoard().putUnit(new EmptyUnit(w.getPosition()));
        if (isOrthAdjToMine(s, w.getPosition())) {
            s.getBoard().putFloor(new GoldMine(w.getPosition()));
        }
    });

    public static PlayerActionRule<Council> GetCofferCostStimulusRule(int cost)
    {
        return new PlayerActionRule<>(PlayerAction.STIMULUS,
            (s, c, n) -> {
                Tank t = toType(n[0], Tank.class);
                return !t.isDead() && c.getCoffer() >= cost;
            },
            (s, c, n) -> {
                Tank t = toType(n[0], Tank.class);
                t.setActions(t.getActions() + 1);
                c.setCoffer(c.getCoffer() - cost);
            }, 
            new TankRange<Council>("target")
        );
    }

    public static PlayerActionRule<Council> GetRule_CofferCost_GrantLife(int cost)
    {
        return new PlayerActionRule<>(PlayerAction.GRANT_LIFE,
            (s, c, n) -> c.getCoffer() >= cost,
            (s, c, n) -> {
                Tank t = toType(n[0], Tank.class);
                t.setDurability(t.getDurability() + 1);
                c.setCoffer(c.getCoffer() - cost);
            },
            new TankRange<Council>("target")
        );
    }
}