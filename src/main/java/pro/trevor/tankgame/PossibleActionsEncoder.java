package pro.trevor.tankgame;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import pro.trevor.tankgame.rule.definition.actions.DiceSet;
import pro.trevor.tankgame.rule.definition.actions.DieRollLogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.EnumeratedLogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.LogFieldValueDescriptor;
import pro.trevor.tankgame.rule.definition.actions.PossibleAction;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.util.IJsonObject;


public class PossibleActionsEncoder {
    /**
     * Encode an array of possible actions into a json format that the UI can use to build actions
     */
    public static JSONArray encodePossibleActions(List<PossibleAction> actions) {
        JSONArray jsonActions = new JSONArray();
        for(PossibleAction action : actions) {
            jsonActions.put(encodePossibleAction(action));
        }

        return jsonActions;
    }

    private static JSONObject encodePossibleAction(PossibleAction action) {
        JSONArray errors = new JSONArray(
            action.getErrors().stream()
                .map((error) -> PlayerRuleErrorEncoder.encode(error))
                .toList()
        );

        JSONObject actionJson = new JSONObject();
        actionJson.put("rule", action.getRuleName());
        if(!action.getDescription().isEmpty()) {
            actionJson.put("description", action.getDescription());
        }

        // If the action doesn't have any errors this will be an empty array aka no error
        actionJson.put("errors", errors);
        actionJson.put("fields", encodeAllFields(action.getFieldSpecs()));
        return actionJson;
    }

    private static JSONArray encodeAllFields(List<LogFieldSpec<?>> logFieldSpecs) {
        JSONArray fields = new JSONArray();
        for(LogFieldSpec<?> spec : logFieldSpecs) {
            fields.put(encodeField(spec));
        }

        return fields;
    }

    private static JSONObject encodeField(LogFieldSpec<?> spec) {
        JSONObject jsonSpec = new JSONObject();
        jsonSpec.put("field_name", spec.getAttribute().getName().toLowerCase());
        jsonSpec.put("data_type", Codec.getTypeFromClass(spec.getAttribute().getAttributeClass()));
        if(!spec.getDescription().isEmpty()) {
            jsonSpec.put("description", spec.getDescription());
        }

        switch(spec) {
            case EnumeratedLogFieldSpec<?> enumSpec -> encodeField(jsonSpec, enumSpec);
            case DieRollLogFieldSpec<?> diceSpec -> encodeField(jsonSpec, diceSpec);
            default -> throw new IllegalArgumentException("Unsupported log field spec: " + spec.getClass().getName());
        }

        return jsonSpec;
    }

    private static void encodeField(JSONObject jsonSpec, EnumeratedLogFieldSpec<?> enumSpec) {
        JSONArray options = new JSONArray();
        for(LogFieldValueDescriptor<?> descriptor : enumSpec.getValueDescriptors()) {
            JSONObject option = new JSONObject();
            option.put("pretty_name", descriptor.getPrettyName());

            Object value = descriptor.getValue();
            if(value instanceof IJsonObject jsonObject) {
                value = Codec.encodeJson(jsonObject);
            }

            option.put("value", value);

            List<LogFieldSpec<?>> nested = descriptor.getNestedSpecs();
            if(!nested.isEmpty()) {
                option.put("nested_fields", encodeAllFields(nested));
            }

            options.put(option);
        }

        jsonSpec.put("options", options);
    }

    private static void encodeField(JSONObject jsonSpec, DieRollLogFieldSpec<?> diceSpec) {
        JSONArray jsonDiceSets = new JSONArray();
        for(DiceSet<?> diceSet : diceSpec.getDiceSets()) {
            JSONArray jsonSides = new JSONArray();
            for(Object side : diceSet.getDie().getSides()) {
                jsonSides.put(side);
            }

            JSONObject jsonDie = new JSONObject();
            jsonDie.put("name", diceSet.getDie().getName());
            jsonDie.put("sides", jsonSides);

            JSONObject jsonDiceSet = new JSONObject();
            jsonDiceSet.put("die", jsonDie);
            jsonDiceSet.put("num_dice", diceSet.getNumDice());
            jsonDiceSets.put(jsonDiceSet);
        }

        jsonSpec.put("dice", jsonDiceSets);
    }
}
