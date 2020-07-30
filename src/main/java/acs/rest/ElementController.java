package acs.rest;

import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
import acs.logic.ElementServiceExtended;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import java.util.Collection;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElementController {
    private ElementServiceExtended elementService;

    public ElementController(ElementServiceExtended elementService) {
        this.elementService = elementService;
    }

    // GET request, path="/acs/elements/{userEmail}/{id}"
    // Accepts: None
    // Returns: ElementBoundary
    @RequestMapping(
        path = "/acs/elements/{userEmail}/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary getElementById(@PathVariable("userEmail") String userEmail, @PathVariable("id") String id) {
        Sentry.getContext().addExtra("id", id);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("GetElementById").build());
        return this.elementService.getSpecificElement(userEmail, id);
    }

    // GET request, path="/acs/elements/{userEmail}"
    // Accepts: None
    // Returns: Array of ElementBoundary
    @RequestMapping(
        path = "/acs/elements/{userEmail}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary[] getAllElements(
        @PathVariable("userEmail") String userEmail,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("GetAllElements").build());
        Collection<ElementBoundary> elementsList = this.elementService.getAll(userEmail, page, size);
        ElementBoundary[] elements = new ElementBoundary[elementsList.size()];
        elementsList.toArray(elements);
        return elements;
    }

    // POST request, path="/acs/elements/{managerEmail}"
    // Accepts: ElementBoundary (with no ID)
    // Returns: ElementBoundary (with ID)
    @RequestMapping(
        path = "/acs/elements/{managerEmail}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary createElement(
        @PathVariable("managerEmail") String managerEmail,
        @RequestBody ElementBoundary element
    ) {
        Sentry.getContext().addExtra("element", element);
        Sentry.getContext().setUser(new UserBuilder().setEmail(managerEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("CreateElement").build());
        return this.elementService.create(managerEmail, element);
    }

    // PUT request, path="/acs/elements/{managerEmail}/{id}"
    // Accepts: ElementBoundary
    // Returns: None
    @RequestMapping(
        path = "/acs/elements/{managerEmail}/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void updateElement(
        @PathVariable("managerEmail") String managerEmail,
        @PathVariable("id") String id,
        @RequestBody ElementBoundary element
    ) {
        Sentry.getContext().addExtra("element", element);
        Sentry.getContext().addExtra("id", id);
        Sentry.getContext().setUser(new UserBuilder().setEmail(managerEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("UpdateElement").build());
        this.elementService.update(managerEmail, id, element);
    }

    // PUT request, path="/acs/elements/{managerEmail}/{parentElementId}/children"
    // Accepts: ElementBoundary
    // Returns: None
    @RequestMapping(
        path = "/acs/elements/{managerEmail}/{parentElementId}/children",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void addChildToElement(
        @PathVariable("managerEmail") String managerEmail,
        @PathVariable("parentElementId") String parentElementId,
        @RequestBody ElementIdBoundary childElementId
    ) {
        Sentry.getContext().addExtra("parentElementId", parentElementId);
        Sentry.getContext().addExtra("childElementId", childElementId);
        Sentry.getContext().setUser(new UserBuilder().setEmail(managerEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("AddChildToElement").build());
        this.elementService.addChildToElement(managerEmail, parentElementId, childElementId.getId());
    }

    // GET request, path="/acs/elements/{userEmail}/{parentElementId}/children"
    // Accepts: None
    // Returns: Array of ElementBoundary
    @RequestMapping(
        path = "/acs/elements/{userEmail}/{parentElementId}/children",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary[] getAllElementChildren(
        @PathVariable("userEmail") String userEmail,
        @PathVariable("parentElementId") String parentElementId,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        Sentry.getContext().addExtra("parentElementId", parentElementId);
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("GetAllElementChildren").build());
        return this.elementService.getAllElementChildren(userEmail, parentElementId, page, size)
            .toArray(new ElementBoundary[0]);
    }

    // GET request, path="/acs/elements/{userEmail}/{childElementId}/parents"
    // Accepts: None
    // Returns: Array of ElementBoundary
    @RequestMapping(
        path = "/acs/elements/{userEmail}/{childElementId}/parents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary[] getAllElementParents(
        @PathVariable("userEmail") String userEmail,
        @PathVariable("childElementId") String childElementId,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        Sentry.getContext().addExtra("childElementId", childElementId);
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("GetAllElementParents").build());
        return this.elementService.getAllElementParents(userEmail, childElementId, page, size)
            .toArray(new ElementBoundary[0]);
    }

    // GET request, path="/acs/elements/{userEmail}/search/byName/{name}"
    // Accepts: None
    // Returns: Array of ElementBoundary
    @RequestMapping(
        path = "/acs/elements/{userEmail}/search/byName/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary[] searchElementsByName(
        @PathVariable("userEmail") String userEmail,
        @PathVariable("name") String name,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        Sentry.getContext().addExtra("name", name);
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("SearchElementsByName").build());
        Set<ElementBoundary> elements = this.elementService.getAllByName(userEmail, name, page, size);
        return elements.toArray(new ElementBoundary[0]);
    }

    // GET request, path="/acs/elements/{userEmail}/search/byType/{type}"
    // Accepts: None
    // Returns: Array of ElementBoundary
    @RequestMapping(
        path = "/acs/elements/{userEmail}/search/byType/{type}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ElementBoundary[] searchElementsByType(
        @PathVariable("userEmail") String userEmail,
        @PathVariable("type") String type,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        Sentry.getContext().addExtra("type", type);
        Sentry.getContext().addExtra("page", page);
        Sentry.getContext().addExtra("size", size);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("SearchElementsByType").build());
        return this.elementService.getAllByType(userEmail, type, page, size).toArray(new ElementBoundary[0]);
    }
}
