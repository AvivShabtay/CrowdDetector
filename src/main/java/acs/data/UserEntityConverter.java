package acs.data;

import acs.boundaries.UserBoundary;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter extends EntityConverterAbstract<UserEntity, UserBoundary> {

    @Override
    public UserBoundary convertEntityToBoundary(UserEntity entity) {
        if (entity instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) entity;
            UserBoundary user = new UserBoundary();

            user.setEmail(userEntity.getEmail());
            user.setUsername(userEntity.getUsername());
            user.setAvatar(userEntity.getAvatar());
            user.setRole(userEntity.getRole().name());
            return user;
        }
        return null;
    }

    @Override
    public UserEntity convertBoundaryToEntity(UserBoundary boundary) {
        if (boundary instanceof UserBoundary) {
            UserBoundary userBoundary = (UserBoundary) boundary;
            UserEntity user = new UserEntity();

            user.setEmail(userBoundary.getEmail());
            user.setUsername(userBoundary.getUsername());
            user.setAvatar(userBoundary.getAvatar());
            user.setRole(UserRole.valueOf(userBoundary.getRole()));

            return user;
        }
        return null;
    }
}
