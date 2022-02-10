package com.whatswater.sql.expression.literal;

import com.whatswater.sql.expression.Literal;

import java.time.LocalDate;

public class DateValue implements Literal {
    private LocalDate value;

    public DateValue(LocalDate date) {
        this.value = date;
    }

    public LocalDate getValue() {
        return value;
    }
}
