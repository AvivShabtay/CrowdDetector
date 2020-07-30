package acs.logic;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.data.ActionEntity;
import acs.data.ActionEntityConverter;
import acs.data.DoneBy;
import acs.data.Element;
import acs.data.Location;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLocationAction implements InvokeActionService {
    private UserServiceExtended userService;
    private ElementServiceExtended elementService;
    private ActionEntityConverter actionEntityConverter;

    @Autowired
    public UserLocationAction(UserServiceExtended userService, ElementServiceExtended elementService) {
        this.userService = userService;
        this.elementService = elementService;
    }

    @Autowired
    public void setActionEntityConverter(ActionEntityConverter actionEntityConverter) {
        this.actionEntityConverter = actionEntityConverter;
    }

    @Override
    public ActionEntity invokeAction(ActionBoundary action) {
        this.elementService.inactiveByTypeAndEmail(action.getInvokedBy().getEmail(), action.getType(), null, null);

        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(USER_PROPERTIES, this.userService.login(action.getInvokedBy().getEmail()));

        ElementBoundary element = this.createActionElement(action);
        element.setElementAttributes(actionAttributes);

        ElementBoundary actionElement = elementService.create(element, action.getInvokedBy());

        return this.createGeneralAction(
                new Element(actionElement.getElementId()),
                action.getInvokedBy(),
                action.getType(),
                actionAttributes
            );
    }

    private ElementBoundary createActionElement(ActionBoundary action) {
        ElementBoundary element = new ElementBoundary();
        element.setType(action.getType());
        element.setActive(true);
        element.setName(action.getType());

        if (action.getActionAttributes().containsKey(LONGITUDE) && action.getActionAttributes().containsKey(LATITUDE)) {
            Location actionLocation = new Location(
                new Double(action.getActionAttributes().get(LATITUDE).toString()),
                new Double(action.getActionAttributes().get(LONGITUDE).toString())
            );
            element.setLocation(actionLocation);
        } else {
            throw new ActionInternalException("Illegal actionAttributes!");
        }

        return element;
    }

    private ActionEntity createGeneralAction(
        Element element,
        DoneBy doneBy,
        String type,
        Map<String, Object> actionAttributes
    ) {
        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setActionId(UUID.randomUUID().toString());
        actionEntity.setType(type);
        actionEntity.setInvokedBy(doneBy);
        actionEntity.setCreatedTimestamp(new Date());
        actionEntity.setElement(element);
        actionEntity.setActionAttributes(
            this.actionEntityConverter.convertBoundaryAttributesToEntityAttributes(actionAttributes)
        );
        return actionEntity;
    }

    @Override
    public String getType() {
        return USER_LOCATION;
    }
}
