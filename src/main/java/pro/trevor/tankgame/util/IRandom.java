package pro.trevor.tankgame.util;

public interface IRandom extends IJsonObject {

    double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << Double.PRECISION)
    float FLOAT_UNIT = 0x1.0p-24f; // 1.0f / (1 << Float.PRECISION)

    // Either true or false
    boolean nextBoolean();

    // Between Integer.MIN_VALUE and Integer.MAX_VALUE
    int nextInt();

    // Between Long.MIN_VALUE and Long.MAX_VALUE
    long nextLong();

    // Between 0.0f and (1.0f - FLOAT_UNIT)
    float nextFloat();

    // Between 0.0d and (1.0d - DOUBLE_UNIT)
    double nextDouble();

}
