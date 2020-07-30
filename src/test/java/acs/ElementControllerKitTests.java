package acs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
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
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementControllerKitTests {
    private RestTemplate restTemplate;

    private String baseUrl;
    private String elementUrl;
    private int port;

    private String adminEmail;
    private String userEmail;
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
        this.elementUrl = "http://localhost:" + port + "/acs/elements";

        this.adminEmail = "TestUserAdmin@test.com";
        this.userEmail = "TestUser@test.com";
        this.managerEmail = "TestUserManager@test.com";
    }

    @BeforeEach
    public void setup() {}

    @AfterEach
    public void teardown() {
        this.restTemplate.delete(
                this.baseUrl + "admin/" + this.elementsRoute + "{userEmail}",
                this.getAdmin().getEmail()
            );
        this.restTemplate.delete(
                this.baseUrl + "admin/" + this.actionsRoute + "{userEmail}",
                this.getAdmin().getEmail()
            );
        this.restTemplate.delete(this.baseUrl + "admin/" + this.usersRoute + "{userEmail}", this.getAdmin().getEmail());
    }

    /*
     * Scenario: Create a new element with correct data.
     * Given - The server is up and the DB contain a manager user.
     * When - The manager invokes a POST request through the URL: /acs/elements/{managerEmail}, and passes an
     * ElementBoundary in the BODY of the request.
     * Then - The application returns 200 and responds with the element data with a generated ID for this element.
     */
    @Test
    public void testCreateElementWithCorrectData() throws Exception {
        ElementBoundary newElement = this.createElements(1, 0, "TestElement", true).get(0);

        ElementBoundary elementFromDB = this.getElement(newElement.getElementId());
        assertThat(newElement)
            .isEqualToComparingOnlyGivenFields(
                elementFromDB,
                "type",
                "name",
                "active",
                "location",
                "elementAttributes"
            );
        assertThat(elementFromDB).hasNoNullFieldsOrProperties();
        assertThat(elementFromDB.getCreatedBy().getEmail()).isEqualTo(this.managerEmail);
    }

    /*
     * Scenario: Update an existing element.
     * Given - The server is up and the DB contain a manager user.
     * When - The manager invokes a PUT request through the URL: /acs/elements/{managerEmail}/{elementID}, and
     * passes ElementBoundary in the BODY of the request.
     * Then - The application updates the element data. Returns 200 with no response BODY.
     */
    @Test
    public void testUpdateExistingElement() throws Exception {
        String updateValue = "Alon Bukai Test";

        ElementBoundary createdElement = this.createElements(1, 0, "TestUpdate", true).get(0);
        createdElement.setName(updateValue);

        this.updateElement(createdElement.getElementId(), createdElement);

        ElementBoundary updatedElement = this.getElement(createdElement.getElementId());

        assertThat(updatedElement)
            .isEqualToComparingOnlyGivenFields(
                createdElement,
                "type",
                "elementId",
                "active",
                "location",
                "createdTimestamp",
                "createdBy",
                "elementAttributes"
            );
        assertThat(updatedElement).hasNoNullFieldsOrProperties();
        assertThat(updatedElement.getName()).isEqualTo(updateValue);
    }

    /*
     * Scenario: Update a nonexistent element.
     * Given - The server is up and the DB contain a manager user.
     * When - The manager invokes a PUT request through the URL: /acs/elements/{managerEmail}/{elementID} (nonexistent element
     * ID), and passes ElementBoundary in the BODY of the request.
     * Then - The application doesn't update the element data. Returns error status 404 with an
     * error message "Couldn't find Element with ID: {elementID}".
     */
    @Test
    public void testUpdateNonExistentElement() throws Exception {
        ElementBoundary createdElement = this.createElements(1, 0, "UpdateNonExists", true).get(0); // get random created elements

        Exception exception = assertThrows(
            NotFound.class,
            () -> {
                this.updateElement("123", createdElement);
            }
        );

        assertTrue(
            "Throws an error when updating nonexistent Element",
            exception.getMessage().contains("Couldn't find Element with ID: 123")
        );
    }

    /*
     * Scenario: Get an element with existing id.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL: /acs/elements/{userEmail}/{elementID}.
     * Then - The application returns 200 and responds with the element data.
     */
    @Test
    public void testGetExistingElement() throws Exception {
        ElementBoundary createdElement = this.createElements(1, 0, "TestGetElement", true).get(0);

        ElementBoundary fetchedElement = this.getElement(createdElement.getElementId());

        assertThat(fetchedElement).isEqualToComparingFieldByField(createdElement);
    }

    /*
     * Scenario: Get an element with nonexistent id.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL: /acs/elements/{userEmail}/{elementID}.
     * Then - The application doesn't fetch the element data. Returns error status 404 with an error
     * message "Couldn't find Element with ID: {elementID}".
     */
    @Test
    public void testGetNonExistentElement() throws Exception {
        Exception exception = assertThrows(
            NotFound.class,
            () -> {
                this.getElement("123");
            }
        );

        assertTrue(
            "Throws an error when fetching nonexistent Element",
            exception.getMessage().contains("Couldn't find Element with ID:")
        );
    }

    /*
     * Scenario: Get all children for specific element.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL: /acs/elements/{userEmail}/{parentElementId}/children.
     * Then - The application returns 200 and responds with the all the children
     * elements.
     */
    @Test
    public void testSetAndGetChildren() throws Exception {
        List<ElementBoundary> elements = this.createElements(3, 0, "TestElements", true);

        List<ElementBoundary> childrensFromDB =
            this.setChildrensElement(
                    elements.get(0),
                    elements
                        .stream()
                        .filter(element -> !element.getElementId().equals(elements.get(0).getElementId()))
                        .collect(Collectors.toList()),
                    null,
                    null
                );

        childrensFromDB
            .stream()
            .forEach(
                child -> {
                    assertThat(child)
                        .isEqualToComparingOnlyGivenFields(
                            elements
                                .stream()
                                .filter(element -> element.getElementId().equals(child.getElementId()))
                                .collect(Collectors.toList())
                                .get(0),
                            "type",
                            "name",
                            "active",
                            "location",
                            "elementAttributes"
                        );
                    assertThat(child).hasNoNullFieldsOrProperties();
                }
            );

        assertThat(childrensFromDB.size()).isEqualTo(2);
    }

    /*
     * Scenario: Get all parents for specific element.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL:
     * /acs/elements/{userEmail}/{parentElementId}/parents.
     * Then - The application returns 200 and responds with the all the parent elements.
     */
    @Test
    public void testSetAndGetParents() throws Exception {
        List<ElementBoundary> elements = this.createElements(3, 0, "TestElements", true);

        this.setChildrensElement(
                elements.get(0),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(2).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );
        this.setChildrensElement(
                elements.get(1),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(2).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );

        List<ElementBoundary> parentsFromDB = this.setParentsElement(elements.get(2), null, null, null);

        parentsFromDB
            .stream()
            .forEach(
                parent -> {
                    assertThat(parent)
                        .isEqualToComparingOnlyGivenFields(
                            elements
                                .stream()
                                .filter(element -> element.getElementId().equals(parent.getElementId()))
                                .collect(Collectors.toList())
                                .get(0),
                            "type",
                            "name",
                            "active",
                            "location",
                            "elementAttributes"
                        );
                    assertThat(parent).hasNoNullFieldsOrProperties();
                }
            );

        assertThat(parentsFromDB.size()).isEqualTo(2);
    }

    /*
     * Scenario: Get paginated parents for specific element.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL:
     * /acs/elements/{userEmail}/{parentElementId}/parents?page={page}&size={size}.
     * Then - The application returns 200 and responds with the the paginated parent elements.
     */
    @Test
    public void testGetParentsPaginated() throws Exception {
        List<ElementBoundary> elements = this.createElements(4, 0, "TestElements", true);

        this.setChildrensElement(
                elements.get(0),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(3).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );
        this.setChildrensElement(
                elements.get(1),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(3).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );
        this.setChildrensElement(
                elements.get(2),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(3).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );

        List<ElementBoundary> parentsFromDB = this.setParentsElement(elements.get(3), null, 0, 2);

        parentsFromDB
            .stream()
            .forEach(
                parent -> {
                    assertThat(parent)
                        .isEqualToComparingOnlyGivenFields(
                            elements
                                .stream()
                                .filter(element -> element.getElementId().equals(parent.getElementId()))
                                .collect(Collectors.toList())
                                .get(0),
                            "type",
                            "name",
                            "active",
                            "location",
                            "elementAttributes"
                        );
                    assertThat(parent).hasNoNullFieldsOrProperties();
                }
            );
        assertThat(parentsFromDB.size()).isEqualTo(2);
    }

    /*
     * Scenario: Get paginated children for specific element.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL:
     * /acs/elements/user@example.com/{childrenElementId}/children?page={page}&size={size}.
     * Then - The application returns 200 and responds with the the paginated child elements.
     */
    @Test
    public void testGetChildrenPaginated() throws Exception {
        List<ElementBoundary> elements = this.createElements(4, 0, "TestElements", true);

        this.setChildrensElement(
                elements.get(0),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(1).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );
        this.setChildrensElement(
                elements.get(0),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(2).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );
        this.setChildrensElement(
                elements.get(0),
                elements
                    .stream()
                    .filter(element -> element.getElementId().equals(elements.get(3).getElementId()))
                    .collect(Collectors.toList()),
                null,
                null
            );

        List<ElementBoundary> parentsFromDB = this.setChildrensElement(elements.get(0), null, 0, 2);

        parentsFromDB
            .stream()
            .forEach(
                parent -> {
                    assertThat(parent)
                        .isEqualToComparingOnlyGivenFields(
                            elements
                                .stream()
                                .filter(element -> element.getElementId().equals(parent.getElementId()))
                                .collect(Collectors.toList())
                                .get(0),
                            "type",
                            "name",
                            "active",
                            "location",
                            "elementAttributes"
                        );
                    assertThat(parent).hasNoNullFieldsOrProperties();
                }
            );
        assertThat(parentsFromDB.size()).isEqualTo(2);
    }

    /*
     * Scenario: Search all elements with existing user email by name.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL: /acs/elements/user@example.com/search/byName/{name}?page={page}&size={size}.
     * Then - The application returns 200 and responds with the all the elements that match the name.
     */
    @Test
    public void testSearchAllElementsByName() throws Exception {
        List<ElementBoundary> elements = this.createElements(7, 0, "TestSearch", true);
        elements.get(0).setName(elements.get(1).getName());
        elements.get(0).setCreatedBy(elements.get(1).getCreatedBy());
        this.updateElement(elements.get(0).getElementId(), elements.get(0));

        String idUrl =
            this.elementUrl +
            "/" +
            elements.get(0).getCreatedBy().getEmail() +
            "/search/byName/" +
            elements.get(0).getName() +
            "?page=0&size=1";

        ElementBoundary[] fetchedElements = this.restTemplate.getForObject(idUrl, ElementBoundary[].class);

        assertThat(fetchedElements.length).isEqualTo(1);
    }

    /*
     * Scenario: Search all elements with existing user email by type.
     * Given - The server is up and the DB contain a user.
     * When - The user invokes a GET request through the URL: /acs/elements/user@example.com/search/byType/{type}?page={page}&size={size}.
     * Then - The application returns 200 and responds with the all the elements that match the type.
     */
    //@Test
    public void testSearchAllElementsByType() throws Exception {
        List<ElementBoundary> elements = this.createElements(7, 0, "TestSearch", true);
        elements.get(1).setType(elements.get(0).getType());
        this.updateElement(elements.get(1).getElementId(), elements.get(1));

        String idUrl =
            this.elementUrl +
            "/" +
            this.getUser().getEmail() +
            "/search/byType/" +
            elements.get(0).getType() +
            "?page=0&size=2";

        ElementBoundary[] fetchedElements = this.restTemplate.getForObject(idUrl, ElementBoundary[].class);

        assertThat(fetchedElements[0]).hasNoNullFieldsOrProperties();
        assertThat(fetchedElements.length).isEqualTo(2);
        assertThat(fetchedElements[1].getType()).isEqualTo(elements.get(0).getType());
    }

    public UserBoundary getUser() {
        try {
            return this.restTemplate.getForObject(
                    this.baseUrl + this.usersRoute + "login/{userEmail}",
                    UserBoundary.class,
                    this.userEmail
                );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                return IntStream
                    .range(0, 1)
                    .mapToObj(
                        i ->
                            new UserBoundary(
                                this.userEmail,
                                UserRole.PLAYER.name(),
                                UserRole.PLAYER.name().toLowerCase() + " " + UserRole.PLAYER.name().toLowerCase(),
                                UserRole.PLAYER.name().toLowerCase()
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

    public UserBoundary getUser(String email) {
        return this.restTemplate.getForObject(
                this.baseUrl + this.usersRoute + "login/{userEmail}",
                UserBoundary.class,
                email
            );
    }

    public void updateUser(UserBoundary user) {
        this.restTemplate.put(this.baseUrl + this.usersRoute + "{userEmail}", user, user.getEmail());
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
                            this.elementUrl + "/{managerEmail}",
                            element,
                            ElementBoundary.class,
                            this.getManager().getEmail()
                        )
            )
            .collect(Collectors.toList());
    }

    public ElementBoundary getElement(String elementId) {
        return this.restTemplate.getForObject(
                this.elementUrl + "/{userEmail}/{Id}",
                ElementBoundary.class,
                this.getManager().getEmail(),
                elementId
            );
    }

    public void updateElement(String elementId, ElementBoundary element) {
        this.restTemplate.put(
                this.elementUrl + "/{managerEmail}/{id}",
                element,
                this.getManager().getEmail(),
                elementId
            );
    }

    public List<ElementBoundary> setChildrensElement(
        ElementBoundary parent,
        List<ElementBoundary> childrens,
        Object page,
        Object size
    ) {
        String url =
            (
                (page == null || size == null)
                    ? this.elementUrl + "/" + this.getManager().getEmail() + "/" + parent.getElementId() + "/children"
                    : this.elementUrl +
                    "/" +
                    this.getManager().getEmail() +
                    "/" +
                    parent.getElementId() +
                    "/children?page=" +
                    Integer.parseInt(page.toString()) +
                    "&size=" +
                    Integer.parseInt(size.toString())
            );

        if (childrens != null) {
            childrens
                .stream()
                .forEach(element -> this.restTemplate.put(url, new ElementIdBoundary(element.getElementId())));
        }
        return Arrays
            .asList(this.restTemplate.getForObject(url, ElementBoundary[].class))
            .stream()
            .collect(Collectors.toList());
    }

    public List<ElementBoundary> setParentsElement(
        ElementBoundary child,
        List<ElementBoundary> parents,
        Object page,
        Object size
    ) {
        String url =
            (
                (page == null || size == null)
                    ? this.elementUrl + "/" + this.getManager().getEmail() + "/" + child.getElementId() + "/parents"
                    : this.elementUrl +
                    "/" +
                    this.getManager().getEmail() +
                    "/" +
                    child.getElementId() +
                    "/parents?page=" +
                    Integer.parseInt(page.toString()) +
                    "&size=" +
                    Integer.parseInt(size.toString())
            );

        if (parents != null) {
            parents
                .stream()
                .forEach(element -> this.restTemplate.put(url, new ElementIdBoundary(element.getElementId())));
        }
        return Arrays
            .asList(this.restTemplate.getForObject(url, ElementBoundary[].class))
            .stream()
            .collect(Collectors.toList());
    }
}
