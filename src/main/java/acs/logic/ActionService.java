package acs.logic;

import acs.boundaries.ActionBoundary;
import java.util.List;

public interface ActionService {
    public Object invokeAction(ActionBoundary action);

    public List<ActionBoundary> getAllActions(String adminEmail);

    public void deleteAllActions(String adminEmail);
}
