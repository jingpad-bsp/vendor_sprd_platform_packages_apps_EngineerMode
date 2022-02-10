package com.unisoc.engineermode.core.exception;

public enum ErrorCode {
    UNKNOWN(100),
    AT_RETURN_ERROR(101),
    AT_RETURN_PARSE_ERROR(102),
    SOCKET_CONN_FAILED(103),
    CMD_EXEC_ERROR(104);

    private int code;
    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
