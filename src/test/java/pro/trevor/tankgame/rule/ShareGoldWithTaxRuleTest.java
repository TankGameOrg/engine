package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.util.TankBuilder;

public class ShareGoldWithTaxRuleTest {

    @Test
    public void DeadTankCannotDonateGold() {
        Tank sender = TankBuilder.buildV3Tank().with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, true).finish();
        Tank reciever = TankBuilder.buildV3Tank().with(Attribute.GOLD, 0).finish();

        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(new TestState(), sender, reciever, 3 /* donationAmount */));
    }

    @Test
    public void NoGoldCannotDonate() {
        Tank sender = TankBuilder.buildV3Tank().finish();
        Tank reciever = TankBuilder.buildV3Tank().with(Attribute.GOLD, 0).finish();

        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(new TestState(), sender, reciever, 3 /* donationAmount */));
    }

    @Test
    public void TargetCantHoldGoldCannotDonate() {
        Tank sender = TankBuilder.buildV3Tank().with(Attribute.GOLD, 5).finish();
        Tank reciever = TankBuilder.buildV3Tank().finish();

        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(new TestState(), sender, reciever, 3 /* donationAmount */));
    }

    @ParameterizedTest()
    @CsvSource({
            "3, 3, 1", /* not enough gold with tax */
            "4, 3, 2", /* not enough gold with tax other than 1 */
            "2, 3, 0", /* not enough gold to begin with */
    })
    public void NotEnoughGoldCannotDonate(int senderGold, int donation, int tax) {
        Tank sender = TankBuilder.buildV3Tank().with(Attribute.GOLD, senderGold).finish();
        Tank reciever = TankBuilder.buildV3Tank().with(Attribute.GOLD, 0).finish();

        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(tax);
        assertFalse(rule.canApply(new TestState(), sender, reciever, donation));
    }

    @Test
    public void NegativeGoldCannotBeDonated() {
        Tank sender = TankBuilder.buildV3Tank().with(Attribute.GOLD, 5).finish();
        Tank reciever = TankBuilder.buildV3Tank().with(Attribute.GOLD, 0).finish();

        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(new TestState(), sender, reciever, -3 /* donationAmount */));
    }

    @Test
    public void CannotDonateToOutOfRangeTank() {
        Tank sender = TankBuilder.buildV3Tank().with(Attribute.GOLD, 5).with(Attribute.RANGE, 1).finish();
        Tank reciever = TankBuilder.buildV3Tank()
            .at(new Position("A3")).with(Attribute.GOLD, 0).finish();

        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(1);
        assertFalse(rule.canApply(new TestState(), sender, reciever, 1 /* donationAmount */));
    }

    @ParameterizedTest()
    @CsvSource({
            /* senderStartingGold, recieverStartingGold, startingCoffer, tax, donation */
            "  5,                  0,                    0,              3,   2",
            "  4,                  0,                    2,              2,   2",
            "  3,                  4,                    0,              1,   2",
            "  15,                 34,                   19,             3,   11",
    })
    public void GoldIsTransferedCorrectly(int senderStartingGold, int recieverStartingGold, int startingCoffer, int tax, int donation) {
        Tank sender = TankBuilder.buildV3Tank().with(Attribute.GOLD, senderStartingGold).finish();
        Tank reciever = TankBuilder.buildV3Tank().with(Attribute.GOLD, recieverStartingGold).finish();

        int senderEndingGold = senderStartingGold - (donation + tax);
        int recieverEndingGold = recieverStartingGold + donation;
        int endingCoffer = startingCoffer + tax;

        State state = new TestState();
        Attribute.COFFER.to(state.getCouncil(), startingCoffer);
        PlayerActionRule<Tank> rule = PlayerRules.GetShareGoldWithTaxRule(tax);
        rule.apply(state, sender, reciever, donation);
        assertEquals(senderEndingGold, Attribute.GOLD.unsafeFrom(sender));
        assertEquals(recieverEndingGold, Attribute.GOLD.unsafeFrom(reciever));
        assertEquals(endingCoffer, Attribute.COFFER.unsafeFrom(state.getCouncil()));
    }
}
