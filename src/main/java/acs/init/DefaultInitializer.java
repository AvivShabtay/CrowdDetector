package acs.init;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.data.DoneBy;
import acs.data.Location;
import acs.logic.ActionServiceExtended;
import acs.logic.ElementServiceExtended;
import acs.logic.UserServiceExtended;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("DefaultManualTests")
public class DefaultInitializer implements CommandLineRunner {
    private ActionServiceExtended actionService;
    private UserServiceExtended userService;
    private ElementServiceExtended elementService;

    public DefaultInitializer() {}

    @Autowired
    public void setUserService(
        ActionServiceExtended actionService,
        UserServiceExtended userService,
        ElementServiceExtended elementService
    ) {
        this.actionService = actionService;
        this.userService = userService;
        this.elementService = elementService;
    }

    @Override
    public void run(String... args) throws Exception {
        this.userService.createUser(new UserBoundary("TestUserAdmin@tst.com", "ADMIN", "Admin User", "Admin Avatar"));
        this.userService.createUser(
                new UserBoundary("TestUserManager@tst.com", "MANAGER", "Manager User", "Manager Avatar")
            );
        this.userService.createUser(
                new UserBoundary("TestUserPlayer@tst.com", "PLAYER", "Player User", "Player Avatar")
            );
        this.userService.createUser(
                new UserBoundary("TestUserPlayer2@tst.com", "PLAYER", "Player2 User", "Player2 Avatar")
            );
        this.userService.createUser(
                new UserBoundary("TestUserPlayer3@tst.com", "PLAYER", "Player3 User", "Player3 Avatar")
            );

        for (int i = 0; i <= 7; i++) {
            ElementBoundary newElement = new ElementBoundary();
            newElement.setName("element #" + i);
            if (i % 2 == 0) {
                newElement.setElementId("" + i);
                newElement.setActive(true);
                newElement.setType("ElementTest");
            } else {
                newElement.setActive(false);
                newElement.setType("ElementTest" + i);
            }
            newElement.setLocation(
                new Location(
                    ThreadLocalRandom.current().nextDouble(29, 32),
                    ThreadLocalRandom.current().nextDouble(29, 32)
                )
            );
            newElement = this.elementService.create(newElement, new DoneBy("TestUserPlayer@tst.com"));
        }

        Map<String, Object> actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));
        this.actionService.invokeAction(
                new ActionBoundary("userLocation", new DoneBy("TestUserPlayer@tst.com"), actionAttributes)
            );

        actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("radius", ThreadLocalRandom.current().nextDouble(100, 500));
        this.actionService.invokeAction(
                new ActionBoundary("userLocation", new DoneBy("TestUserPlayer2@tst.com"), actionAttributes)
            );

        actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("radius", ThreadLocalRandom.current().nextDouble(100, 500));
        this.actionService.invokeAction(
                new ActionBoundary("userEnvironment", new DoneBy("TestUserPlayer3@tst.com"), actionAttributes)
            );

        actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("radius", ThreadLocalRandom.current().nextDouble(100, 500));
        this.actionService.invokeAction(
                new ActionBoundary("userEnvironment", new DoneBy("TestUserPlayer@tst.com"), actionAttributes)
            );

        actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("radius", ThreadLocalRandom.current().nextDouble(100, 500));
        this.actionService.invokeAction(
                new ActionBoundary("userEnvironment", new DoneBy("TestUserPlayer2@tst.com"), actionAttributes)
            );
    }
}
