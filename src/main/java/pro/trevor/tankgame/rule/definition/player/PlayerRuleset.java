package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.player.stage.IPlayerRuleStage;

import java.util.*;

public class PlayerRuleset {

    private final List<IPlayerRule> rules;
    private final Set<String> keyset;
    private final HashMap<String, IPlayerRuleStage> stages;

    public PlayerRuleset() {
        this.rules = new ArrayList<>();
        this.keyset = new HashSet<>();
        this.stages = new HashMap<>();
    }

    public void add(IPlayerRule rule) {
        rules.add(rule);
        if (rule instanceof PlayerRule playerRule) {
            playerRule.getStages().forEach((stage) -> {
                if (keyset.contains(stage)) {
                    throw new Error("Duplicate rule stage key: " + stage);
                }
                keyset.add(stage);
            });
        }
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

    public boolean validate() {
        return keyset.stream().allMatch(stages::containsKey);
    }
}
