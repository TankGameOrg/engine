package pro.trevor.tankgame.rule.type;

public interface ICooldownPlayerElement extends IPlayerElement {

    // Returns the time in milliseconds since epoch of the last usage of the specified rule or 0 if it has not been used
    long getLastUsage(String rule);

    // Sets the time last used of the specified rule to the given time in milliseconds since epoch
    void setLastUsage(String rule, long time);

}
