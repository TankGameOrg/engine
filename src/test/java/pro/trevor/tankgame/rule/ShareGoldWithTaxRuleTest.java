package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.TestUtilities;

public class ShareGoldWithTaxRuleTest {

    @Test
    public void DeadTankCannotDonateGold() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, true).finish();
        GenericTank receiver = TankBuilder.buildTank().with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(TestUtilities.generateBoard(1, 1, sender), sender.getPlayerRef(), receiver, 3));
    }

    @Test
    public void NoGoldCannotDonate() {
        GenericTank sender = TankBuilder.buildTank().finish();
        GenericTank receiver = TankBuilder.buildTank().with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(TestUtilities.generateBoard(1, 1, sender), sender.getPlayerRef(), receiver, 3));
    }

    @Test
    public void TargetCantHoldGoldCannotDonate() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, 5).finish();
        GenericTank receiver = TankBuilder.buildTank().finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(TestUtilities.generateBoard(1, 1, sender), sender.getPlayerRef(), receiver, 3));
    }

    @ParameterizedTest()
    @CsvSource({
            "3, 3, 1", /* not enough gold with tax */
            "4, 3, 2", /* not enough gold with tax other than 1 */
            "2, 3, 0", /* not enough gold to begin with */
    })
    public void NotEnoughGoldCannotDonate(int senderGold, int donation, int tax) {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, senderGold).finish();
        GenericTank receiver = TankBuilder.buildTank().with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(tax);
        assertFalse(rule.canApply(TestUtilities.generateBoard(1, 1, sender), sender.getPlayerRef(), receiver, donation));
    }

    @Test
    public void NegativeGoldCannotBeDonated() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, 5).finish();
        GenericTank receiver = TankBuilder.buildTank().with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(TestUtilities.generateBoard(1, 1, sender), sender.getPlayerRef(), receiver, -3));
    }

    @Test
    public void CannotDonateToOutOfRangeTank() {
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, 5).with(Attribute.RANGE, 1).finish();
        GenericTank receiver = TankBuilder.buildTank()
            .at(new Position("A3")).with(Attribute.GOLD, 0).finish();

        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(TestUtilities.generateBoard(3, 3, sender), sender.getPlayerRef(), receiver, 1));
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
        GenericTank sender = TankBuilder.buildTank().with(Attribute.GOLD, senderStartingGold).finish();
        GenericTank receiver = TankBuilder.buildTank().with(Attribute.GOLD, receiverStartingGold).finish();

        int senderEndingGold = senderStartingGold - (donation + tax);
        int receiverEndingGold = receiverStartingGold + donation;
        int endingCoffer = startingCoffer + tax;

        State state = TestUtilities.generateBoard(5, 5, sender);
        Attribute.COFFER.to(state.getCouncil(), startingCoffer);
        IPlayerRule rule = PlayerRules.getShareGoldWithTaxRule(tax);
        rule.apply(state, sender.getPlayerRef(), receiver, donation);
        assertEquals(senderEndingGold, sender.getUnsafe(Attribute.GOLD));
        assertEquals(receiverEndingGold, receiver.getUnsafe(Attribute.GOLD));
        assertEquals(endingCoffer, state.getCouncil().getUnsafe(Attribute.COFFER));
    }
}
