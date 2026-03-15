package com.imbank.smartgrid.electricityproviderservice.exception;

import lombok.Getter;

@Getter
public class InvalidReadingException extends RuntimeException{
    private final String field;
    private final Object rejectedValue;
    private final String reason;

    public InvalidReadingException(String field, Object rejectedValue, String reason){
        super(String.format("Invalid %s: '%s' - %s", field, rejectedValue, reason));
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.reason = reason;
    }

}
