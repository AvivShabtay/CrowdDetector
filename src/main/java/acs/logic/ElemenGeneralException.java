package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ElemenGeneralException extends RuntimeException {
    private static final long serialVersionUID = -4413952889596506430L;

    public ElemenGeneralException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ElemenGeneralException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public ElemenGeneralException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ElemenGeneralException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ElemenGeneralException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
