package pro.trevor.tankgame.rule.definition.player;

import java.util.*;

public class PlayerRuleset {

    private final List<IPlayerRule> rules;

    public PlayerRuleset() {
        rules = new ArrayList<>();
    }

    public void add(IPlayerRule rule) {
        rules.add(rule);
    }

    public Optional<IPlayerRule> getByName(String name) {
        for (IPlayerRule rule : rules) {
            if (rule.name().equals(name)) {
                return Optional.of(rule);
            }
        }

        return Optional.empty();
    }

    public List<IPlayerRule> getAllRules() {
        return rules;
    }
}
