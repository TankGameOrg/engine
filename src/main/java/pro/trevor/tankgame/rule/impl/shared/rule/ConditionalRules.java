package pro.trevor.tankgame.rule.impl.shared.rule;

import static pro.trevor.tankgame.util.Util.isOrthAdjToMine;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.state.attribute.Attributes;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.ArmisticeCouncil;

import java.util.stream.Collectors;

public class ConditionalRules {
    public static final ConditionalRule<BasicWall> DESTROY_WALL_ON_ZERO_DURABILITY = new ConditionalRule<>(
            (s, w) -> w.getDurability() == 0,
            (s, w) -> {
                s.getBoard().putUnit(new EmptyUnit(w.getPosition()));
                if (isOrthAdjToMine(s, w.getPosition())) {
                    s.getBoard().putFloor(new GoldMine(w.getPosition()));
                }
            });

    public static <T extends GenericTank> ConditionalRule<T> GetKillOrDestroyTankOnZeroDurabilityRule() {
        return new ConditionalRule<>(
                (s, t) -> Attributes.DURABILITY.from(t).orElse(-1) == 0, // -1, so that if a tank doesn't have
                                                                         // durability, this rule won't apply
                (s, t) -> {
                    if (Attributes.DEAD.from(t).orElse(false)) {
                        s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                        String tankPlayer = t.getPlayer();
                        s.getCouncil().getCouncillors().remove(tankPlayer);
                        s.getCouncil().getSenators().add(tankPlayer);
                    } else {
                        Attributes.DEAD.to(t, true);
                        Attributes.ACTION_POINTS.to(t, 0);
                        Attributes.GOLD.to(t, 0);
                        Attributes.BOUNTY.to(t, 0);
                        Attributes.DURABILITY.to(t, 3);
                        s.getCouncil().getCouncillors().add(t.getPlayer());
                    }
                });
    }

    public static final ConditionalRule<Board> TANK_WIN_CONDITION = new ConditionalRule<>(
            (s, b) -> b.gatherUnits(GenericTank.class).stream().filter((t) -> !Attributes.DEAD.from(t).orElse(false))
                    .collect(Collectors.toSet()).size() == 1,
            (s, b) -> {
                s.setRunning(false);
                s.setWinner(
                        b.gatherUnits(GenericTank.class).stream().filter((t) -> !Attributes.DEAD.from(t).orElse(false))
                                .findFirst().get().getPlayer());
            });

    public static final ConditionalRule<ArmisticeCouncil> ARMISTICE_COUNCIL_WIN_CONDITION = new ConditionalRule<>(
            (s, c) -> c.getArmisticeVoteCount() >= c.getArmisticeVoteCap(),
            (s, c) -> {
                s.setRunning(false);
                s.setWinner("Council");
            });
}
