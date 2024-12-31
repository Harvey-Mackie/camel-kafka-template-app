package com.example.accounting.exception;

public class RetryableException extends RuntimeException {
    public RetryableException(String message){
        super(message, null, false, false);
    }

    // overriding to prevent stack trace being logged - unneccessary noise.
    // exceptions are only used due to camel limitations with seek on same consumer as routes are defined.
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
