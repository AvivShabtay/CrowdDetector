package acs.logic;

import acs.boundaries.ActionBoundary;
import acs.data.ActionEntity;
import acs.data.ActionEntityConverter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

public class ActionServiceMockup implements ActionService {
    private ActionEntityConverter actionEntityConverter;
    private Map<String, ActionEntity> database;

    @Autowired
    public void setActionEntityConverter(ActionEntityConverter actionEntityConverter) {
        this.actionEntityConverter = actionEntityConverter;
    }

    @PostConstruct
    public void init() {
        this.database = Collections.synchronizedMap(new TreeMap<>());
    }

    @Override
    public Object invokeAction(ActionBoundary action) {
        String newId = UUID.randomUUID().toString();
        ActionEntity actionEntity = (ActionEntity) this.actionEntityConverter.convertBoundaryToEntity(action);
        actionEntity.setActionId(newId);
        this.database.put(newId, actionEntity);
        return this.actionEntityConverter.convertEntityToBoundary(actionEntity);
    }

    @Override
    public List<ActionBoundary> getAllActions(String adminEmail) {
        return this.database.values()
            .stream()
            .map(actionEntity -> (ActionBoundary) this.actionEntityConverter.convertEntityToBoundary(actionEntity))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteAllActions(String adminEmail) {
        this.database.clear();
    }
}
