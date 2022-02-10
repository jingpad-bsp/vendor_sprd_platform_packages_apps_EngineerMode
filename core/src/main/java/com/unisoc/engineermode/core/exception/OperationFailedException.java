package com.unisoc.engineermode.core.exception;

public class OperationFailedException extends EmException {
    private ErrorCode code = ErrorCode.UNKNOWN;

    public OperationFailedException() {
        super();
    }
    public OperationFailedException(String msg) {
        super(msg);
        this.code = ErrorCode.UNKNOWN;
    }

    public OperationFailedException(ErrorCode code) {
        super();
        this.code = code;
    }

    public OperationFailedException(ErrorCode code, String msg) {
        super(msg);
        this.code = code;
    }

    public ErrorCode getCode() {
        return this.code;
    }

}
