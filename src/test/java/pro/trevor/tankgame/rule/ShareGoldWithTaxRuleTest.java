package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.TestUtilities;

public class ShareGoldWithTaxRuleTest {

    PlayerRuleContext makeContext(State state, PlayerRef subject, GenericTank reciever, int gold) {
        return new ContextBuilder(state, subject)
            .withTarget(reciever)
            .with(Attribute.DONATION, gold)
            .finish();
    }

    boolean canApply(IPlayerRule rule, State state, PlayerRef subject, GenericTank reciever, int gold) {
        return rule.canApply(makeContext(state, subject, reciever, gold)).isEmpty();
    }

    @Test
    public void DeadTankCannotDonateGold() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, true).with(Attribute.RANGE, 1).finish();
        GenericTank receiver = TankBuilder.buildTank()
            .at(new Position("B1")).with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(1);
        assertFalse(canApply(rule, TestUtilities.generateBoard(1, 1, sender, receiver), sender.getPlayerRef(), receiver, 3));
    }

    @Test
    public void NoGoldCannotDonate() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.RANGE, 1).finish();
        GenericTank receiver = TankBuilder.buildTank()
            .at(new Position("B1")).with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(1);
        assertFalse(canApply(rule, TestUtilities.generateBoard(1, 1, sender, receiver), sender.getPlayerRef(), receiver, 3));
    }

    @Test
    public void TargetCantHoldGoldCannotDonate() {
        GenericTank sender = TankBuilder.buildTank()
            .with(Attribute.RANGE, 1).with(Attribute.GOLD, 5).finish();
        GenericTank receiver = TankBuilder.buildTank().at(new Position("B1")).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(1);
        assertFalse(canApply(rule, TestUtilities.generateBoard(1, 1, sender, receiver), sender.getPlayerRef(), receiver, 3));
    }

    @ParameterizedTest()
    @CsvSource({
            "3, 3, 1", /* not enough gold with tax */
            "4, 3, 2", /* not enough gold with tax other than 1 */
            "2, 3, 0", /* not enough gold to begin with */
    })
    public void NotEnoughGoldCannotDonate(int senderGold, int donation, int tax) {
        GenericTank sender = TankBuilder.buildTank()
            .with(Attribute.RANGE, 1).with(Attribute.GOLD, senderGold).finish();
        GenericTank receiver = TankBuilder.buildTank().at(new Position("B1")).with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(tax);
        assertFalse(canApply(rule, TestUtilities.generateBoard(1, 1, sender, receiver), sender.getPlayerRef(), receiver, donation));
    }

    @Test
    public void NegativeGoldCannotBeDonated() {
        GenericTank sender = TankBuilder.buildTank()
            .with(Attribute.RANGE, 1).with(Attribute.GOLD, 5).finish();
        GenericTank receiver = TankBuilder.buildTank().at(new Position("B1")).with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(1);
        assertFalse(canApply(rule, TestUtilities.generateBoard(1, 1, sender, receiver), sender.getPlayerRef(), receiver, -3));
    }

    @Test
    public void CannotDonateToOutOfRangeTank() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, 5).with(Attribute.RANGE, 1).finish();
        GenericTank receiver = TankBuilder.buildTank()
            .at(new Position("A3")).with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(1);
        assertFalse(canApply(rule, TestUtilities.generateBoard(3, 3, sender, receiver), sender.getPlayerRef(), receiver, 1));
    }

    @ParameterizedTest()
    @CsvSource({
            /* senderStartingGold, receiverStartingGold, startingCoffer, tax, donation */
            "  5,                  0,                    0,              3,   2",
            "  4,                  0,                    2,              2,   2",
            "  3,                  4,                    0,              1,   2",
            "  15,                 34,                   19,             3,   11",
    })
    public void GoldIsTransferredCorrectly(int senderStartingGold, int receiverStartingGold, int startingCoffer, int tax, int donation) {
        GenericTank sender = TankBuilder.buildTank()
            .with(Attribute.RANGE, 1).with(Attribute.GOLD, senderStartingGold).finish();
        GenericTank receiver = TankBuilder.buildTank()
            .at(new Position("B1")).with(Attribute.GOLD, receiverStartingGold).finish();

        int senderEndingGold = senderStartingGold - (donation + tax);
        int receiverEndingGold = receiverStartingGold + donation;
        int endingCoffer = startingCoffer + tax;

        State state = TestUtilities.generateBoard(5, 5, sender, receiver);
        state.getCouncil().put(Attribute.COFFER, startingCoffer);
        IPlayerRule rule = PlayerRules.getShareGoldWithTaxToCofferRule(tax);
        rule.apply(makeContext(state, sender.getPlayerRef(), receiver, donation));
        assertEquals(senderEndingGold, sender.getUnsafe(Attribute.GOLD));
        assertEquals(receiverEndingGold, receiver.getUnsafe(Attribute.GOLD));
        assertEquals(endingCoffer, state.getCouncil().getUnsafe(Attribute.COFFER));
    }
}
