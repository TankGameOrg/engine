package pro.trevor.tankgame.rule.type;

public interface ICooldownPlayerElement extends IPlayerElement {

    // Returns the time in seconds when the specified rule is no longer on cooldown
    long getCooldownEnd(String rule);

    // Sets the time in seconds when the specified rule is no longer on cooldown
    void setCooldownEnd(String rule, long time);

}
