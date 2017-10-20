package cn.ieclipse.smartim.exception;

public class LogicException extends RuntimeException {
    private int code;
    
    public LogicException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
