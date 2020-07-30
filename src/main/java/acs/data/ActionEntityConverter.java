package acs.data;

import acs.boundaries.ActionBoundary;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ActionEntityConverter extends EntityConverterAbstract<ActionEntity, ActionBoundary> {

    @Override
    public ActionBoundary convertEntityToBoundary(ActionEntity entity) {
        ActionEntity actionEntity = (ActionEntity) entity;
        ActionBoundary actionBoundary = new ActionBoundary();

        actionBoundary.setActionId(actionEntity.getActionId());
        actionBoundary.setCreatedTimestamp(actionEntity.getCreatedTimestamp());
        actionBoundary.setType(actionEntity.getType());
        actionBoundary.setElement(actionEntity.getElement());
        actionBoundary.setInvokedBy(actionEntity.getInvokedBy());
        if (!(entity.getActionAttributes() == null)) {
            // unmarshalling
            try {
                actionBoundary.setActionAttributes(
                    this.getJackson().readValue(actionEntity.getActionAttributes(), Map.class)
                );
            } catch (Exception exeption) {
                throw new RuntimeException(exeption);
            }
        }
        return actionBoundary;
    }

    @Override
    public ActionEntity convertBoundaryToEntity(ActionBoundary boundary) {
        ActionBoundary actionBoundary = (ActionBoundary) boundary;
        ActionEntity actionEntity = new ActionEntity();

        actionEntity.setActionId(actionBoundary.getActionId());
        actionEntity.setType(actionBoundary.getType());
        actionEntity.setElement(actionBoundary.getElement());
        actionEntity.setCreatedTimestamp(actionBoundary.getCreatedTimestamp());
        actionEntity.setInvokedBy(actionBoundary.getInvokedBy());
        try {
            actionEntity.setActionAttributes(
                this.getJackson().writeValueAsString(actionBoundary.getActionAttributes())
            );
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return actionEntity;
    }

    public String convertBoundaryAttributesToEntityAttributes(Map<String, Object> BoundaryAttributes) {
        try {
            return this.getJackson().writeValueAsString(BoundaryAttributes);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public Map<String, Object> convertEntityAttributesToBoundaryAttributes(String EntityAttributes) {
        try {
            if (!(EntityAttributes == null)) {
                // unmarshalling
                try {
                    return this.getJackson().readValue(EntityAttributes, Map.class);
                } catch (Exception exeption) {
                    throw new RuntimeException(exeption);
                }
            } else return new HashMap<String, Object>();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
