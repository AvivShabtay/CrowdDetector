package acs.logic;

import acs.boundaries.ActionBoundary;
import acs.dal.ActionDao;
import acs.data.ActionEntity;
import acs.data.ActionEntityConverter;
import acs.data.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionServiceWithDb implements ActionServiceExtended {
    private ActionDao actionDao;
    private UserServiceExtended userService;
    private ActionEntityConverter actionEntityConverter;

    @Autowired
    public ActionServiceWithDb(ActionDao actionDao, UserServiceExtended userService) {
        this.actionDao = actionDao;
        this.userService = userService;
    }

    @Autowired
    public void setActionEntityConverter(ActionEntityConverter actionEntityConverter) {
        this.actionEntityConverter = actionEntityConverter;
    }

    @Override
    @Transactional(readOnly = false)
    public Object invokeAction(ActionBoundary action) {
        this.userService.identifyUserRoleByEmail(action.getInvokedBy().getEmail(), Arrays.asList(UserRole.PLAYER));

        ActionEntity actionEntity = (ActionEntity) this.actionEntityConverter.convertBoundaryToEntity(action);
        actionEntity = InvokeActionFactory.getService(action.getType()).invokeAction(action);
        actionEntity = this.actionDao.save(actionEntity);
        return this.actionEntityConverter.convertEntityToBoundary(actionEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionBoundary> getAllActions(String adminEmail) {
        this.userService.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));

        Iterable<ActionEntity> allActionEntities = this.actionDao.findAll();
        List<ActionBoundary> allActionBoundaries = new ArrayList<>();

        // Converts all the actions in the application:
        for (ActionEntity actionEntity : allActionEntities) {
            allActionBoundaries.add(this.actionEntityConverter.convertEntityToBoundary(actionEntity));
        }

        return allActionBoundaries;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllActions(String adminEmail) {
        this.userService.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));

        this.actionDao.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionBoundary> getAllActions(String adminEmail, int size, int page) {
        this.userService.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));

        return this.actionDao.findAll(PageRequest.of(page, size, Direction.ASC, "createdTimestamp", "actionId"))
            .getContent()
            .stream()
            .map(this.actionEntityConverter::convertEntityToBoundary)
            .collect(Collectors.toList());
    }
}
