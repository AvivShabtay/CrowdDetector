package acs.logic;

import acs.boundaries.ElementBoundary;
import java.util.List;

public interface ElementService {
    public ElementBoundary create(String managerEmail, ElementBoundary element);

    public void update(String managerEmail, String elementId, ElementBoundary update);

    public List<ElementBoundary> getAll(String userEmail);

    public ElementBoundary getSpecificElement(String userEmail, String elementId);

    public void deleteAllElements(String adminEmail);
}
