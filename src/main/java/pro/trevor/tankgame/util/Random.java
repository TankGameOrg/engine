package pro.trevor.tankgame.util;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

@JsonType(name = "Random")
public class Random extends AttributeContainer implements IRandom {

    protected static final long MULTIPLIER = 0x5DEECE66DL;
    protected static final long ADDEND = 0xBL;
    protected static final long MASK = 0xFFFF_FFFF_FFFFL;

    public Random(JSONObject json) {
        super(json);
    }

    public Random(long seed) {
        super();
        setSeed(seed);
    }

    /**
     * Sets the seed without scrambling
     */
    protected void setSeedInternal(long seed) {
        put(Attribute.RNG_SEED, seed);
    }

    /**
     * Sets the seed after scrambling it.
     * @param seed the input "seed" to be scrambled and used as a seed.
     */
    @Override
    public void setSeed(long seed) {
        put(Attribute.RNG_SEED, scramble(seed));
    }

    /**
     * Gets the seed directly exposing the state of the generator.
     */
    protected long getSeedInternal() {
        return getUnsafe(Attribute.RNG_SEED);
    }

    /**
     * Scrambles the given seed in case the seed to get a pseudo-random starting seed.
     * @param seed the seed to scramble.
     * @return the scrambled seed.
     */
    protected static long scramble(long seed) {
        return (seed ^ MULTIPLIER) & MASK;
    }

    protected int next(int bits) {
        long seed = (getSeedInternal() * MULTIPLIER + ADDEND) & MASK;
        setSeedInternal(seed);
        return (int) (seed >>> (48 - bits));
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
        return ((long) next(Long.SIZE - HALF_SIZE) << HALF_SIZE) | next(HALF_SIZE);
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
}
