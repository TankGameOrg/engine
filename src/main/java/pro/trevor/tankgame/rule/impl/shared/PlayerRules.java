package pro.trevor.tankgame.rule.impl.shared;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.function.*;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.actions.DiceSet;
import pro.trevor.tankgame.rule.definition.actions.DieRollLogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.DieRollResult;
import pro.trevor.tankgame.rule.definition.actions.EnumeratedLogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.LogFieldHelpers;
import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.LogFieldValueDescriptor;
import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.rule.definition.player.conditional.*;
import pro.trevor.tankgame.rule.impl.util.ILootProvider;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.DestructibleFloor;
import pro.trevor.tankgame.state.board.floor.Lava;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.LootBox;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.Pair;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.util.function.ITriFunction;
import pro.trevor.tankgame.util.function.ITriPredicate;

import static pro.trevor.tankgame.util.Util.*;

public class PlayerRules {

    private static IRulePredicate cofferCost(int cost) {
        return new RulePredicateStream<>(PredicateHelpers::getCouncil)
            .map(PredicateHelpers.getAttribute(Attribute.COFFER))
            .filter((context, coffer) -> coffer >= cost, new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Council does not have enough gold in the coffer"));
    }

    private static final IRulePredicate PLAYER_TANK_IS_ALIVE_PREDICATE = new RulePredicateStream<>(PredicateHelpers::getTank)
        .map(PredicateHelpers.getAttribute(Attribute.DEAD, false))
        .filter((context, dead) -> !dead, new PlayerRuleError(PlayerRuleError.Category.NOT_APPLICABLE, "Tank must be alive"));

    private static final IRulePredicate PLAYER_IS_COUNCIL_PREDICATE = new BasicRulePredicate((context) -> {
        return context.getState().getCouncil().allPlayersOnCouncil().contains(context.getPlayerRef());
    }, new PlayerRuleError(PlayerRuleError.Category.NOT_APPLICABLE, "Player must be a councilor"));

    // Check if TARGET_POSITION is within the subject's range
    private static final IRulePredicate TARGET_IS_IN_RANGE = new RulePredicateStream<>(PredicateHelpers::getTank)
        .filter(PredicateHelpers::hasLogEntry)
        .filter((context, tank) -> {
            Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
            return tank.getPosition().distanceFrom(target) <= tank.getOrElse(Attribute.RANGE, 0);
        }, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target position is not in range"));

    private static final IRulePredicate TANK_HAS_ENOUGH_GOLD_TO_BUY_ACTION = new RulePredicateStream<>(PredicateHelpers::getTank)
        .filter(PredicateHelpers::hasLogEntry)
        .filter(PredicateHelpers.minimum(Attribute.GOLD, (context) -> PredicateHelpers.getLogField(context, Attribute.GOLD)));

    private static final IRulePredicate TARGET_TANK_IS_IN_RANGE = new RulePredicateStream<>(PredicateHelpers::getTank)
        .filter(PredicateHelpers::hasLogEntry)
        .filter((context, tank) -> {
            Result<Tank, PlayerRuleError> result = PredicateHelpers.getTargetTank(context);
            if(result.isError()) {
                return Result.error(result.getError());
            }

            Tank targetTank = result.getValue();

            Set<Position> positionsInRange = getSpacesInRange(context.getState().getBoard(), tank.getPosition(), tank.get(Attribute.RANGE).orElse(0));
            if(!positionsInRange.contains(targetTank.getPosition())) {
                return Result.error(new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Tank has insufficient range"));
            }

            return Result.ok();
        });

    private static final IRulePredicate TARGET_POSITION_IS_EMPTY_SPACE = RulePredicateStream.empty()
        .filter(PredicateHelpers::hasLogEntry)
        .filter((context) -> context.getState().getBoard().isEmpty(PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)), new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target space is not empty"));

    private static final IRulePredicate TARGET_TANK_IS_ALIVE = new RulePredicateStream<>(PredicateHelpers::getTargetTank)
        .map(PredicateHelpers.getAttribute(Attribute.DEAD, true))
        .filter((context, dead) -> !dead, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target must be a living tank"));

    private static final IRulePredicate TARGET_TANKS_SPEED_IS_NOT_MODIFIED = new RulePredicateStream<>(PredicateHelpers::getTargetTank)
        .filter((context, targetTank) -> {
            return targetTank.getOrElse(Attribute.PREVIOUS_SPEED, targetTank.getUnsafe(Attribute.SPEED)).equals(targetTank.getUnsafe(Attribute.SPEED));
        }, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Your target's speed has already been modified you can't modify it until the previous modification is removed"));

    private static final IRulePredicate TARGET_POSITION_IS_ON_BOARD = RulePredicateStream.empty()
        .filter(PredicateHelpers::hasLogEntry)
        .filter((context) -> context.getState().getBoard().isValidPosition(PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)),
            new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target position is not within the game board"));

    private static final IRulePredicate PLAYER_TANK_IS_DEAD_PREDICATE = new BasicRulePredicate((context) -> {
        return context.getState().getTankForPlayerRef(context.getPlayerRef())
            .map((tank) -> tank.getOrElse(Attribute.DEAD, false))
            .orElse(true);
    }, new PlayerRuleError(PlayerRuleError.Category.NOT_APPLICABLE, "Player tank must be dead"));

    private static int translateGoldCostPlusDiscount(int gold) {
        int n5 = gold / 5;
        int rem = gold - n5 * 5;
        int n3 = rem / 3;
        assert rem == n3 * 3;
        return n5 * 2 + n3;
    }

    public static final PlayerConditionRule BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT = new PlayerConditionRule(
            PlayerRules.ActionKeys.BUY_ACTION,
            new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE, TANK_HAS_ENOUGH_GOLD_TO_BUY_ACTION,
                new RulePredicateStream<>(PredicateHelpers::getTank)
                    .filter(PredicateHelpers.minimum(Attribute.GOLD, 3))),
            (context) -> {
                Tank tank = PredicateHelpers.getTank(context).getValue();
                int gold = PredicateHelpers.getLogField(context, Attribute.GOLD);
                int actions = translateGoldCostPlusDiscount(gold);

                tank.put(Attribute.ACTION_POINTS, tank.getOrElse(Attribute.ACTION_POINTS, 0) + actions);
                tank.put(Attribute.GOLD, tank.getUnsafe(Attribute.GOLD) - gold);
            },
            (context) -> {
                Stream<Pair<Integer, Integer>> exchangeOptions = Stream.of(3, 5, 8, 10)
                    .filter((goldCost) -> PredicateHelpers.getTank(context).getValue().getOrElse(Attribute.GOLD, 0) >= goldCost)
                    .map((goldCost) -> new Pair<>(goldCost, translateGoldCostPlusDiscount(goldCost)));

                return List.of(
                    LogFieldHelpers.getExchangeSpec(Attribute.GOLD, Attribute.ACTION_POINTS, exchangeOptions)
                );
            });

    public static PlayerConditionRule buyActionWithGold(int actionCost, int maxBuys) {
        if (actionCost <= 0)
            throw new Error("Illegal Action Cost of " + actionCost + " gold. Must be positive and non-zero.");
        if (maxBuys <= 0)
            throw new Error("Illegal max buys of " + maxBuys + ". Must be positive and non-zero.");

        return new PlayerConditionRule(ActionKeys.BUY_ACTION,
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE, TANK_HAS_ENOUGH_GOLD_TO_BUY_ACTION,
                    RulePredicateStream.empty()
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter((context) -> PredicateHelpers.getLogField(context, Attribute.GOLD) / actionCost <= maxBuys,
                            new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Actions bought must be fewer than or equal to %s", maxBuys)),
                    RulePredicateStream.empty()
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter((context) -> PredicateHelpers.getLogField(context, Attribute.GOLD) % actionCost == 0,
                            new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Gold spent must be a multiple of the action cost: %s", maxBuys)),
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers.minimum(Attribute.GOLD, actionCost))
                ),
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    int goldSpent = PredicateHelpers.getLogField(context, Attribute.GOLD);
                    int boughtActions = goldSpent / actionCost;

                    tank.put(Attribute.ACTION_POINTS, tank.getUnsafe(Attribute.ACTION_POINTS) + boughtActions);
                    tank.put(Attribute.GOLD, tank.getUnsafe(Attribute.GOLD) - goldSpent);
                },
                (context) -> {
                    Stream<Pair<Integer, Integer>> exchangeOptions = IntStream.rangeClosed(1, maxBuys).boxed()
                        .map((actions) -> new Pair<>(actions * actionCost, actions))
                        .filter((exchangeRate) -> PredicateHelpers.getTank(context).getValue().getOrElse(Attribute.GOLD, 0) >= exchangeRate.left());

                    return List.of(
                        LogFieldHelpers.getExchangeSpec(Attribute.GOLD, Attribute.ACTION_POINTS, exchangeOptions)
                    );
                });
    }

    public static PlayerConditionRule getMoveRule(Attribute<Integer> attribute, int cost) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.MOVE,
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE,
                        new RulePredicateStream<>(PredicateHelpers::getTank)
                            .filter(PredicateHelpers.minimum(attribute, cost)),
                        new RulePredicateStream<>(PredicateHelpers::getTank)
                            .filter(PredicateHelpers::hasLogEntry)
                            .filter((context, tank) -> {
                                Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
                                return canMoveTo(context.getState(), tank.getPosition(), target, tank.getOrElse(Attribute.SPEED, 1));
                            }, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Tank cannot move to target position")))
                ,
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    tank.put(attribute, tank.getUnsafe(attribute) - cost);
                    context.getState().getBoard().putUnit(new EmptyUnit(tank.getPosition()));
                    tank.setPosition(PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION));
                    context.getState().getBoard().putUnit(tank);
                },
                (context) -> List.of(LogFieldHelpers.getMovablePositionsSpec(context)));
    }

    public static PlayerConditionRule getUpgradeRangeRule(Attribute<Integer> attribute, int cost) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.UPGRADE_RANGE,
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE,
                        new RulePredicateStream<>(PredicateHelpers::getTank)
                            .filter(PredicateHelpers.minimum(attribute, cost))),
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    tank.put(Attribute.RANGE, tank.getOrElse(Attribute.RANGE, 0) + 1);
                    tank.put(attribute, tank.getUnsafe(attribute) - cost);
                },
                (context) -> List.of());
    }

    public static PlayerConditionRule getShareGoldWithTaxToCofferRule(int taxAmount) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.DONATE,
                "Donate gold to another tank within your range",
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE,
                    RulePredicateStream.empty()
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter((context) -> PredicateHelpers.getLogField(context, Attribute.DONATION) > 0, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Donation must be positive")),
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter(PredicateHelpers.minimum(Attribute.GOLD, (context) -> PredicateHelpers.getLogField(context, Attribute.DONATION) + taxAmount)),
                    new RulePredicateStream<>(PredicateHelpers::getTargetTank)
                        .filter((context, targetTank) -> targetTank.has(Attribute.GOLD), new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target must have gold attribute")),
                    TARGET_TANK_IS_IN_RANGE,
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers.minimum(Attribute.GOLD, taxAmount + 1))
                ),
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    Tank other = PredicateHelpers.getTargetTank(context).getValue();
                    int donation = PredicateHelpers.getLogField(context, Attribute.DONATION);

                    tank.put(Attribute.GOLD, tank.getUnsafe(Attribute.GOLD) - (donation + taxAmount));
                    other.put(Attribute.GOLD, other.getUnsafe(Attribute.GOLD) + donation);
                    Council council = context.getState().getCouncil();
                    council.put(Attribute.COFFER, council.getUnsafe(Attribute.COFFER) + taxAmount);
                },
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();

                    return List.of(
                        LogFieldHelpers.getPlayersInDonationRangeSpec(context),
                        LogFieldHelpers.getIntegerSpec(Attribute.DONATION, 1, tank.getUnsafe(Attribute.GOLD) - taxAmount)
                    );
                });
    }

    public static PlayerConditionRule getShareGoldWithTaxRule(int taxAmount) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.DONATE,
                "Donate gold to another tank within your range",
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE,
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter(PredicateHelpers.minimum(Attribute.GOLD, (context) -> PredicateHelpers.getLogField(context, Attribute.DONATION) + taxAmount)),
                    TARGET_TANK_IS_IN_RANGE,
                    RulePredicateStream.empty()
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter((context) -> PredicateHelpers.getLogField(context, Attribute.DONATION) > 0, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Donation must be positive")),
                    new RulePredicateStream<>(PredicateHelpers::getTargetTank)
                        .filter((context, targetTank) -> targetTank.has(Attribute.GOLD), new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target must have gold attribute")),
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers.minimum(Attribute.GOLD, taxAmount + 1))
                ),
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    Tank other = PredicateHelpers.getTargetTank(context).getValue();
                    int donation = PredicateHelpers.getLogField(context, Attribute.DONATION);

                    tank.put(Attribute.GOLD, tank.getUnsafe(Attribute.GOLD) - (donation + taxAmount));
                    other.put(Attribute.GOLD, other.getUnsafe(Attribute.GOLD) + donation);
                },
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();

                    return List.of(
                        LogFieldHelpers.getPlayersInDonationRangeSpec(context),
                        LogFieldHelpers.getIntegerSpec(Attribute.DONATION, 1, tank.getUnsafe(Attribute.GOLD) - taxAmount)
                    );
                });
    }

    public static PlayerConditionRule getCofferCostStimulusRule(int cost) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.STIMULUS,
                "Grant an action to a living tank",
                new RuleCondition(PLAYER_IS_COUNCIL_PREDICATE,
                    cofferCost(cost),
                    TARGET_TANK_IS_ALIVE
                ),
                (context) -> {
                    Council council = context.getState().getCouncil();
                    Tank target = PredicateHelpers.getTargetTank(context).getValue();
                    target.put(Attribute.ACTION_POINTS, target.getOrElse(Attribute.ACTION_POINTS, 0) + 1);
                    council.put(Attribute.COFFER, council.getUnsafe(Attribute.COFFER) - cost);
                },
                (context) -> List.of(LogFieldHelpers.getAllPlayersWithLivingTanksSpec(context)));
    }

    public static PlayerConditionRule getRuleCofferCostGrantLife(int cost, int minimumCouncillors) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.GRANT_LIFE,
                new RuleCondition(PLAYER_IS_COUNCIL_PREDICATE,
                    cofferCost(cost),
                    new BasicRulePredicate((context) -> context.getState().getCouncil().allPlayersOnCouncil().size() >= minimumCouncillors, "Council has insufficient members"),
                    new RulePredicateStream<>(PredicateHelpers::getTargetTank)
                        .filter((context, targetTank) -> targetTank.has(Attribute.DURABILITY), new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target tank must have durability"))
                ),
                (context) -> {
                    Council council = context.getState().getCouncil();
                    council.put(Attribute.COFFER, council.getUnsafe(Attribute.COFFER) - cost);
                    Tank targetTank = PredicateHelpers.getTargetTank(context).getValue();
                    if (targetTank.getOrElse(Attribute.DEAD, false)) {
                        targetTank.put(Attribute.DEAD, false);
                        targetTank.put(Attribute.DURABILITY, 1);
                        council.getCouncillors().remove(targetTank.getPlayerRef());
                    } else {
                        targetTank.put(Attribute.DURABILITY, targetTank.getUnsafe(Attribute.DURABILITY) + 1);
                    }
                },
                (context) -> List.of(LogFieldHelpers.getAllPlayersWithTanksSpec(context)));
    }

    public static PlayerConditionRule getRuleCofferCostBounty(int lowerBound, int upperBound) {
        assert lowerBound >= 0 && upperBound >= lowerBound;
        return new PlayerConditionRule(PlayerRules.ActionKeys.BOUNTY,
                new RuleCondition(PLAYER_IS_COUNCIL_PREDICATE,
                    new RulePredicateStream<>(PredicateHelpers::getCouncil)
                        .map(PredicateHelpers.getAttribute(Attribute.CAN_BOUNTY, true))
                        .filter((context, canBounty) -> canBounty, new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Council can only bounty once per day")),
                    TARGET_TANK_IS_ALIVE,
                    new RulePredicateStream<>(PredicateHelpers::getCouncil)
                        .filter(PredicateHelpers::hasLogEntry)
                        .map(PredicateHelpers.getAttribute(Attribute.COFFER, 0))
                        .filter((context, coffer) -> coffer >= PredicateHelpers.getLogField(context, Attribute.BOUNTY), new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Council has insufficient coffer"))
                ),
                (context) -> {
                    Council council = context.getState().getCouncil();
                    Tank targetTank = PredicateHelpers.getTargetTank(context).getValue();
                    int bounty = PredicateHelpers.getLogField(context, Attribute.BOUNTY);
                    assert council.getUnsafe(Attribute.COFFER) >= bounty;
                    targetTank.put(Attribute.BOUNTY, targetTank.getOrElse(Attribute.BOUNTY, 0) + bounty);
                    council.put(Attribute.COFFER, council.getUnsafe(Attribute.COFFER) - bounty);
                    council.put(Attribute.CAN_BOUNTY, false);
                },
                (context) -> {
                    int coffer = context.getState().getCouncil().getOrElse(Attribute.COFFER, 0);

                    return List.of(
                        LogFieldHelpers.getAllPlayersWithLivingTanksSpec(context),
                        LogFieldHelpers.getIntegerSpec(Attribute.BOUNTY, lowerBound, Math.min(upperBound, coffer))
                    );
                });
    }

    public static PlayerConditionRule getSpawnWallWithCostRule(int cost, int durability) {
        assert cost >= 0;
        assert durability > 0;
        return new PlayerConditionRule(ActionKeys.SPAWN_WALL, new RuleCondition(PLAYER_TANK_IS_DEAD_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getPlayer)
                .filter(PredicateHelpers.minimum(Attribute.POWER, cost)),
            TARGET_POSITION_IS_EMPTY_SPACE
        ),
        (context) -> {
            Player player = PredicateHelpers.getPlayer(context).getValue();
            Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
            player.put(Attribute.POWER, player.getUnsafe(Attribute.POWER) - cost);
            context.getState().getBoard().putUnit(new BasicWall(target, durability));
        },
        (context) -> List.of(LogFieldHelpers.getEmptyPositionsSpec(context)));
    }

    public static PlayerConditionRule getSpawnLavaWithCostRule(int cost, int damage) {
        assert cost >= 0;
        assert damage > 0;
        return new PlayerConditionRule(ActionKeys.SPAWN_LAVA, new RuleCondition(PLAYER_TANK_IS_DEAD_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getPlayer)
                .filter(PredicateHelpers.minimum(Attribute.POWER, cost)),
            TARGET_POSITION_IS_EMPTY_SPACE),
            (context) -> {
                Player player = PredicateHelpers.getPlayer(context).getValue();
                Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
                player.put(Attribute.POWER, player.getUnsafe(Attribute.POWER) - cost);
                context.getState().getBoard().putFloor(new Lava(target, damage));
            },
            (context) -> List.of(LogFieldHelpers.getEmptyPositionsSpec(context)));
    }

    public static PlayerConditionRule getSmiteRule(int cost, int health) {
        assert cost >= 0;
        assert health > 0;
        return new PlayerConditionRule(ActionKeys.SMITE, new RuleCondition(PLAYER_TANK_IS_DEAD_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getPlayer)
                .filter(PredicateHelpers.minimum(Attribute.POWER, cost)),
            TARGET_TANK_IS_ALIVE),
            (context) -> {
                Player player = PredicateHelpers.getPlayer(context).getValue();
                Tank target = PredicateHelpers.getTargetTank(context).getValue();
                player.put(Attribute.POWER, player.getUnsafe(Attribute.POWER) - cost);
                target.put(Attribute.DURABILITY, target.getUnsafe(Attribute.DURABILITY) - health);
            },
            (context) -> List.of(LogFieldHelpers.getAllPlayersWithLivingTanksSpec(context)));
    }

    public static PlayerConditionRule getHealRule(int cost, int health) {
        assert cost >= 0;
        assert health > 0;
        return new PlayerConditionRule(ActionKeys.HEAL, new RuleCondition(PLAYER_TANK_IS_DEAD_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getPlayer)
                .filter(PredicateHelpers.minimum(Attribute.POWER, cost)),
            TARGET_TANK_IS_ALIVE),
            (context) -> {
                Player player = PredicateHelpers.getPlayer(context).getValue();
                Tank target = PredicateHelpers.getTargetTank(context).getValue();
                player.put(Attribute.POWER, player.getUnsafe(Attribute.POWER) - cost);
                target.put(Attribute.DURABILITY, target.getUnsafe(Attribute.DURABILITY) + health);
            },
            (context) -> List.of(LogFieldHelpers.getAllPlayersWithLivingTanksSpec(context)));
    }

    public static PlayerConditionRule getSlowRule(int cost, int modifier) {
        assert cost >= 0;
        assert modifier > 0;
        return new PlayerConditionRule(ActionKeys.SLOW, new RuleCondition(PLAYER_TANK_IS_DEAD_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getPlayer)
                .filter(PredicateHelpers.minimum(Attribute.POWER, cost)),
            TARGET_TANK_IS_ALIVE,
            TARGET_TANKS_SPEED_IS_NOT_MODIFIED),
            (context) -> {
                Player player = PredicateHelpers.getPlayer(context).getValue();
                Tank tank = PredicateHelpers.getTargetTank(context).getValue();
                player.put(Attribute.POWER, player.getUnsafe(Attribute.POWER) - cost);
                tank.put(Attribute.PREVIOUS_SPEED, tank.getUnsafe(Attribute.SPEED));
                tank.put(Attribute.SPEED, tank.getUnsafe(Attribute.SPEED) - modifier);
                tank.put(Attribute.SLOWED, true);
            },
            (context) -> List.of(LogFieldHelpers.getAllPlayersWithLivingTanksSpec(context)));
    }

    public static PlayerConditionRule getHastenRule(int cost, int modifier) {
        assert cost >= 0;
        assert modifier > 0;
        return new PlayerConditionRule(ActionKeys.HASTEN, new RuleCondition(PLAYER_TANK_IS_DEAD_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getPlayer)
                .filter(PredicateHelpers.minimum(Attribute.POWER, cost)),
            TARGET_TANK_IS_ALIVE,
            TARGET_TANKS_SPEED_IS_NOT_MODIFIED),
            (context) -> {
                Player player = PredicateHelpers.getPlayer(context).getValue();
                Tank tank = PredicateHelpers.getTargetTank(context).getValue();
                player.put(Attribute.POWER, player.getUnsafe(Attribute.POWER) - cost);
                tank.put(Attribute.PREVIOUS_SPEED, tank.getUnsafe(Attribute.SPEED));
                tank.put(Attribute.SPEED, tank.getUnsafe(Attribute.SPEED) + modifier);
                tank.put(Attribute.HASTENED, true);
            },
            (context) -> List.of(LogFieldHelpers.getAllPlayersWithLivingTanksSpec(context)));
    }

    /**
     * A rule that allows tanks to loot other units or floors
     *
     * If the target has the ONLY_LOOTABLE_BY then only the player specified by the attribute can loot this target
     *
     * @param canLootTarget A function that checks if the specified target is lootable
     * @param transferLoot A callback transfers the looted targets attributes
     */
    public static PlayerConditionRule getLootTargetRule(
            ITriFunction<PlayerRuleContext, Tank, AttributeContainer, Result<Void, PlayerRuleError>> canLootTarget,
            ITriConsumer<PlayerRuleContext, Tank, AttributeContainer> transferLoot) {

        IRulePredicate canLootRule = (context) -> {
            Result<Void, PlayerRuleError> logEntryError = PredicateHelpers.hasLogEntry(context);
            if(logEntryError.isError()) {
                return logEntryError;
            }

            State state = context.getState();
            PlayerRef player = context.getPlayerRef();
            // Make sure that the position is an AttributeContainer
            Position position = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
            Optional<IElement> targetElement = state.getBoard().getUnitOrFloor(position);
            if(targetElement.isEmpty()) {
                return Result.error(new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target " + position + " is out of bounds"));
            }

            if(!(targetElement.get() instanceof AttributeContainer)) {
                return Result.error(new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Cannot target " + position + " it's a " + targetElement.get().getClass() + " not an AttributeContainer"));
            }

            AttributeContainer target = (AttributeContainer) targetElement.get();

            // Make sure this player is allowed to loot this target
            if(target.has(Attribute.ONLY_LOOTABLE_BY)) {
                PlayerRef lootableBy = target.getUnsafe(Attribute.ONLY_LOOTABLE_BY);
                if(!lootableBy.equals(player)) {
                    String targetName = target.getOrElse(Attribute.NAME, position.toString());
                    String lootableByName = lootableBy.toPlayer(state).get().getOrElse(Attribute.NAME, "somebody");
                    return Result.error(new PlayerRuleError(PlayerRuleError.Category.GENERIC, targetName + " can only be looted by " + lootableByName));
                }
            }

            // Check ruleset specific requirements
            Tank tank = PredicateHelpers.getTank(context).getValue();
            return canLootTarget.accept(context, tank, target);
        };

        RuleCondition lootCondition = new RuleCondition(
            TARGET_IS_IN_RANGE,
            PLAYER_TANK_IS_ALIVE_PREDICATE,
            new RulePredicateStream<>(PredicateHelpers::getTank)
                .filter(PredicateHelpers::hasLogEntry)
                .filter((context, tank) -> LineOfSight.hasLineOfSightV4(context.getState(), tank.getPosition(), PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)),
                    new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Target position is not in line-of-sight")),
            canLootRule
        );

        return new PlayerConditionRule(PlayerRules.ActionKeys.LOOT,
            lootCondition,
            (context) -> {
                Tank tank = PredicateHelpers.getTank(context).getValue();
                Position position = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
                AttributeContainer targetObject = (AttributeContainer) context.getState().getBoard().getUnitOrFloor(position).get();
                transferLoot.accept(context, tank, targetObject);
            },
            (context) -> {
                Stream<Position> lootablePositions = LogFieldHelpers.getPositionsInRange(context).stream().filter((position) -> {
                    PlayerRuleContext testContext = new PlayerRuleContext(context.getState(), context.getPlayerRef(), new LogEntry(Map.of(Attribute.TARGET_POSITION, position)));
                    return lootCondition.test(testContext).isEmpty();
                });

                return List.of(LogFieldHelpers.makeLogFieldSpec(Attribute.TARGET_POSITION, lootablePositions));
            });
    }

    /**
     * Rule that loots gold from dead tanks or loot boxes if the subject has the PLAYER_CAN_LOOT attribute
     */
    public static PlayerConditionRule getLootRule(ILootProvider lootTable) {
        return getLootTargetRule((context, tank, target) -> {
            if(!tank.getOrElse(Attribute.PLAYER_CAN_LOOT, false)) {
                return Result.error(new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Players can only loot once per day"));
            }

            if(target instanceof Tank && target.getOrElse(Attribute.DEAD, false)) {
                return Result.ok();
            }

            if(target instanceof LootBox) {
                return Result.ok();
            }

            return Result.error(new PlayerRuleError(PlayerRuleError.Category.GENERIC, "You can only loot dead tanks or loot boxes"));
        }, (context, tank, target) -> {
            if(target instanceof Tank) {
                tank.put(Attribute.GOLD, tank.getOrElse(Attribute.GOLD, 0) + target.getOrElse(Attribute.GOLD, 0));
                target.put(Attribute.GOLD, 0);
            } else if (target instanceof LootBox lootBox) {
                lootTable.grantLoot(context.getState(), target, tank);
                lootBox.setHasBeenLooted();
            }

            tank.remove(Attribute.PLAYER_CAN_LOOT);
        });
    }

    public static PlayerConditionRule spendActionToShootGenericWithHitBoolean(
            ITriPredicate<State, Position, Position> lineOfSight,
            BiFunction<PlayerRuleContext, IElement, List<LogFieldSpec<?>>> diceForPosition,
            ITriConsumer<PlayerRuleContext, Tank, IElement> handleHit) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.SHOOT,
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE, TARGET_IS_IN_RANGE,
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers.minimum(Attribute.ACTION_POINTS, 1)),
                    TARGET_POSITION_IS_ON_BOARD,
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter((context, tank) -> lineOfSight.test(context.getState(), tank.getPosition(), PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)),
                            new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Target position is not in line-of-sight"))),
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);

                    boolean hit = false;
                    LogEntry logEntry = context.getLogEntry().get();
                    if(logEntry.has(Attribute.HIT_ROLL)) {
                        hit = ((DieRollResult<Boolean>) logEntry.getUnsafe(Attribute.HIT_ROLL)).getResults()
                            .stream().reduce(false, (currentHit, dieRoll) -> currentHit || dieRoll);
                    }
                    else if(logEntry.has(Attribute.HIT)) {
                        hit = logEntry.getUnsafe(Attribute.HIT);
                    }

                    tank.put(Attribute.ACTION_POINTS, tank.getUnsafe(Attribute.ACTION_POINTS) - 1);

                    Optional<IElement> optionalElement = context.getState().getBoard().getUnitOrFloor(target);
                    if (optionalElement.isEmpty()) {
                        throw new Error(String.format("Target position %s is not on the game board", target.toString()));
                    }

                    if (hit) {
                        handleHit.accept(context, tank, optionalElement.get());
                    }
                },
                (context) -> {
                    return List.of(
                        LogFieldHelpers.getShootablePositionsSpec(context, lineOfSight, (position) -> {
                            IElement element = context.getState().getBoard().getUnitOrFloor(position).get();
                            return diceForPosition.apply(context, element);
                        })
                    );
                });
    }

    public static PlayerConditionRule spendActionToShootGeneric(
            ITriPredicate<State, Position, Position> lineOfSight,
            BiFunction<PlayerRuleContext, IElement, List<LogFieldSpec<?>>> diceForPosition,
            ITriConsumer<PlayerRuleContext, Tank, IElement> handleHit) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.SHOOT,
                new RuleCondition(PLAYER_TANK_IS_ALIVE_PREDICATE, TARGET_IS_IN_RANGE,
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers.minimum(Attribute.ACTION_POINTS, 1)),
                    TARGET_POSITION_IS_ON_BOARD,
                    new RulePredicateStream<>(PredicateHelpers::getTank)
                        .filter(PredicateHelpers::hasLogEntry)
                        .filter((context, tank) -> lineOfSight.test(context.getState(), tank.getPosition(), PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)),
                            new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Target position is not in line-of-sight"))),
                (context) -> {
                    Tank tank = PredicateHelpers.getTank(context).getValue();
                    Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
                    tank.put(Attribute.ACTION_POINTS, tank.getUnsafe(Attribute.ACTION_POINTS) - 1);

                    Optional<IElement> optionalElement = context.getState().getBoard().getUnitOrFloor(target);
                    if (optionalElement.isEmpty()) {
                        throw new Error(String.format("Target position %s is not on the game board", target.toString()));
                    }

                    handleHit.accept(context, tank, optionalElement.get());
                },
                (context) -> {
                    return List.of(
                        LogFieldHelpers.getShootablePositionsSpec(context, lineOfSight, (position) -> {
                            IElement element = context.getState().getBoard().getUnitOrFloor(position).get();
                            return diceForPosition.apply(context, element);
                        })
                    );
                });
    }

    public static PlayerConditionRule spendActionToShootWithDeathHandleHitBoolean(
            ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<PlayerRuleContext, Tank, Tank> handleDeath) {
        return spendActionToShootGenericWithHitBoolean(lineOfSight,
        (context, targetElement) -> {
            Tank subjectTank = PredicateHelpers.getTank(context).getValue();

            // We are guarenteed to hit unless we are shooting a tank
            // Indicate this to the user by showing a field with only one option "hit"
            LogFieldSpec<?> hitRoll = new EnumeratedLogFieldSpec<>(Attribute.HIT, List.of(
                new LogFieldValueDescriptor<>(true, "hit")
            )).setDescription("Like the broad side of a barn you can't miss this target!");

            if(targetElement instanceof Tank Tank && !Tank.getOrElse(Attribute.DEAD, false)) {
                int distance = subjectTank.getPosition().distanceFrom(targetElement.getPosition());
                int numDice = (subjectTank.getOrElse(Attribute.RANGE, 0) - distance) + 1;
                hitRoll = new DieRollLogFieldSpec<>(Attribute.HIT_ROLL, List.of(new DiceSet<>(numDice, DiceSet.HIT_DIE)));
            }

            return List.of(
                hitRoll
            );
        },
        (context, tank, element) -> {
            switch (element) {
                case Tank otherTank -> {
                    otherTank.put(Attribute.DURABILITY, otherTank.getUnsafe(Attribute.DURABILITY) - 1);
                    if (!otherTank.getUnsafe(Attribute.DEAD) && otherTank.getUnsafe(Attribute.DURABILITY) == 0) {
                        handleDeath.accept(context, tank, otherTank);
                    }
                }
                case BasicWall wall -> wall.setDurability(wall.getDurability() - 1);
                case DestructibleFloor floor -> {
                    if (floor.get(Attribute.DESTROYED).orElse(false))
                        return;
                    floor.put(Attribute.DURABILITY, floor.getUnsafe(Attribute.DURABILITY) - 1);
                    if (floor.getUnsafe(Attribute.DURABILITY) == 0) {
                        floor.put(Attribute.DESTROYED, true);
                    }
                }
                default -> {
                }
            }
        });
    }

    public static PlayerConditionRule spendActionToShootWithDeathHandleHitDamage(
            ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<PlayerRuleContext, Tank, Tank> handleDeath) {
        return spendActionToShootGeneric(lineOfSight, (context, targetElement) -> {
            return List.of(
                new DieRollLogFieldSpec<>(Attribute.DAMAGE_ROLL, List.of(new DiceSet<>(1, DiceSet.D4)))
            );
        },
        (context, tank, element) -> {
            int damage = ((DieRollResult<Integer>) PredicateHelpers.getLogField(context, Attribute.DAMAGE_ROLL)).getResults()
                .stream().reduce(0, (totalDamage, die) -> totalDamage + die);

            switch (element) {
                case Tank otherTank -> {
                    otherTank.put(Attribute.DURABILITY, otherTank.getUnsafe(Attribute.DURABILITY) - damage);
                    if (!otherTank.getUnsafe(Attribute.DEAD) && otherTank.getUnsafe(Attribute.DURABILITY) == 0) {
                        handleDeath.accept(context, tank, otherTank);
                    }
                }
                case BasicWall wall -> wall.setDurability(wall.getDurability() - damage);
                case DestructibleFloor floor -> {
                    if (floor.get(Attribute.DESTROYED).orElse(false))
                        return;
                    floor.put(Attribute.DURABILITY, floor.getUnsafe(Attribute.DURABILITY) - damage);
                    if (floor.getUnsafe(Attribute.DURABILITY) <= 0) {
                        floor.put(Attribute.DESTROYED, true);
                    }
                }
                default -> {
                }
            }
        });
    }

    public static final PlayerConditionRule SHOOT_V3 = spendActionToShootWithDeathHandleHitBoolean(
            LineOfSight::hasLineOfSightV3,
            (context, tank, targetTank) -> tank.put(Attribute.GOLD, tank.getOrElse(Attribute.GOLD, 0) + targetTank.getUnsafe(Attribute.GOLD) + targetTank.getUnsafe(Attribute.BOUNTY)));

    public static final PlayerConditionRule SHOOT_V4 = spendActionToShootWithDeathHandleHitBoolean(
            LineOfSight::hasLineOfSightV4,
            (context, tank, dead) -> {
                tank.put(Attribute.GOLD, tank.getOrElse(Attribute.GOLD, 0) + dead.getUnsafe(Attribute.BOUNTY));
                switch (dead.getUnsafe(Attribute.GOLD)) {
                    case 0 -> {
                    }
                    case 1 -> tank.put(Attribute.GOLD, tank.getOrElse(Attribute.GOLD, 0) + 1);
                    default -> {
                        // Tax is target tank gold / 4, rounded
                        int tax = (dead.getUnsafe(Attribute.GOLD) + 2) / 4;
                        tank.put(Attribute.GOLD, tank.getOrElse(Attribute.GOLD, 0) + dead.getUnsafe(Attribute.GOLD) - tax);
                        Council council = context.getState().getCouncil();
                        council.put(Attribute.COFFER, council.getUnsafe(Attribute.COFFER) + tax);
                    }
                }
            });

    public static final PlayerConditionRule PROPOSED_SHOOT_V5 = spendActionToShootWithDeathHandleHitDamage(
            LineOfSight::hasLineOfSightV4,
            (context, subject, target) -> {
                target.put(Attribute.GOLD, target.getOrElse(Attribute.GOLD, 0) + target.getOrElse(Attribute.BOUNTY, 0));
                target.put(Attribute.ONLY_LOOTABLE_BY, subject.getPlayerRef());
            }
    );

    public static final PlayerConditionRule SHOOT_V5 = spendActionToShootWithDeathHandleHitBoolean(
        LineOfSight::hasLineOfSightV4,
        (context, subjectTank, target) -> {
            target.put(Attribute.GOLD, target.getOrElse(Attribute.GOLD, 0) + target.getOrElse(Attribute.BOUNTY, 0));
            target.put(Attribute.ONLY_LOOTABLE_BY, subjectTank.getPlayerRef());
        });

    public static class ActionKeys {

        public static final String SHOOT = "shoot";
        public static final String MOVE = "move";
        public static final String DONATE = "donate";
        public static final String BUY_ACTION = "buy_action";
        public static final String UPGRADE_RANGE = "upgrade_range";
        public static final String LOOT = "loot";

        public static final String SPAWN_WALL = "spawn_wall";
        public static final String SPAWN_LAVA = "spawn_lava";
        public static final String SMITE = "smite";
        public static final String HEAL = "heal";
        public static final String SLOW = "slow";
        public static final String HASTEN = "hasten";

        public static final String STIMULUS = "stimulus";
        public static final String GRANT_LIFE = "grant_life";
        public static final String BOUNTY = "bounty";
    }
}