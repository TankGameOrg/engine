package pro.trevor.tankgame.util;

public interface IRandom extends IJsonObject {

    /**
     * The smallest unit representable by double precision float without potential loss of precision.
     */
    double DOUBLE_UNIT = 0x1.0p-53d; // 1.0d / (1L << Double.PRECISION)

    /**
     * The smallest unit representable by single precision float without potential loss of precision.
     */
    float FLOAT_UNIT = 0x1.0p-24f; // 1.0f / (1 << Float.PRECISION)

    /**
     * Sets the initial seed based on the given seed.
     * @param seed the given seed.
     */
    void setSeed(long seed);

    /**
     * Returns a random boolean: either true or false.
     * @return a random boolean: either true or false.
     */
    boolean nextBoolean();

    /**
     * Returns a random integer within [Integer.MIN_VALUE, Integer.MAX_VALUE].
     * @return a random integer within [Integer.MIN_VALUE, Integer.MAX_VALUE].
     */
    int nextInt();

    /**
     * Returns a random integer within [0, bound).
     * @return a random integer within [0, bound).
     */
    default int nextInt(int bound) {
        return nextInt() % bound;
    }

    /**
     * Returns a random long within [Long.MIN_VALUE, Long.MAX_VALUE].
     * @return a random long within [Long.MIN_VALUE, Long.MAX_VALUE].
     */
    long nextLong();

    /**
     * Returns a random long within [0, bound).
     * @return a random long within [0, bound).
     */
    default long nextLong(long bound) {
        return nextLong() % bound;
    }

    /**
     * Returns a random float within [0, 1) with steps of size FLOAT_UNIT.
     * @return a random float within [0, 1).
     */
    float nextFloat();

    /**
     * Returns a random double within [0, 1) with steps of size DOUBLE_UNIT.
     * @return a random double within [0, 1).
     */
    double nextDouble();

}
