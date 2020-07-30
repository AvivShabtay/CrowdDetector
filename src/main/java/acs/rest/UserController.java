package acs.rest;

import acs.boundaries.UserBoundary;
import acs.logic.UserServiceExtended;
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
public class UserController {
    private UserServiceExtended userService;

    @Autowired
    public UserController(UserServiceExtended userService) {
        super();
        this.userService = userService;
    }

    /*
     * Create new user using the given UserBoundary.
     *
     * @Purpose POST request, path="/acs/users", Accepts: UserBoundary, Returns:
     * UserBoundary.
     *
     * @param UserBoundary for a new user.
     *
     * @throws When user fail to create. throw custom UserNotFoundException.
     */
    @RequestMapping(path = "/acs/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody UserBoundary user) {
        Sentry.getContext().addExtra("user", user);
        Sentry.getContext().setUser(new UserBuilder().setEmail(user.getEmail()).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("CreateUser").build());
        return this.userService.createUser(user);
    }

    /*
     * @Purpose GET request, path="/acs/users/login/{userEmail}", Accepts: None,
     * Returns: UserBoundary.
     *
     * @param String represent userEmail.
     *
     * @throws When user not exists throw custom UserNotFoundException.
     */
    @RequestMapping(
        path = "/acs/users/login/{userEmail}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserBoundary loginUser(@PathVariable("userEmail") String userEmail) {
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("LoginUser").build());
        return this.userService.login(userEmail);
    }

    /*
     * @Purpose PUT request, path="/acs/users/{userEmail}", Accepts: UserBoundary,
     * Returns: None.
     *
     * @param userEmail String represent userEmail.
     *
     * @param update UserBoundary represent the new user properties.
     *
     * @throws When user not exists throw custom UserNotFoundException.
     */
    @RequestMapping(
        path = "/acs/users/{userEmail}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void updateUser(@PathVariable("userEmail") String userEmail, @RequestBody UserBoundary user) {
        Sentry.getContext().addExtra("user", user);
        Sentry.getContext().setUser(new UserBuilder().setEmail(userEmail).build());
        Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("UpdateUser").build());
        this.userService.updateUser(userEmail, user);
    }
}
