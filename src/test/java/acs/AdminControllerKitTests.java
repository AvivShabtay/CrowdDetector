package acs;

import static org.assertj.core.api.Assertions.assertThat;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.data.DoneBy;
import acs.data.Location;
import acs.data.UserRole;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminControllerKitTests {
    private RestTemplate restTemplate;

    private String baseUrl;
    private String adminUrl;

    private int port;

    private String adminEmail;
    private String managerEmail;

    final String elementsRoute = "elements/";
    final String actionsRoute = "actions/";
    final String usersRoute = "users/";

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();

        this.baseUrl = "http://localhost:" + port + "/acs/";
        this.adminUrl = this.baseUrl + "admin/";

        this.adminEmail = "AutoTestUserAdmin@test.com";
        this.managerEmail = "AutoTestUserManager@test.com";
    }

    @BeforeEach
    public void setup() {}

    @AfterEach
    public void teardown() {
        this.restTemplate.delete(this.adminUrl + this.elementsRoute + "{userEmail}", this.getAdmin().getEmail());
        this.restTemplate.delete(this.adminUrl + this.actionsRoute + "{userEmail}", this.getAdmin().getEmail());
        this.restTemplate.delete(this.adminUrl + this.usersRoute + "{userEmail}", this.getAdmin().getEmail());
    }

    /*
     * Scenario: Requesting from the system to delete all elements.
     * Given - The server is up and the DB contain elements.
     * When - Admin user invokes DELETE request through the URL: /acs/admin/elements/{adminEmail}
     * Then - The application remove all elements in the DB.
     */
    @Test
    public void testDeleteAllElements() throws Exception {
        this.createElements(3, 0, "TestDelete", true);

        String deleteUrl = this.adminUrl + this.elementsRoute + this.getAdmin().getEmail();
        this.restTemplate.delete(deleteUrl);

        assertThat(this.getElements(this.getManager().getEmail(), 0, Integer.MAX_VALUE).size()).isEqualTo(0);
    }

    /*
     * Scenario: Requesting from the system to delete all actions.
     * Given - The server is up and the DB contain actions.
     * When - Admin user invokes DELETE request through the URL: /acs/admin/actions/{adminEmail}
     * Then - The application remove all actions in the DB.
     */
    @Test
    public void testDeleteAllActions() throws Exception {
        List<UserBoundary> players = this.cretaeUsers(3, 3, UserRole.PLAYER);

        ActionBoundary actionToPerform = new ActionBoundary();

        Map<String, Object> actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));

        actionToPerform.setActionAttributes(actionAttributes);
        actionToPerform.setInvokedBy(new DoneBy(players.get(0).getEmail()));
        actionToPerform.setType("userLocation");

        this.restTemplate.postForObject(this.baseUrl + this.actionsRoute, actionToPerform, ActionBoundary.class);

        String deleteUrl = this.adminUrl + this.actionsRoute + this.getAdmin().getEmail();
        this.restTemplate.delete(deleteUrl);

        List<ActionBoundary> actions = Arrays.asList(
            this.restTemplate.getForObject(
                    this.adminUrl + this.actionsRoute + "{adminEmail}",
                    ActionBoundary[].class,
                    this.getAdmin().getEmail()
                )
        );

        assertThat(actions.size()).isEqualTo(0);
    }

    /*
     * Scenario: Requesting from the system to delete all users.
     * Given - The server is up and the DB contain users.
     * When - Admin user invokes DELETE request through the URL: /acs/admin/users/{adminEmail}
     * Then - The application remove all users in the DB.
     */
    @Test
    public void testDeleteAllUsers() throws Exception {
        this.cretaeUsers(7, 0, UserRole.PLAYER);
        String deleteUrl = this.adminUrl + this.usersRoute + this.getAdmin().getEmail();
        this.restTemplate.delete(deleteUrl);

        assertThat(this.getUsers().size()).isEqualTo(0);
    }

    /*
     * Scenario: Requesting from the system to retrieve all users.
     * Given - The server is up and the DB contain users.
     * When - Admin invokes GET request through the URL: /acs/admin/users/{adminEmail}.
     * Then - The application create List of UserBoundary JSON contains all the users in the DB.
     */
    @Test
    public void testExportsAllUsers() {
        List<UserBoundary> users = this.cretaeUsers(7, 0, UserRole.PLAYER);
        List<UserBoundary> usersFromDB = this.getUsers();

        users
            .stream()
            .forEach(
                user ->
                    assertThat(user)
                        .isEqualToComparingFieldByField(
                            usersFromDB
                                .stream()
                                .filter(dbUser -> dbUser.getEmail().equals(user.getEmail()))
                                .collect(Collectors.toList())
                                .get(0)
                        )
            );
    }

    /*
     * Scenario: Requesting from the system to retrieve all actions.
     * Given - The server is up and the DB contain actions.
     * When - Admin invokes GET request through the URL: /acs/admin/actions/{adminEmail}.
     * Then - The application create List of ActionBoundary JSON contains all the actions in the DB.
     */
    @Test
    public void testExportsAllActions() {
        List<ElementBoundary> elements = this.createElements(7, 0, "TetsExport", true);
        List<ElementBoundary> elementsFromDB = this.getElements(this.getManager().getEmail(), 0, Integer.MAX_VALUE);

        elements
            .stream()
            .forEach(
                element ->
                    assertThat(element)
                        .isEqualToComparingFieldByField(
                            elementsFromDB
                                .stream()
                                .filter(dbElement -> dbElement.getElementId().equals(element.getElementId()))
                                .collect(Collectors.toList())
                                .get(0)
                        )
            );
    }

    /*
     * Scenario: When the server is up the DB is empty of users.
     * Given - The server is up.
     * When - The Admin invokes a GET request through the URL: /acs/admin/users/{adminEmail}.
     * Then - The application returns 200 and responds with empty List of UserBoundary JSON of the request from the DB.
     */
    @Test
    public void testGetAllUsersOnServerInitReturnsEmptyArray() throws Exception {
        assertThat(this.getUsers()).isEmpty();
    }

    public UserBoundary getAdmin() {
        try {
            return this.restTemplate.getForObject(
                    this.baseUrl + this.usersRoute + "login/{userEmail}",
                    UserBoundary.class,
                    this.adminEmail
                );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                return IntStream
                    .range(0, 1)
                    .mapToObj(
                        i ->
                            new UserBoundary(
                                this.adminEmail,
                                UserRole.ADMIN.name(),
                                UserRole.ADMIN.name().toLowerCase() + " " + UserRole.ADMIN.name().toLowerCase(),
                                UserRole.ADMIN.name().toLowerCase()
                            )
                    )
                    .map(
                        user ->
                            this.restTemplate.postForObject(this.baseUrl + this.usersRoute, user, UserBoundary.class)
                    )
                    .collect(Collectors.toList())
                    .get(0);
            }
            return null;
        }
    }

    public UserBoundary getManager() {
        try {
            return this.restTemplate.getForObject(
                    this.baseUrl + this.usersRoute + "login/{userEmail}",
                    UserBoundary.class,
                    this.managerEmail
                );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                return IntStream
                    .range(0, 1)
                    .mapToObj(
                        i ->
                            new UserBoundary(
                                this.managerEmail,
                                UserRole.MANAGER.name(),
                                UserRole.MANAGER.name().toLowerCase() + " " + UserRole.MANAGER.name().toLowerCase(),
                                UserRole.MANAGER.name().toLowerCase()
                            )
                    )
                    .map(
                        user ->
                            this.restTemplate.postForObject(this.baseUrl + this.usersRoute, user, UserBoundary.class)
                    )
                    .collect(Collectors.toList())
                    .get(0);
            }
            return null;
        }
    }

    public List<UserBoundary> cretaeUsers(int numberOfUsers, int startFrom, UserRole role) {
        return IntStream
            .range(startFrom, numberOfUsers + startFrom)
            .mapToObj(
                i ->
                    new UserBoundary(
                        role.name().toLowerCase() + i + "TestUser@Test.com",
                        role.name(),
                        role.name().toLowerCase() + i + " " + role.name().toLowerCase() + i,
                        role.name().toLowerCase() + i
                    )
            )
            .map(user -> this.restTemplate.postForObject(this.baseUrl + this.usersRoute, user, UserBoundary.class))
            .collect(Collectors.toList());
    }

    public List<UserBoundary> getUsers() {
        UserBoundary adminUser = this.getAdmin();
        return Arrays
            .asList(
                this.restTemplate.getForObject(
                        this.adminUrl + this.usersRoute + "{adminEmail}",
                        UserBoundary[].class,
                        this.getAdmin().getEmail()
                    )
            )
            .stream()
            .filter(user -> !user.getEmail().equals(adminUser.getEmail()))
            .collect(Collectors.toList());
    }

    public List<ElementBoundary> createElements(int numberOfElements, int startFrom, String type, Boolean active) {
        return IntStream
            .range(startFrom, numberOfElements + startFrom)
            .mapToObj(
                i -> {
                    Map<String, Object> elementAttributes = new HashMap<>();
                    elementAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
                    elementAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));
                    return new ElementBoundary(
                        null,
                        type,
                        "TestElement " + i,
                        active,
                        null,
                        new DoneBy(this.getAdmin().getEmail()),
                        new Location(
                            ThreadLocalRandom.current().nextDouble(29, 32),
                            ThreadLocalRandom.current().nextDouble(29, 32)
                        ),
                        elementAttributes
                    );
                }
            )
            .map(
                element ->
                    this.restTemplate.postForObject(
                            this.baseUrl + this.elementsRoute + "/{managerEmail}",
                            element,
                            ElementBoundary.class,
                            this.getManager().getEmail()
                        )
            )
            .collect(Collectors.toList());
    }

    public List<ElementBoundary> getElements(String email, int page, int size) {
        return Arrays
            .asList(
                this.restTemplate.getForObject(
                        this.baseUrl + this.elementsRoute + "/" + email + "?page={page}&size={size}",
                        ElementBoundary[].class,
                        page,
                        size
                    )
            )
            .stream()
            .collect(Collectors.toList());
    }
}
