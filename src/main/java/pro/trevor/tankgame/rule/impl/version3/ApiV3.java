package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.RulesetDescription;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.range.DiscreteTypeRange;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.definition.range.VariableTypeRange;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.Council;

import java.util.*;

import org.json.*;
import pro.trevor.tankgame.util.Pair;

public class ApiV3 implements IApi {
    protected final RulesetDescription ruleset;
    protected State state;

    protected static final String COUNCIL = "Council";

    public ApiV3() {
        this.ruleset = IRuleset.getRuleset(new Ruleset());
    }

    protected ApiV3(IRuleset ruleset) {
        this.ruleset = IRuleset.getRuleset(ruleset);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public JSONObject getRules() {
        JSONObject rules = new JSONObject();
        rules.put("type", "rules");
        JSONArray playerRules = ruleset.getPlayerRules().toJsonRequirements();
        JSONArray metaRules = ruleset.getMetaPlayerRules().toJsonRequirements();
        metaRules.forEach(playerRules::put);
        rules.put("rules", playerRules);
        return rules;
    }

    @Override
    public void ingestAction(JSONObject json) {
        if (!Attribute.RUNNING.fromOrElse(state, true)) {
            throw new Error("The game is over; no actions can be submitted");
        }

        if (json.keySet().contains(JsonKeys.DAY)) {
            applyTick(state, ruleset);
        } else {
            String action = json.getString(JsonKeys.ACTION);
            Object subject = json.get(JsonKeys.SUBJECT);

            Optional<Pair<Class<?>, IPlayerRule<?>>> optionalRule = getRuleByName(action);
            if (optionalRule.isEmpty()) {
                throw new Error("Unexpected action: " + action);
            }

            Pair<Class<?>, IPlayerRule<?>> rulePair = optionalRule.get();
            Class<?> ruleClass = rulePair.left();
            IPlayerRule<Object> rule = (IPlayerRule<Object>) rulePair.right();

            // If the subject given was JSON, try to construct it into an Object of its given class;
            // otherwise, send the Object through to attempt to be cast to the rule subject type.
            if (subject instanceof JSONObject subjectJson) {
                subject = Codec.decodeJson(subjectJson);
            }

            // Also, if the subject Object is a String, try to decode it as a Tank (via name) or position;
            // This could result in issues if a tank/player has the same name as a grid space (e.g., B3).
            if (subject instanceof String subjectString) {
                subject = fromString(subjectString);
            }

            try {
                rule.apply(state, ruleClass.cast(subject), getArguments(rule, json));
            } catch (ClassCastException e) {
                throw new Error("Unable to cast given subject to class " + ruleClass.getSimpleName(), e);
            }
        }

        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }

    private boolean isPosition(String string) {
        char c = string.charAt(0);
        boolean canParseRemaining = false;
        try {
            Integer.parseInt(string.substring(1));
            canParseRemaining = true;
        } catch (Exception ignored) {}
        return canParseRemaining && (c >= 'A' && c <= 'z');
    }

    private Object fromString(String string) {
        if (string.equals(COUNCIL)) {
            return state.getCouncil();
        }
        Optional<Tank> optionalTank = state.getBoard().gatherUnits(Tank.class).stream()
                .filter(t -> t.getPlayer().getName().equals(string)).findFirst();
        if (optionalTank.isPresent()) {
            return optionalTank.get();
        } else if (isPosition(string)) {
            return new Position(string);
        } else {
            throw new Error("Subject string could is not a living tank's player nor position: " + string);
        }
    }

    private Object[] getArguments(IPlayerRule<?> rule, JSONObject json) {
        Object[] arguments = new Object[rule.parameters().length];
        for (int i = 0; i < arguments.length; ++i) {
            Object input = json.get(rule.parameters()[i].getName());
            if (input instanceof JSONObject inputJson) {
                arguments[i] = Codec.decodeJson(inputJson);
            } else if (input instanceof String inputString) {
                arguments[i] = fromString(inputString);
            } else {
                // Try to assume that, if it is not JSON or position/tank string, it is a primitive
                arguments[i] = input;
            }
        }
        return arguments;
    }

    @Override
    public JSONObject getPossibleActions(String player) {
        JSONObject actions = new JSONObject();
        actions.put("error", false);
        actions.put("type", "possible_actions");
        actions.put("player", player);

        JSONArray actionsArray = new JSONArray();
        List<IPlayerRule<?>> rules;
        Class<?> type;
        Object subject;

        if (player.equals(COUNCIL)) {
            type = Council.class;
            rules = ruleset.getMetaPlayerRules().get(type);
            subject = state.getCouncil();
        } else if (state.getPlayer(player).isPresent()) {
            type = Tank.class;
            rules = ruleset.getPlayerRules().get(type);
            Optional<Tank> tank = state.getBoard().gather(Tank.class).stream()
                    .filter((t) -> t.getPlayer().getName().equals(player))
                    .findFirst();
            if (tank.isPresent()) {
                subject = tank.get();
            } else {
                actions.put("actions", actionsArray);
                return actions;
            }
        } else {
            throw new Error("Unknown player: " + player);
        }

        assert type.isInstance(subject);

        for (IPlayerRule<?> rule : rules) {
            JSONObject actionJson = new JSONObject();
            actionJson.put("rule", rule.name());
            actionJson.put("subject", type.getSimpleName().toLowerCase());

            // find all states of each parameter
            JSONArray fields = new JSONArray();
            for (TypeRange<?> field : rule.parameters()) {
                if (field instanceof VariableTypeRange<?,?> variableField) {
                    VariableTypeRange<Object, ?> genericField = (VariableTypeRange<Object, ?>) variableField;
                    genericField.generate(state, subject);
                }
                fields.put(field.toJson());
            }
            actionJson.put("fields", fields);
            actionsArray.put(actionJson);
        }

        actions.put("actions", actionsArray);
        return actions;
    }

    private Set<List<Object>> allPermutations(IPlayerRule<?> rule, State state, Object subject) {
        IPlayerRule<Object> genericRule = (IPlayerRule<Object>) rule;
        DiscreteTypeRange<?>[] parameters = new DiscreteTypeRange<?>[rule.parameters().length];
        for (int i = 0; i < rule.parameters().length; ++i) {
            TypeRange<?> parameter = rule.parameters()[i];
            if (parameter instanceof DiscreteTypeRange<?> discreteParameter) {
                parameters[i] = discreteParameter;
            } else {
                throw new Error(String.format("Given parameter `%s` is not discrete", parameter.getName()));
            }
        }
        return allPermutations(genericRule, parameters, state, subject);
    }

    private Set<List<Object>> allPermutations(IPlayerRule<Object> rule, DiscreteTypeRange<?>[] discreteParameters, State state, Object subject, Object... permutation) {
        if (permutation.length == discreteParameters.length) {
            if (rule.canApply(state, subject, permutation)) {
                return new HashSet<>(List.of(List.of(permutation)));
            }
        } else {
            DiscreteTypeRange<?> currentParameter = discreteParameters[permutation.length];
            if (currentParameter instanceof VariableTypeRange<?,?> variableRange) {
                VariableTypeRange<Object, ?> genericRange = (VariableTypeRange<Object, ?>) variableRange;
                genericRange.generate(state, subject);
            }
            Set<List<Object>> output = new HashSet<>();
            for (Object possibleValue : currentParameter.getElements()) {
                Object[] newPermutation = new Object[permutation.length + 1];
                System.arraycopy(permutation, 0, newPermutation, 0, permutation.length);
                newPermutation[permutation.length] = possibleValue;
                output.addAll(allPermutations(rule, discreteParameters, state, subject, newPermutation));
            }
            return output;
        }
        return new HashSet<>(0);
    }

    protected Optional<Pair<Class<?>, IPlayerRule<?>>> getRuleByName(String name) {
        Optional<Pair<Class<?>, IPlayerRule<?>>> rule = ruleset.getPlayerRules().getByName(name);
        if (rule.isPresent()) {
            return rule;
        }

        return ruleset.getMetaPlayerRules().getByName(name);

    }

    protected <T extends IPlayerElement> IPlayerRule<T> getRule(Class<T> t, String name) {
        List<IPlayerRule<T>> rules = ruleset.getPlayerRules().getExact(t);
        if (rules.isEmpty()) {
            throw new Error(String.format("No rule for `%s`", t.getSimpleName()));
        }

        List<IPlayerRule<T>> namedRules = rules.stream().filter(r -> r.name().equals(name)).toList();
        if (namedRules.isEmpty()) {
            throw new Error(String.format("No rule named `%s`", name));
        }

        return namedRules.getFirst();
    }

    protected <T extends IPlayerElement> IPlayerRule<T> getMetaRule(Class<T> t, String name) {
        List<IPlayerRule<T>> rules = ruleset.getMetaPlayerRules().getExact(t);
        if (rules.isEmpty()) {
            throw new Error(String.format("No rule for `%s`", t.getSimpleName()));
        }

        List<IPlayerRule<T>> namedRules = rules.stream().filter(r -> r.name().equals(name)).toList();
        if (namedRules.isEmpty()) {
            throw new Error(String.format("No rule named `%s`", name));
        }

        return namedRules.getFirst();
    }

    protected Tank getTank(String player) {
        return state.getBoard().gatherUnits(Tank.class).stream()
                .filter(t -> t.getPlayer().getName().equals(player)).toList().getFirst();
    }

    protected static void enforceInvariants(State state, RulesetDescription ruleset) {
        state.getBoard().gatherAll().forEach((x) -> ruleset.getEnforcerRules().enforceRules(state, x));
        state.getMetaElements().forEach((x) -> ruleset.getMetaEnforcerRules().enforceRules(state, x));
    }

    protected static void applyConditionals(State state, RulesetDescription ruleset) {
        state.getBoard().gatherAll().forEach((x) -> ruleset.getConditionalRules().applyRules(state, x));
        state.getMetaElements().forEach((x) -> ruleset.getMetaConditionalRules().applyRules(state, x));
    }

    protected static void applyTick(State state, RulesetDescription ruleset) {
        state.getBoard().gatherAll().forEach((x) -> ruleset.getTickRules().applyRules(state, x));
        state.getMetaElements().forEach((x) -> ruleset.getMetaTickRules().applyRules(state, x));
    }

    protected static class JsonKeys {
        public static final String DAY = "day";
        public static final String SUBJECT = "subject";
        public static final String ACTION = "action";
        public static final String POSITION = "position";
        public static final String TARGET = "target";
        public static final String HIT = "hit";
        public static final String GOLD = "gold";
        public static final String DONATION = "donation";
        public static final String BOUNTY = "bounty";
        public static final String TIME = "timestamp";
    }
}
