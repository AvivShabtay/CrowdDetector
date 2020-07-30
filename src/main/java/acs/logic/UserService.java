package acs.logic;

import acs.boundaries.UserBoundary;
import java.util.List;

public interface UserService {
    public UserBoundary createUser(UserBoundary user);

    public UserBoundary login(String userEmail);

    public UserBoundary updateUser(String userEmail, UserBoundary update);

    public List<UserBoundary> getAllUsers(String adminEmail);

    public void deleteAllUsers(String adminEmail);
}
