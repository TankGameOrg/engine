package pro.trevor.tankgame.rule.impl;

import java.util.HashSet;
import java.util.Set;

import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.Tank;

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
}