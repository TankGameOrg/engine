package pro.trevor.tankgame.rule.impl.shared;

import static pro.trevor.tankgame.util.Util.isOrthAdjToMine;

import java.util.stream.Collectors;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.definition.Priority;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.LootBox;
import pro.trevor.tankgame.state.meta.Council;

public class ConditionalRules {
    public static final ConditionalRule<BasicWall> DESTROY_WALL_ON_ZERO_DURABILITY = new ConditionalRule<>(
            (s, w) -> w.getDurability() == 0,
            (s, w) -> {
                s.getBoard().putUnit(new EmptyUnit(w.getPosition()));
                if (isOrthAdjToMine(s, w.getPosition())) {
                    s.getBoard().putFloor(new GoldMine(w.getPosition()));
                }
            });


    public static final ConditionalRule<Tank> HANDLE_TANK_ON_ZERO_DURABILITY = new ConditionalRule<>(
            (s, t) -> t.get(Attribute.DURABILITY).orElse(-1) == 0, // -1, so that if a tank doesn't have durability, this rule won't apply
            (s, t) -> {
                if (t.get(Attribute.DEAD).orElse(false)) {
                    s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                    s.getCouncil().getCouncillors().remove(t.getPlayerRef());
                    s.getCouncil().getSenators().add(t.getPlayerRef());
                } else {
                    t.put(Attribute.DEAD, true);
                    t.put(Attribute.ACTION_POINTS, 0);
                    t.put(Attribute.GOLD, 0);
                    t.put(Attribute.BOUNTY, 0);
                    t.put(Attribute.DURABILITY, 3);
                    s.getCouncil().getCouncillors().add(t.getPlayerRef());
                }
            });

    public static final ConditionalRule<Board> TANK_WIN_CONDITION = new ConditionalRule<>(
            (s, b) -> b.gatherUnits(Tank.class).stream().filter((t) -> !t.get(Attribute.DEAD).orElse(false))
                    .toList().size() == 1,
            (s, b) -> {
                s.put(Attribute.RUNNING, false);
                s.put(Attribute.WINNER, b.gatherUnits(Tank.class).stream()
                        .filter((t) -> !t.get(Attribute.DEAD).orElse(false))
                        .findFirst().get().getPlayerRef().getName());
            }, Priority.LOWEST);

    public static final ConditionalRule<Board> TEAM_WIN_CONDITION = new ConditionalRule<>(
            (s, b) -> b.gatherUnits(Tank.class).stream()
                    .filter((t) -> !t.get(Attribute.DEAD).orElse(false))
                    .map((t) -> t.getPlayerRef().toPlayer(s).get().getUnsafe(Attribute.TEAM))
                    .collect(Collectors.toSet()).size() == 1,
            (s, b) -> {
                s.put(Attribute.RUNNING, false);
                s.put(Attribute.WINNER, b.gatherUnits(Tank.class).stream()
                        .filter((t) -> !t.get(Attribute.DEAD).orElse(false))
                        .map((t) -> t.getPlayerRef().toPlayer(s).get().getUnsafe(Attribute.TEAM))
                        .findAny().get());
            }, Priority.LOWEST);

    public static final ConditionalRule<Council> ARMISTICE_COUNCIL_WIN_CONDITION = new ConditionalRule<>(
            (s, c) -> c.getOrElse(Attribute.ARMISTICE_COUNT, 0) >= c.getUnsafe(Attribute.ARMISTICE_MAX),
            (s, c) -> {
                s.put(Attribute.RUNNING, false);
                s.put(Attribute.WINNER, "Council");
            }, Priority.LOWEST);

    public static final ConditionalRule<LootBox> DESTORY_EMPTY_LOOT_BOXES = new ConditionalRule<>(
        (state, box) -> box.isEmpty(),
        (state, box) -> state.getBoard().putUnit(new EmptyUnit(box.getPosition())));
}
