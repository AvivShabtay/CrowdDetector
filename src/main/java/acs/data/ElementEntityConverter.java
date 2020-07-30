package acs.data;

import acs.boundaries.ElementBoundary;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ElementEntityConverter extends EntityConverterAbstract<ElementEntity, ElementBoundary> {

    @Override
    public ElementBoundary convertEntityToBoundary(ElementEntity entity) {
        ElementEntity elementEntity = (ElementEntity) entity;
        ElementBoundary elementBoundary = new ElementBoundary();

        elementBoundary.setElementId(elementEntity.getElementId());
        elementBoundary.setType(elementEntity.getType());
        elementBoundary.setName(elementEntity.getName());
        elementBoundary.setActive(elementEntity.getActive());
        elementBoundary.setCreatedTimestamp(elementEntity.getCreatedTimestamp());
        elementBoundary.setCreatedBy(elementEntity.getCreatedBy());
        elementBoundary.setLocation(elementEntity.getLocation());
        // unmarshalling
        try {
            elementBoundary.setElementAttributes(
                this.getJackson().readValue(elementEntity.getElementAttributes(), Map.class)
            );
        } catch (Exception exeption) {
            throw new RuntimeException(exeption);
        }
        return elementBoundary;
    }

    @Override
    public ElementEntity convertBoundaryToEntity(ElementBoundary boundary) {
        ElementBoundary elementBoundary = (ElementBoundary) boundary;
        ElementEntity elementEntity = new ElementEntity();

        elementEntity.setElementId(elementBoundary.getElementId());
        elementEntity.setType(elementBoundary.getType());
        elementEntity.setName(elementBoundary.getName());
        elementEntity.setActive(elementBoundary.getActive());
        elementEntity.setCreatedTimestamp(elementBoundary.getCreatedTimestamp());
        elementEntity.setCreatedBy(elementBoundary.getCreatedBy());
        elementEntity.setLocation(elementBoundary.getLocation());
        try {
            elementEntity.setElementAttributes(
                this.getJackson().writeValueAsString(elementBoundary.getElementAttributes())
            );
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return elementEntity;
    }
}
