package acs.logic;

import acs.boundaries.UserBoundary;
import acs.data.UserEntity;
import acs.data.UserEntityConverter;
import acs.data.UserRole;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A mockup which represents a database layer for the User in memory.
 */
public class UserServiceMockup implements UserService {
    private Map<String, UserEntity> database;
    private UserEntityConverter userEntityConverter;
    private AtomicLong nextId;

    private final String EMAIL_TEMPLATE = "@gmail.com";

    public UserServiceMockup() {}

    @Autowired
    public void setEntityConverter(UserEntityConverter userEntityConverter) {
        this.userEntityConverter = userEntityConverter;
    }

    @PostConstruct
    public void init() {
        this.database = Collections.synchronizedMap(new TreeMap<>());
        this.nextId = new AtomicLong(0L);
        //        generateUsers(4);
    }

    @Override
    public UserBoundary createUser(UserBoundary user) {
        Long newId = nextId.incrementAndGet();
        UserEntity userEntity = (UserEntity) this.userEntityConverter.convertBoundaryToEntity(user);

        if (userEntity.getEmail() == null) {
            userEntity.setEmail(newId + EMAIL_TEMPLATE);
        }

        this.database.put(userEntity.getEmail(), userEntity);

        return this.userEntityConverter.convertEntityToBoundary(userEntity);
    }

    @Override
    public UserBoundary login(String userEmail) {
        UserEntity user = this.database.get(userEmail);

        if (user != null) {
            return (UserBoundary) this.userEntityConverter.convertEntityToBoundary(user);
        } else {
            throw new UserNotFoundException("Couldn't find user for Email: " + userEmail);
        }
    }

    @Override
    public UserBoundary updateUser(String userEmail, UserBoundary update) {
        UserBoundary user = login(userEmail);
        boolean isDirty = false;

        if (update.getRole() != null) {
            user.setRole(update.getRole());
            isDirty = true;
        }

        if (update.getAvatar() != null) {
            user.setAvatar(update.getAvatar());
            isDirty = true;
        }

        if (update.getUsername() != null) {
            user.setUsername(update.getUsername());
            isDirty = true;
        }

        if (isDirty) {
            this.database.put(userEmail, (UserEntity) this.userEntityConverter.convertBoundaryToEntity(user));
        }

        return user;
    }

    public UserBoundary isAdminUser(String adminEmail) {
        UserBoundary user = login(adminEmail);

        if (!user.getRole().equals(UserRole.ADMIN.name())) throw new UserNotFoundException(
            "The Email " + adminEmail + " doesn't have admin permissions!"
        ); else return user;
    }

    @Override
    public List<UserBoundary> getAllUsers(String adminEmail) {
        isAdminUser(adminEmail);

        return this.database.values()
            .stream()
            .map(userEntity -> (UserBoundary) this.userEntityConverter.convertEntityToBoundary(userEntity))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUsers(String adminEmail) {
        isAdminUser(adminEmail);
        this.database.clear();
    }

    public List<UserBoundary> generateUsers(int numberOfUsers) {
        createUser(new UserBoundary("TestUserAdmin@tst.com", "Administrator", "Admin User", "Admin Avatar"));
        createUser(new UserBoundary("TestUserManager@tst.com", "Manager", "Manager User", "Manager Avatar"));

        for (int i = 0; i < numberOfUsers; i++) createUser(
            new UserBoundary(
                "TestUserPlayer" + i + "@tst.com",
                "Player",
                "Player" + i + " User",
                "Player" + i + " Avatar"
            )
        );

        return this.database.values()
            .stream()
            .map(userEntity -> (UserBoundary) this.userEntityConverter.convertEntityToBoundary(userEntity))
            .collect(Collectors.toList());
    }
}
