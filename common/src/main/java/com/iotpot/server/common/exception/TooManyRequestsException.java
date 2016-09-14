package com.iotpot.server.common.exception;

public class TooManyRequestsException extends IoTPotBaseException {

    /**
     * The Code.
     */
    Integer code;


    public TooManyRequestsException( Integer code, String message) {
        super(message);
        this.code = code;
    }

    public TooManyRequestsException(String message) {

        super(message);

    }


    public Integer getCode() {
        return code;
    }

}
