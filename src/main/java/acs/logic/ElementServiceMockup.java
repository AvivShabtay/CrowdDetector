package acs.logic;

import acs.boundaries.ElementBoundary;
import acs.data.DoneBy;
import acs.data.ElementEntity;
import acs.data.ElementEntityConverter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

public class ElementServiceMockup implements ElementService {
    private ElementEntityConverter elementEntityConverter;
    private Map<String, ElementEntity> database;

    @Autowired
    public void setElementEntityConverter(ElementEntityConverter elementEntityConverter) {
        this.elementEntityConverter = elementEntityConverter;
    }

    @PostConstruct
    public void init() {
        this.database = Collections.synchronizedMap(new TreeMap<>());
    }

    @Override
    public ElementBoundary create(String managerEmail, ElementBoundary element) {
        String newId = UUID.randomUUID().toString();
        ElementEntity elementEntity = this.elementEntityConverter.convertBoundaryToEntity(element);

        DoneBy createdBy = new DoneBy(managerEmail);

        elementEntity.setElementId(newId);
        elementEntity.setCreatedBy(createdBy);
        elementEntity.setCreatedTimestamp(new Date());
        this.database.put(newId, elementEntity);

        return this.elementEntityConverter.convertEntityToBoundary(elementEntity);
    }

    @Override
    public void update(String managerEmail, String elementId, ElementBoundary update) {
        ElementBoundary element = getSpecificElement(managerEmail, elementId);
        boolean isDirty = false;

        if (update.getType() != null) {
            element.setType(update.getType());
            isDirty = true;
        }

        if (update.getName() != null) {
            element.setName(update.getName());
            isDirty = true;
        }

        if (update.getActive() != null) {
            element.setActive(update.getActive());
            isDirty = true;
        }

        if (update.getLocation() != null) {
            element.setLocation(update.getLocation());
            isDirty = true;
        }

        if (update.getElementAttributes() != null) {
            element.setElementAttributes(update.getElementAttributes());
            isDirty = true;
        }

        if (isDirty) {
            this.database.put(elementId, this.elementEntityConverter.convertBoundaryToEntity(element));
        }
    }

    @Override
    public List<ElementBoundary> getAll(String userEmail) {
        return this.database.values()
            .stream()
            .map(elementEntity -> this.elementEntityConverter.convertEntityToBoundary(elementEntity))
            .collect(Collectors.toList());
    }

    @Override
    public ElementBoundary getSpecificElement(String userEmail, String elementId) {
        ElementEntity entity = this.database.get(elementId);

        if (entity == null) {
            throw new ElementNotFoundException("Couldn't find Element with ID: " + elementId);
        }
        return this.elementEntityConverter.convertEntityToBoundary(entity);
    }

    @Override
    public void deleteAllElements(String adminEmail) {
        this.database.clear();
    }
}
