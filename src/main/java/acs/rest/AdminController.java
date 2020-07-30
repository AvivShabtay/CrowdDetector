package acs.rest;

import acs.boundaries.ActionBoundary;
import acs.boundaries.UserBoundary;
import acs.logic.ActionServiceExtended;
import acs.logic.ElementServiceExtended;
import acs.logic.UserServiceExtended;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    private UserServiceExtended userService;
    private ActionServiceExtended actionService;
    private ElementServiceExtended elementService;

    public AdminController() {}

    @Autowired
    public AdminController(
        UserServiceExtended userService,
        ActionServiceExtended actionService,
        ElementServiceExtended elementService
    ) {
        this.userService = userService;
        this.actionService = actionService;
        this.elementService = elementService;
    }

    // DELETE request, path="/acs/admin/users/{adminEmail}"
    // Accepts: None
    // Returns: None
    @RequestMapping(path = "/acs/admin/users/{adminEmail}", method = RequestMethod.DELETE)
    public void deleteAllUsers(@PathVariable("adminEmail") String adminEmail) {
        Sentry.getContext().setUser(new UserBuilder().setEmail(adminEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Admin deleteAllUsers").build());
        this.userService.deleteAllUsers(adminEmail);
    }

    // DELETE request, path="/acs/admin/elements/{adminEmail}"
    // Accepts: None
    // Returns: None
    @RequestMapping(path = "/acs/admin/elements/{adminEmail}", method = RequestMethod.DELETE)
    public void deleteAllElements(@PathVariable("adminEmail") String adminEmail) {
        Sentry.getContext().setUser(new UserBuilder().setEmail(adminEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Admin deleteAllElements").build());
        this.elementService.deleteAllElements(adminEmail);
    }

    // DELETE request, path="/acs/admin/actions/{adminEmail}"
    // Accepts: None
    // Returns: None
    @RequestMapping(path = "/acs/admin/actions/{adminEmail}", method = RequestMethod.DELETE)
    public void deleteAllActions(@PathVariable("adminEmail") String adminEmail) {
        Sentry.getContext().setUser(new UserBuilder().setEmail(adminEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Admin deleteAllActions").build());
        this.actionService.deleteAllActions(adminEmail);
    }

    // GET request, path="/acs/admin/users/{adminEmail}"
    // Accepts: None
    // Returns: UserBoundary Array

    @RequestMapping(
        path = "/acs/admin/users/{adminEmail}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<UserBoundary> exportsAllUsers(
        @PathVariable("adminEmail") String adminEmail,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(adminEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Admin exportAllUsers").build());
        return this.userService.getAllUsers(adminEmail, size, page);
    }

    // GET request, path="/acs/admin/actions/{adminEmail}"
    // Accepts: None
    // Returns: ActionsBoundary Array

    @RequestMapping(
        path = "/acs/admin/actions/{adminEmail}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ActionBoundary> exportsAllActions(
        @PathVariable("adminEmail") String adminEmail,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    )
        throws Exception {
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(adminEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Admin exportAllActions").build());
        return this.actionService.getAllActions(adminEmail, size, page);
    }
}
