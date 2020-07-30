package acs.dal;

import acs.data.ActionEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActionDao extends PagingAndSortingRepository<ActionEntity, String> {}
