package acs.dal;

import acs.data.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDao extends PagingAndSortingRepository<UserEntity, String> {}
