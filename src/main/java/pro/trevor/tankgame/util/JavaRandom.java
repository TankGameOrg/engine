package pro.trevor.tankgame.util;

import org.json.JSONObject;

public class JavaRandom implements IRandom {

    protected static final long MULTIPLIER = 0x5DEECE66DL;
    protected static final long ADDEND = 0xBL;
    protected static final long MASK = 0xFFFF_FFFF_FFFFL;

    protected long seed;

    public JavaRandom(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        this.seed = scramble(seed);
    }

    private static long scramble(long seed) {
        return (seed ^ MULTIPLIER) & MASK;
    }

    protected int next(int bits) {
        this.seed = (seed * MULTIPLIER + ADDEND) & MASK;
        return (int) (this.seed >>> (48 - bits));
    }

    @Override
    public boolean nextBoolean() {
        return next(1) != 0;
    }

    @Override
    public int nextInt() {
        return next(Integer.SIZE);
    }

    @Override
    public long nextLong() {
        final int HALF_SIZE = 32;
        return ((long)next(Long.SIZE - HALF_SIZE) << HALF_SIZE) | next(HALF_SIZE);
    }

    @Override
    public float nextFloat() {
        return next(Float.PRECISION) * FLOAT_UNIT;
    }

    @Override
    public double nextDouble() {
        final int HALF_PRECISION = 27;
        return (((long) next(Double.PRECISION - HALF_PRECISION) << HALF_PRECISION) | next(HALF_PRECISION)) * DOUBLE_UNIT;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }
}
