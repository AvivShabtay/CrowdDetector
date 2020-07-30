package acs.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvokeActionFactory {
    @Autowired
    private List<InvokeActionService> invokeActionService;

    private static final Map<String, InvokeActionService> invokeActionServiceCache = new HashMap<>();

    @PostConstruct
    public void initActionServiceCache() {
        for (InvokeActionService service : invokeActionService) {
            invokeActionServiceCache.put(service.getType(), service);
        }
    }

    public static InvokeActionService getService(String type) {
        InvokeActionService service = invokeActionServiceCache.get(type);
        if (service == null) new ActionNotFoundException("The action " + type + " not found!");
        return service;
    }
}
