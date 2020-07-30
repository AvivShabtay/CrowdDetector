package acs;

import static org.assertj.core.api.Assertions.assertThat;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.data.DoneBy;
import acs.data.Location;
import acs.data.UserRole;
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
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActionControllerKitTests {
    private RestTemplate restTemplate;
    private int port;

    private String actionUrl;
    private String userUrl;
    private String adminUrl;
    private String elementsUrl;

    private String adminEmail;
    private String managerEmail;

    Map<String, Object> actionAttributes;

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @BeforeEach
    public void setup(TestInfo info) {}

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();

        this.userUrl = "http://localhost:" + port + "/acs/users/";
        this.elementsUrl = "http://localhost:" + port + "/acs/elements/";
        this.adminEmail = "TestUserAdmin@tst.com";
        this.managerEmail = "TestUserManager@tst.com";
        this.actionUrl = "http://localhost:" + port + "/acs/actions";
        this.adminUrl = "http://localhost:" + port + "/acs/admin/";
    }

    @AfterEach
    public void teardown() {
        this.restTemplate.delete(this.adminUrl + "elements/{adminEmail}", this.getAdmin().getEmail());
        this.restTemplate.delete(this.adminUrl + "actions/{adminEmail}", this.getAdmin().getEmail());
        this.restTemplate.delete(this.adminUrl + "users/{adminEmail}", this.getAdmin().getEmail());
    }

    /*
     * Scenario: Requesting from the system to perform an action.
     * Given - The server is up and the DB contain users.
     * When - User invokes POST request through the URL: /acs/actions with
     * ActionBoundary JSON with type of "userLocation" and actionAttributes - lat,lng of his location and invokedBy.
     * Then - The application create corresponding "userLocation" element and action.
     */
    @Test
    public void testInvokeUserLocation() throws Exception {
        List<UserBoundary> players = this.cretaeUsers(3, 0, UserRole.PLAYER);

        ActionBoundary actionToPerform = new ActionBoundary();
        Map<String, Object> actionAttributes = new HashMap<>();
        actionAttributes.put("lat", ThreadLocalRandom.current().nextDouble(29, 32));
        actionAttributes.put("lng", ThreadLocalRandom.current().nextDouble(29, 32));

        actionToPerform.setActionAttributes(actionAttributes);
        actionToPerform.setInvokedBy(new DoneBy(players.get(0).getEmail()));
        actionToPerform.setType("userLocation");

        ActionBoundary actionInvoked =
            this.restTemplate.postForObject(this.actionUrl, actionToPerform, ActionBoundary.class);

        ElementBoundary actionElement = this.getElement(actionInvoked.getElement().getElementId());

        assertThat(actionElement).hasNoNullFieldsOrProperties();
        assertThat(actionInvoked).hasNoNullFieldsOrProperties();
    }

    public UserBoundary getManager() {
        try {
            return this.restTemplate.getForObject(
                    this.userUrl + "login/{userEmail}",
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
                    .map(user -> this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class))
                    .collect(Collectors.toList())
                    .get(0);
            }
            return null;
        }
    }

    public UserBoundary getAdmin() {
        try {
            return this.restTemplate.getForObject(
                    this.userUrl + "login/{userEmail}",
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
                    .map(user -> this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class))
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
            .map(user -> this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class))
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
                        "userLocation " + i,
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
                            this.elementsUrl + "/{managerEmail}",
                            element,
                            ElementBoundary.class,
                            this.getManager().getEmail()
                        )
            )
            .collect(Collectors.toList());
    }

    public ElementBoundary getElement(String elementId) {
        return this.restTemplate.getForObject(
                this.elementsUrl + "/{userEmail}/{Id}",
                ElementBoundary.class,
                this.getManager().getEmail(),
                elementId
            );
    }
}
