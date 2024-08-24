package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "LootBox")
public class LootBox extends GenericElement implements IUnit {
    public enum LootBoxType {
        HEALTH_PACK("health_pack", Attribute.DURABILITY);

        private String typeName;
        private Attribute<Integer> attribute;

        private LootBoxType(String typeName, Attribute<Integer> attribute) {
            this.typeName = typeName;
            this.attribute = attribute;
        }

        protected static LootBoxType fromTypeName(String typeName) {
            for(LootBoxType type : LootBoxType.values()) {
                if(type.getTypeName().equals(typeName)) {
                    return type;
                }
            }

            throw new Error(typeName + " is not a valid loot box type");
        }

        protected String getTypeName() {
            return typeName;
        }

        protected Attribute<Integer> getAttribute() {
            return attribute;
        }

        JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("type", "health_pack");
            return json;
        }
    }

    public static Attribute<String> LOOT_BOX_TYPE = new Attribute<>("LOOT_BOX_TYPE", String.class);

    public LootBox(LootBoxType type, int loot) {
        super();
        put(LOOT_BOX_TYPE, type.getTypeName());
        put(type.getAttribute(), loot);
    }

    public LootBox(JSONObject json) {
        super(json);
    }

    public LootBoxType getType() {
        return LootBoxType.fromTypeName(getUnsafe(LOOT_BOX_TYPE));
    }

    public boolean isEmpty() {
        return !has(getType().getAttribute());
    }

    public void transferLoot(AttributeContainer reciever) {
        Attribute<Integer> attribute = getType().getAttribute();
        reciever.put(attribute, reciever.getOrElse(attribute, 0) + getOrElse(attribute, 0));
        remove(attribute);
    }
}
