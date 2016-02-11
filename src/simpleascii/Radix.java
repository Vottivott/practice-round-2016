package simpleascii;

/**
 *
 * Created by Simon Moos on 2016-01-19.
 *
 * Defines a type safe and more explicit radix specifier for parsing integers, etc.
 *
 */
public enum Radix {

    Octal(8),
    Decimal(10),
    Hexadecimal(16);

    private int radix;
    Radix(int radix) {
        this.radix = radix;
    }

    /**
     *
     * Returns the radix as an int.
     *
     * @return this as an int.
     */
    public int asInt() {
        return radix;
    }

}
