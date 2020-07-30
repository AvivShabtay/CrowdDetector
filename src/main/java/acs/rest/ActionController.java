package acs.rest;

import acs.boundaries.ActionBoundary;
import acs.logic.ActionService;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActionController {
    private ActionService actionService;

    @Autowired
    public ActionController(ActionService actionService) {
        super();
        this.actionService = actionService;
    }

    // POST request, path="/acs/actions"
    // Accepts: ActionBoundary (with null actionID)
    // Returns: Any JSON object
    @RequestMapping(
        path = "/acs/actions",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Object invokeAction(@RequestBody ActionBoundary action) {
        Sentry.getContext().addExtra("action", action);
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Invoke action").build());
        return this.actionService.invokeAction(action);
    }

    // GET request, path="/acs/actions/{adminEmail}"
    // Accepts: None
    // Returns: ActionBoundary
    @RequestMapping(
        path = "/acs/actions/{adminEmail}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ActionBoundary[] getAllActions(@PathVariable("adminEmail") String adminEmail) {
        Sentry.getContext().setUser(new UserBuilder().setEmail(adminEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Admin getAllActions").build());
        return this.actionService.getAllActions(adminEmail).toArray(new ActionBoundary[0]);
    }
}
