package tech.me.direct.debit.service.mandate.create.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND_EXCEPTION = "USER_NOT_FOUND_EXCEPTION";
    private final String userId;

    public UserNotFoundException() {
        super(USER_NOT_FOUND_EXCEPTION);
        this.userId = null;
    }

    public UserNotFoundException(String userId) {
        super(USER_NOT_FOUND_EXCEPTION);
        this.userId = userId;
    }
}
