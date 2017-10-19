package cn.ieclipse.smartim.exception;

public class HttpException extends RuntimeException {
    private int code;
    
    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}