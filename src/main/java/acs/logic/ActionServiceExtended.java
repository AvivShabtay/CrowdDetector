package acs.logic;

import acs.boundaries.ActionBoundary;
import java.util.List;

public interface ActionServiceExtended extends ActionService {
    public List<ActionBoundary> getAllActions(String adminEmail, int size, int page);
}
