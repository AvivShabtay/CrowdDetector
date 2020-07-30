package acs.logic;

import acs.boundaries.ElementBoundary;
import acs.data.DoneBy;
import acs.data.Location;
import java.util.Set;

public interface ElementServiceExtended extends ElementService {
    public void addChildToElement(String managerEmail, String parentElementId, String childElementId);

    public Set<ElementBoundary> getAllElementChildren(String userEmail, String parentElementId, int page, int size);

    public Set<ElementBoundary> getAllElementParents(String userEmail, String childElementId, int page, int size);

    public Set<ElementBoundary> getAll(String userEmail, int page, int size);

    public Set<ElementBoundary> getAllByName(String userEmail, String name, int page, int size);

    public Set<ElementBoundary> getAllByType(String userEmail, String type, int page, int size);

    public ElementBoundary create(ElementBoundary element, DoneBy createdBy);

    public void update(String elementId, ElementBoundary update);

    public void inactiveByTypeAndEmail(String userEmail, String type, Object page, Object size);

    public Set<ElementBoundary> getAllByTypeAndEmail(String email, String type, int page, int size);

    public Set<ElementBoundary> getAllByTypeAndRadius(
        DoneBy doneBy,
        String type,
        Location location,
        Double radius,
        Object page,
        Object size
    );
}
