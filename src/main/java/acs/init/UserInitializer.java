package acs.init;

import acs.boundaries.UserBoundary;
import acs.logic.UserServiceExtended;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("userManualTests")
public class UserInitializer implements CommandLineRunner {
    private UserServiceExtended userService;

    public UserInitializer() {}

    @Autowired
    public void setUserService(UserServiceExtended userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        this.userService.createUser(new UserBoundary("TestUserAdmin@tst.com", "ADMIN", "Admin User", "Admin Avatar"));
        this.userService.createUser(
                new UserBoundary("TestUserManager@tst.com", "MANAGER", "Manager User", "Manager Avatar")
            );
    }
}
