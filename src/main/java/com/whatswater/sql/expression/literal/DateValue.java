package com.whatswater.sql.expression.literal;

import java.time.LocalDate;

public class DateValue implements Literal {
    private LocalDate value;

    public DateValue(LocalDate date) {
        this.value = date;
    }

    public LocalDate getValue() {
        return value;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.VALUE_DATE;
    }
}
