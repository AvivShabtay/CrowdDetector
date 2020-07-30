package acs.boundaries;

import acs.data.UserRole;
import acs.logic.UserNotFoundException;
import java.util.Arrays;

/**
 * This Boundary represents the User which is sent by the client as a JSON
 * object.
 */
public class UserBoundary {
    private String email;
    private UserRole role;
    private String username;
    private String avatar;

    public UserBoundary() {}

    public UserBoundary(String email, String role, String username, String avatar) {
        super();
        setEmail(email);
        setRole(role);
        setUsername(username);
        setAvatar(avatar);
    }

    public void validateEmail(String email) {
        if (email == null || email.length() == 0) {
            throw new UserNotFoundException("Not a valid user - Email can't be null or empty.");
        }
    }

    public String getEmail() {
        validateEmail(this.email);
        return this.email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void validateRole(String role) {
        if (!Arrays.stream(UserRole.values()).anyMatch(e -> e.name().equals(role)) || role == null) {
            throw new UserNotFoundException("Not a valid user - Not such role value.");
        }
    }

    public String getRole() {
        if (this.role == null) {
            throw new UserNotFoundException("Not a valid user - role can't be null.");
        }
        validateRole(this.role.name());
        return this.role.name();
    }

    public void setRole(String role) {
        validateRole(role);
        this.role = UserRole.valueOf(role);
    }

    public void validateUserName(String username) {
        if (username == null || username.length() == 0) {
            throw new UserNotFoundException("Not a valid user - username can't be null or empty.");
        }
    }

    public String getUsername() {
        validateUserName(this.username);
        return this.username;
    }

    public void setUsername(String username) {
        validateUserName(username);
        this.username = username;
    }

    public void validateAvatar(String avatar) {
        if (avatar == null || avatar.length() == 0) {
            throw new UserNotFoundException("Not a valid user - avatar can't be null or empty.");
        }
    }

    public String getAvatar() {
        validateAvatar(this.avatar);
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        validateAvatar(avatar);
        this.avatar = avatar;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new UserBoundary(this.email, this.role.name(), this.username, this.avatar);
    }

    @Override
    public String toString() {
        return (
            "UserBoundary [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]"
        );
    }
}
