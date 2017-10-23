package cn.ieclipse.smartim.exception;

/**
 * 接口业务逻辑异常
 * 
 * @author Jamling
 * @date 2017年10月20日
 *       
 */
public class LogicException extends RuntimeException {
    private int code;
    
    public LogicException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public LogicException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
