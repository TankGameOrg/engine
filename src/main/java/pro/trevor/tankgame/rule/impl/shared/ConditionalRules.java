package pro.trevor.tankgame.rule.impl.shared;

import static pro.trevor.tankgame.util.Util.isOrthAdjToMine;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;

public class ConditionalRules 
{
    public static final ConditionalRule<BasicWall> DESTROY_WALL_ON_ZERO_DURABILITY = new ConditionalRule<>(
        (s, w) -> w.getDurability() == 0, 
        (s, w) -> {
            s.getBoard().putUnit(new EmptyUnit(w.getPosition()));
            if (isOrthAdjToMine(s, w.getPosition())) {
                s.getBoard().putFloor(new GoldMine(w.getPosition()));
            }
        }
    );

    public static final ConditionalRule<Tank> KILL_OR_DESTROY_TANK_ON_ZERO_DURABILITY = new ConditionalRule<>(
        (s, t) -> t.getDurability() == 0, 
        (s, t) -> {
            if (t.isDead()) {
                s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                String tankPlayer = t.getPlayer();
                s.getCouncil().getCouncillors().remove(tankPlayer);
                s.getCouncil().getSenators().add(tankPlayer);
            } else {
                t.setDead(true);
                t.setActions(0);
                t.setGold(0);
                t.setDurability(3);
                s.getCouncil().getCouncillors().add(t.getPlayer());
            }
        }
    );
}
