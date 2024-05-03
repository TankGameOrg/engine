package pro.trevor.tankgame.state.board.attribute;

public enum DurableAttribute implements IAttribute {
    DURABILITY;

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

    public static final Decoder DECODER = new Decoder();
    public static class Decoder extends AbstractAttributeDecoder<DurableAttribute> {
        protected Decoder() {}

        @Override
        public DurableAttribute fromSource(String attribute) {
            if (attribute.equals(DURABILITY.name())) {
                return DURABILITY;
            } else {
                return null;
            }
        }
    }
}
