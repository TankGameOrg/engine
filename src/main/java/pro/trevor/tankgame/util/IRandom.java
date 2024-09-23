package pro.trevor.tankgame.util;

public interface IRandom extends IJsonObject {

    // These are technically implementation details, but could be useful to multiple implementers of IRandom.
    double DOUBLE_UNIT = 0x1.0p-53d; // 1.0d / (1L << Double.PRECISION)
    float FLOAT_UNIT = 0x1.0p-24f; // 1.0f / (1 << Float.PRECISION)

    void setSeed(long seed);

    // Either true or false
    boolean nextBoolean();

    // Within [Integer.MIN_VALUE, Integer.MAX_VALUE]
    int nextInt();

    // Within [0, bound)
    default int nextInt(int bound) {
        return nextInt() % bound;
    }

    // Within [Long.MIN_VALUE, Long.MAX_VALUE]
    long nextLong();

    // Within [0, bound)
    default long nextLong(long bound) {
        return nextLong() % bound;
    }

    // Between 0.0f and (1.0f - FLOAT_UNIT)
    float nextFloat();

    // Between 0.0d and (1.0d - DOUBLE_UNIT)
    double nextDouble();

}
