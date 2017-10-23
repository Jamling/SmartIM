package cn.ieclipse.smartim.exception;

/**
 * 网络接口异常
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public class HttpException extends RuntimeException {
    private int code;
    
    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public HttpException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}