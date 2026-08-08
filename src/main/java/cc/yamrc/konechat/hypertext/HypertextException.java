package cc.yamrc.konechat.hypertext;

public class HypertextException extends RuntimeException {
    public HypertextException(String message) {
        super(message);
    }

    public HypertextException(String message, Throwable cause) {
        super(message, cause);
    }
}
