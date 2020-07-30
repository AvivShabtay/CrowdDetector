package acs.logic;

public class ActionNotPermittedException extends RuntimeException {
    private static final long serialVersionUID = -9082209024361508920L;

    public ActionNotPermittedException() {
        super();
    }

    public ActionNotPermittedException(String action, Throwable cause) {
        super(action, cause);
    }

    public ActionNotPermittedException(String action) {
        super(action);
    }

    public ActionNotPermittedException(Throwable cause) {
        super(cause);
    }
}
