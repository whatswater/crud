package com.whatswater.sql.expression.literal;

import com.whatswater.sql.expression.Literal;

import java.time.LocalDateTime;

public class DateTimeExpression implements Literal {
    private LocalDateTime value;

    public DateTimeExpression(LocalDateTime dateTime) {
        this.value = dateTime;
    }

    public LocalDateTime getValue() {
        return value;
    }
}
