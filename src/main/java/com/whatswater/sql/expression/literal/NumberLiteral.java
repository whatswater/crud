package com.whatswater.sql.expression.literal;


import com.whatswater.sql.expression.Literal;

/**
 * 数字字面量
 * @author maxwell
 *
 */
public class NumberLiteral implements Literal {
    final Number value;
    public NumberLiteral(Number value) {
        this.value = value;
    }

    public static NumberLiteral ZERO = new NumberLiteral(0);
    public static NumberLiteral ONE = new NumberLiteral(1);
    public static NumberLiteral TWO = new NumberLiteral(2);
    public static NumberLiteral THREE = new NumberLiteral(3);
    public static NumberLiteral FOUR = new NumberLiteral(4);
    public static NumberLiteral FIVE = new NumberLiteral(5);
    public static NumberLiteral SIX = new NumberLiteral(6);
    public static NumberLiteral SEVEN = new NumberLiteral(7);
    public static NumberLiteral EIGHT = new NumberLiteral(8);
    public static NumberLiteral NINE = new NumberLiteral(9);
    public static NumberLiteral TEN = new NumberLiteral(10);

    public static NumberLiteral valueOf(int i) {
        if(0 <= i && i <= 10) {
            switch(i) {
                case 0:
                    return ZERO;
                case 1:
                    return ONE;
                case 2:
                    return TWO;
                case 3:
                    return THREE;
                case 4:
                    return FOUR;
                case 5:
                    return FIVE;
                case 6:
                    return SIX;
                case 7:
                    return SEVEN;
                case 8:
                    return EIGHT;
                case 9:
                    return NINE;
                case 10:
                    return TEN;
            }
        }
        return new NumberLiteral(i);
    }

    public Number getValue() {
        return value;
    }
}
