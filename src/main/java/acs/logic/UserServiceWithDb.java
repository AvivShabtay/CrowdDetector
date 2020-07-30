package acs.logic;

import acs.boundaries.UserBoundary;
import acs.dal.UserDao;
import acs.data.UserEntity;
import acs.data.UserEntityConverter;
import acs.data.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A mockup which represents a database layer for the User in memory.
 */
@Service
public class UserServiceWithDb implements UserServiceExtended {
    private UserDao userDao;
    private UserEntityConverter userEntityConverter;

    @Autowired
    public UserServiceWithDb(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setEntityConverter(UserEntityConverter userEntityConverter) {
        this.userEntityConverter = userEntityConverter;
    }

    public boolean isValidEmail(String userEmail) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return userEmail.matches(regex);
    }

    @Override
    @Transactional(readOnly = false)
    public UserBoundary createUser(UserBoundary user) {
        UserEntity userEntity = (UserEntity) this.userEntityConverter.convertBoundaryToEntity(user);
        if (userEntity.getEmail() == null) {
            throw new UserNotFoundException("Email can't be empty!");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new UserNotFoundException("Invalid user Email!");
        }

        Optional<UserEntity> existUser = this.userDao.findById(userEntity.getEmail());

        if (!existUser.isPresent()) {
            userEntity = this.userDao.save(userEntity);
            return this.userEntityConverter.convertEntityToBoundary(userEntity);
        } else {
            throw new UserGeneralException("Email: " + userEntity.getEmail() + " already exists! Use other Email.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userEmail) {
        Optional<UserEntity> user = this.userDao.findById(userEmail);

        if (user.isPresent()) {
            return this.userEntityConverter.convertEntityToBoundary(user.get());
        } else {
            throw new UserNotFoundException("Couldn't find user for Email: " + userEmail);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary identifyUserRoleByEmail(String userEmail, List<UserRole> role) {
        Optional<UserEntity> user = this.userDao.findById(userEmail);

        if (user.isPresent()) {
            UserBoundary userBoundary = this.userEntityConverter.convertEntityToBoundary(user.get());
            Optional<String> roles = role
                .stream()
                .map(currentRole -> currentRole.name())
                .filter(userBoundary.getRole()::equals)
                .findAny();

            if (!roles.isPresent()) {
                throw new UserNotFoundException(
                    "The Email " +
                    userBoundary.getEmail() +
                    " doesn't have " +
                    Arrays.toString(role.toArray()) +
                    " permissions!"
                );
            }
            return userBoundary;
        }
        throw new UserNotFoundException("Couldn't find user for Email: " + userEmail);
    }

    @Override
    @Transactional(readOnly = false)
    public UserBoundary updateUser(String userEmail, UserBoundary update) {
        UserBoundary user = login(userEmail);

        if (update.getRole() != null) {
            user.setRole(update.getRole());
        }

        if (update.getAvatar() != null) {
            user.setAvatar(update.getAvatar());
        }

        if (update.getUsername() != null) {
            user.setUsername(update.getUsername());
        }

        this.userDao.save((UserEntity) this.userEntityConverter.convertBoundaryToEntity(user));

        return user;
    }

    public void assertUserRole(UserBoundary user, UserRole role) {
        if (!user.getRole().equals(role.name())) throw new UserNotFoundException(
            "The Email " + user.getEmail() + " doesn't have " + role.name() + " permissions!"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminEmail) {
        this.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));

        List<UserBoundary> users = new ArrayList<>();

        Iterable<UserEntity> allusers = this.userDao.findAll();
        for (UserEntity curUser : allusers) {
            users.add(this.userEntityConverter.convertEntityToBoundary(curUser));
        }
        return users;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllUsers(String adminEmail) {
        this.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));
        this.userDao.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> generateUsers(int numberOfUsers) {
        for (int i = 0; i < numberOfUsers; i++) createUser(
            new UserBoundary(
                "TestUserPlayer" + i + "@tst.com",
                "PLAYER",
                "Player" + i + " User",
                "Player" + i + " Avatar"
            )
        );

        List<UserBoundary> users = new ArrayList<>();

        Iterable<UserEntity> allusers = this.userDao.findAll();
        for (UserEntity user : allusers) {
            users.add(this.userEntityConverter.convertEntityToBoundary(user));
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminEmail, int size, int page) {
        this.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));
        return this.userDao.findAll(PageRequest.of(page, size, Direction.ASC, "email"))
            .getContent()
            .stream()
            .map(this.userEntityConverter::convertEntityToBoundary)
            .collect(Collectors.toList());
    }
}
