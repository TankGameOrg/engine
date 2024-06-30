package pro.trevor.tankgame.rule.impl.version4;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.version3.ApiV3;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.Council;

public class ApiV4 extends ApiV3 implements IApi {

    public ApiV4() {
        super(new Ruleset());
    }

    @Override
    public void ingestAction(JSONObject json) {
        if (!Attribute.RUNNING.fromOrElse(state, true)) {
            throw new Error("The game is over; no actions can be submitted");
        }

        if (json.keySet().contains(JsonKeys.DAY)) {
            applyTick(state, ruleset);
        } else {
            String subject = json.getString(JsonKeys.SUBJECT);
            String action = json.getString(JsonKeys.ACTION);
            long time = json.getLong(JsonKeys.TIME);

            switch (action) {
                case PlayerRules.ActionKeys.MOVE -> {
                    String positionString = json.getString(JsonKeys.TARGET);
                    Position position = new Position(positionString);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.MOVE).apply(state, tank, time, position);
                }
                case PlayerRules.ActionKeys.SHOOT -> {
                    String location = json.getString(JsonKeys.TARGET);
                    Position position = new Position(location);
                    boolean hit = json.getBoolean(JsonKeys.HIT);
                    Tank tank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.SHOOT).apply(state, tank, time, position, hit);

                }
                case PlayerRules.ActionKeys.DONATE -> {
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.DONATION);
                    Tank subjectTank = getTank(subject);
                    Tank targetTank = getTank(target);
                    getRule(Tank.class, PlayerRules.ActionKeys.DONATE).apply(state, subjectTank, time, targetTank, quantity);
                }
                case PlayerRules.ActionKeys.BUY_ACTION -> {
                    int quantity = json.getInt(JsonKeys.GOLD);
                    Tank subjectTank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.BUY_ACTION).apply(state, subjectTank, time, quantity);
                }
                case PlayerRules.ActionKeys.UPGRADE_RANGE -> {
                    Tank subjectTank = getTank(subject);
                    getRule(Tank.class, PlayerRules.ActionKeys.UPGRADE_RANGE).apply(state, subjectTank, time);
                }

                case PlayerRules.ActionKeys.STIMULUS -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, PlayerRules.ActionKeys.STIMULUS).apply(state, state.getCouncil(), targetTank);
                }
                case PlayerRules.ActionKeys.BOUNTY -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    int quantity = json.getInt(JsonKeys.BOUNTY);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, PlayerRules.ActionKeys.BOUNTY).apply(state, state.getCouncil(), targetTank, quantity);
                }
                case PlayerRules.ActionKeys.GRANT_LIFE -> {
                    assert subject.equals(COUNCIL);
                    String target = json.getString(JsonKeys.TARGET);
                    Tank targetTank = getTank(target);
                    getMetaRule(Council.class, PlayerRules.ActionKeys.GRANT_LIFE).apply(state, state.getCouncil(), targetTank);
                }
                default -> throw new Error("Unexpected action: " + action);
            }
        }

        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }
}
