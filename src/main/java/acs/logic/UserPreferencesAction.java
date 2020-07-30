package acs.logic;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.data.ActionEntity;
import acs.data.ActionEntityConverter;
import acs.data.DoneBy;
import acs.data.Element;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesAction implements InvokeActionService {
    private UserServiceExtended userService;
    private ElementServiceExtended elementService;
    private ActionEntityConverter actionEntityConverter;

    @Autowired
    public UserPreferencesAction(UserServiceExtended userService, ElementServiceExtended elementService) {
        this.userService = userService;
        this.elementService = elementService;
    }

    @Autowired
    public void setActionEntityConverter(ActionEntityConverter actionEntityConverter) {
        this.actionEntityConverter = actionEntityConverter;
    }

    @Override
    public ActionEntity invokeAction(ActionBoundary action) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(USER_PROPERTIES, this.userService.login(action.getInvokedBy().getEmail()));

        ElementBoundary element = this.createActionElement(action);
        actionAttributes.put(PREFERENCES, element.getElementAttributes());
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

        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        if (action.getActionAttributes().containsKey(THEME)) {
            actionAttributes.put(THEME, action.getActionAttributes().get(THEME).toString());
            element.setElementAttributes(actionAttributes);
        } else {
            try {
                ElementBoundary userPreferences =
                    this.elementService.getAllByType(action.getInvokedBy().getEmail(), action.getType(), 0, 1)
                        .stream()
                        .collect(Collectors.toList())
                        .get(0);

                if (userPreferences != null) {
                    Map<String, Object> userPreferencesAttributes = (Map<String, Object>) userPreferences
                        .getElementAttributes()
                        .get(PREFERENCES);
                    this.elementService.inactiveByTypeAndEmail(
                            action.getInvokedBy().getEmail(),
                            action.getType(),
                            null,
                            null
                        );
                    actionAttributes.put(THEME, userPreferencesAttributes.get(THEME));
                } else {
                    throw new ActionInternalException(
                        "No avialible preferences for " + action.getInvokedBy().getEmail()
                    );
                }
                element.setElementAttributes(actionAttributes);
            } catch (Exception e) {
                throw new ActionInternalException("Fail to load preferences for " + action.getInvokedBy().getEmail());
            }
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
        return USER_PREFERENCES;
    }
}
