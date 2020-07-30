package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ActionInternalException extends RuntimeException {
    private static final long serialVersionUID = 7798579558129763466L;

    public ActionInternalException() {
        super();
    }

    public ActionInternalException(String action, Throwable cause) {
        super(action, cause);
    }

    public ActionInternalException(String action) {
        super(action);
    }

    public ActionInternalException(Throwable cause) {
        super(cause);
    }
}
