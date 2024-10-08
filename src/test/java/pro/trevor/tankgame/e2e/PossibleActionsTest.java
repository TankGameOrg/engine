package pro.trevor.tankgame.e2e;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.actions.DieRollLogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.DieRollResult;
import pro.trevor.tankgame.rule.definition.actions.EnumeratedLogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.LogFieldValueDescriptor;
import pro.trevor.tankgame.rule.definition.actions.PossibleAction;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV3RulesetRegister;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV4RulesetRegister;
import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.meta.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.trevor.tankgame.e2e.EndToEndTestUtils.readFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PossibleActionsTest {
    /**
     * Load the specified inital state, start day 1, and confirm that we can apply all of the actions
     */
    private void testAllPossibleActions(IRulesetRegister rulesetRegister, String initialStatePath) {
        Api api = new Api(rulesetRegister);
        api.setState((State) Codec.decodeJson(new JSONObject(readFile(initialStatePath))));

        api.ingestAction(new LogEntry(Map.of(
            Attribute.DAY, 1
        )));

        testAllPossibleActions(api);
    }

    /**
     * Given an API conferm that we can apply all possible actions on the current state
     */
    private void testAllPossibleActions(Api api) {
        for(Player player : api.getState().getPlayers()) {
            List<PossibleAction> actions = api.getPossibleActions(player.toRef());
            for(PossibleAction action : actions) {
                // We can't build actions with errors
                if(!action.getErrors().isEmpty()) {
                    continue;
                }

                LinkedAttributeList<?> logEntryBuilder = new LinkedAttributeList<>(Attribute.ACTION, action.getRuleName())
                    .with(Attribute.SUBJECT, player.toRef())
                    .with(Attribute.TIMESTAMP, System.currentTimeMillis() / 1000L);

                buildAllPermutations(api, action.getFieldSpecs(), logEntryBuilder, 0);
            }
        }
    }

    private void buildAllPermutations(Api api, List<LogFieldSpec<?>> fieldSpecs, LinkedAttributeList<?> logEntryBuilder, int index) {
        // We've build all of the fields try to apply the action
        if(index == fieldSpecs.size()) {
            LogEntry logEntry = logEntryBuilder.buildLogEntry();
            assertEquals(List.of(), api.canIngestAction(logEntry));
            return;
        }

        LogFieldSpec<?> spec = fieldSpecs.get(index);
        switch(spec) {
            case EnumeratedLogFieldSpec<?> enumSpec -> enumerateField(api, fieldSpecs, logEntryBuilder, index, enumSpec);
            case DieRollLogFieldSpec<?> dieRoll -> enumerateField(api, fieldSpecs, logEntryBuilder, index, dieRoll, new LinkedList<>(), 0, 0);
            default -> throw new IllegalArgumentException("Unsupported log field spec: " + spec.getClass().getName());
        }
    }

    private void enumerateField(Api api, List<LogFieldSpec<?>> fieldSpecs, LinkedAttributeList<?> logEntryBuilder, int index, EnumeratedLogFieldSpec<?> enumSpec) {
        for(LogFieldValueDescriptor<?> descriptor : enumSpec.getValueDescriptors()) {
            Attribute<?> attribute = enumSpec.getAttribute();
            LinkedAttributeList<?> logEntryBuilderWithValue = logEntryBuilder.with((Attribute<Object>) attribute, descriptor.getValue());

            List<LogFieldSpec<?>> nestedSpecs = descriptor.getNestedSpecs();
            if(!nestedSpecs.isEmpty()) {
                fieldSpecs = Stream.concat(fieldSpecs.stream(), nestedSpecs.stream()).toList();
            }

            buildAllPermutations(api, fieldSpecs, logEntryBuilderWithValue, index + 1);
        }
    }

    private void enumerateField(Api api, List<LogFieldSpec<?>> fieldSpecs, LinkedAttributeList<?> logEntryBuilder, int index, DieRollLogFieldSpec<?> dieRoll, LinkedList<Object> rollResults, int setIndex, int dieIndex) {
        if(setIndex == dieRoll.getDiceSets().size()) {
            Attribute<?> attribute = dieRoll.getAttribute();
            LinkedAttributeList<?> logEntryBuilderWithValue = logEntryBuilder.with((Attribute<Object>) attribute, new DieRollResult<>(rollResults));
            buildAllPermutations(api, fieldSpecs, logEntryBuilderWithValue, index + 1);
            return;
        }

        if(dieIndex == dieRoll.getDiceSets().get(setIndex).expandDice().size()) {
            enumerateField(api, fieldSpecs, logEntryBuilder, index, dieRoll, rollResults, setIndex + 1, 0);
            return;
        }

        for(Object value : dieRoll.getDiceSets().get(setIndex).expandDice().get(dieIndex).getSides()) {
            LinkedList<Object> modifiedResults = (LinkedList<Object>) rollResults.clone();
            modifiedResults.add(value);
            enumerateField(api, fieldSpecs, logEntryBuilder, index, dieRoll, modifiedResults, setIndex, dieIndex + 1);
        }
    }

    @Test
    public void testPossibleActionsDefaultV3() {
        testAllPossibleActions(new DefaultV3RulesetRegister(), "src/test/resources/initial-v3.json");
    }

    @Test
    public void testPossibleActionsDefaultV4() {
        testAllPossibleActions(new DefaultV4RulesetRegister(), "src/test/resources/initial-v4.json");
    }
}
