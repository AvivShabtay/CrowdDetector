package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ActionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -8868940317682165686L;

    public ActionNotFoundException() {
        super();
    }

    public ActionNotFoundException(String action, Throwable cause) {
        super(action, cause);
    }

    public ActionNotFoundException(String action) {
        super(action);
    }

    public ActionNotFoundException(Throwable cause) {
        super(cause);
    }
}
