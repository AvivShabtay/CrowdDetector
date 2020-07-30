package acs.logic;

import acs.boundaries.UserBoundary;
import acs.data.UserRole;
import java.util.List;

public interface UserServiceExtended extends UserService {
    public List<UserBoundary> generateUsers(int numberOfUsers);

    public UserBoundary identifyUserRoleByEmail(String userEmail, List<UserRole> role);

    public List<UserBoundary> getAllUsers(String adminEmail, int size, int page);
}
