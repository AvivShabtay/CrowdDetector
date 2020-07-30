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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEnvironmentAction implements InvokeActionService {
    private ElementServiceExtended elementService;
    private ActionEntityConverter actionEntityConverter;
    private InvokeActionService userLocationAction;

    @Autowired
    public UserEnvironmentAction(
        UserServiceExtended userService,
        ElementServiceExtended elementService,
        InvokeActionService userLocationAction
    ) {
        this.elementService = elementService;
        this.userLocationAction = userLocationAction;
    }

    @Autowired
    public void setActionEntityConverter(ActionEntityConverter actionEntityConverter) {
        this.actionEntityConverter = actionEntityConverter;
    }

    @Override
    public ActionEntity invokeAction(ActionBoundary action) {
        this.elementService.inactiveByTypeAndEmail(action.getInvokedBy().getEmail(), action.getType(), null, null);

        ActionBoundary userLocation;
        try {
            userLocation = (ActionBoundary) action.clone();
            userLocation.setType(USER_LOCATION);
            this.userLocationAction.invokeAction(userLocation);
        } catch (CloneNotSupportedException e1) {
            throw new ActionInternalException("Fail to clone action!");
        }

        Double radius;
        if (action.getActionAttributes().containsKey(RADIUS)) {
            radius = new Double(action.getActionAttributes().get(RADIUS).toString());
        } else {
            radius = (double) 0;
        }

        ElementBoundary element = this.createActionElement(action);
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(RADIUS, radius);
        actionAttributes.put(
            USERS,
            this.elementService.getAllByTypeAndRadius(
                    action.getInvokedBy(),
                    USER_LOCATION,
                    element.getLocation(),
                    radius,
                    null,
                    null
                )
                .stream()
                .map(
                    item -> {
                        Map<String, Object> attributes = item.getElementAttributes();
                        attributes.put(
                            DISTANCE,
                            UserEnvironmentAction.calcDistance(element.getLocation(), item.getLocation())
                        );
                        item.setElementAttributes(attributes);
                        return item;
                    }
                )
                .collect(Collectors.toList())
        );
        element.setElementAttributes(actionAttributes);

        ElementBoundary actionElement = elementService.create(element, action.getInvokedBy());

        if (action.getActionAttributes().containsKey(LONGITUDE) && action.getActionAttributes().containsKey(LATITUDE)) {
            actionAttributes.put(LATITUDE, action.getActionAttributes().get(LATITUDE));
            actionAttributes.put(LONGITUDE, action.getActionAttributes().get(LONGITUDE));
        } else {
            throw new ActionInternalException("Illegal actionAttributes!");
        }

        return createGeneralAction(
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

        Location actionLocation = new Location(
            new Double(action.getActionAttributes().get(LATITUDE).toString()),
            new Double(action.getActionAttributes().get(LONGITUDE).toString())
        );
        element.setLocation(actionLocation);

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

    public static double calcDistance(Location current, Location other) {
        if ((current.getLat() == other.getLat()) && (current.getLng() == other.getLng())) {
            return 0;
        } else {
            double theta = current.getLng() - other.getLng();
            double dist =
                Math.sin(Math.toRadians(current.getLat())) *
                Math.sin(Math.toRadians(other.getLat())) +
                Math.cos(Math.toRadians(current.getLat())) *
                Math.cos(Math.toRadians(other.getLat())) *
                Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1609.344;
            return (dist);
        }
    }

    @Override
    public String getType() {
        return USER_ENVIRONMENT;
    }
}
