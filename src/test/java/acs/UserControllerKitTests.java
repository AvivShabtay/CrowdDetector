package acs;

import static org.assertj.core.api.Assertions.assertThat;

import acs.boundaries.UserBoundary;
import acs.data.UserRole;
import java.util.Arrays;
import java.util.List;
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
public class UserControllerKitTests {
    private RestTemplate restTemplate;
    private String baseUrl;
    private String url;
    private int port;
    private String adminEmail;

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @BeforeEach
    public void setup(TestInfo info) {}

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + port + "/acs";
        this.url = "http://localhost:" + port + "/acs/users/";
        this.adminEmail = "TestUserAdmin@tst.com";
    }

    @AfterEach
    public void teardown() {
        this.restTemplate.delete(this.baseUrl + "/admin/users/{adminEmail}", this.getAdmin().getEmail());
    }

    /*
     * Scenario: Create a new first user and validate content.
     * Given - The server is up and the DB is empty of users.
     * When - The user invokes a POST request through the URL: /acs/users/, and passes an
     * UserBoundary in the BODY of the request.
     * Then - The application returns 200 and responds with the corresponding UserBoundary JSON of the request from the DB.
     */
    @Test
    public void testCreateUserWithEmptyDbAndValidate() throws Exception {
        UserBoundary createdUser = this.createUsers(1, 0, UserRole.PLAYER).get(0);
        UserBoundary userFromDb = this.getUsers().get(0);
        assertThat(createdUser).usingRecursiveComparison().isEqualTo(userFromDb);
    }

    /*
     * Scenario: Create a new user and validate content.
     * Given - The server is up and the DB is contain users.
     * When - The user invokes a POST request through the URL: /acs/users/, and passes an
     * UserBoundary in the BODY of the request.
     * Then - The application returns 200 and responds with the corresponding UserBoundary JSON of the request from the DB.
     */
    @Test
    public void testCreateUserWithPopulatedDbAndValidate() throws Exception {
        this.createUsers(3, 0, UserRole.PLAYER);
        UserBoundary createdUser = this.createUsers(1, 3, UserRole.PLAYER).get(0);
        UserBoundary userFromDb =
            this.getUsers()
                .stream()
                .filter(user -> user.getEmail().equals(createdUser.getEmail()))
                .collect(Collectors.toList())
                .get(0);
        assertThat(createdUser).usingRecursiveComparison().isEqualTo(userFromDb);
    }

    /*
     * Scenario: Update existing user and validate content.
     * Given - The server is up and the DB is empty of users.
     * When - The user invokes a PUT request through the URL: /acs/users/{userEmail}, and passes an
     * UserBoundary to update in the BODY of the request.
     * Then - The application returns 200 and responds with the corresponding UserBoundary JSON of the request from the DB after update.
     */
    @Test
    public void testUpdateUserAttrubitesOfNewUserAndValidateEmptyDbIsUpdated() throws Exception {
        UserBoundary createdUser = this.createUsers(1, 0, UserRole.PLAYER).get(0);
        UserBoundary userFromDb = this.getUsers().get(0);

        // Update User
        createdUser.setRole(UserRole.PLAYER.name());
        createdUser.setUsername("Updated user");
        createdUser.setAvatar("Update Avatar");

        this.updateUser(createdUser);

        userFromDb = this.getUser(createdUser.getEmail());

        assertThat(createdUser).isEqualToComparingOnlyGivenFields(userFromDb, "Role", "Username", "Avatar");
    }

    /*
     * Scenario: Update existing user and validate content.
     * Given - The server is up and the DB contain users.
     * When - The user invokes a PUT request through the URL: /acs/users/{userEmail}, and passes an
     * UserBoundary to update in the BODY of the request.
     * Then - The application returns 200 and responds with the corresponding UserBoundary JSON of the request from the DB after update.
     */
    @Test
    public void testUpdateUserAttributesOfNewUserAndValidatePopulatedDbIsUpdated() throws Exception {
        this.createUsers(3, 0, UserRole.PLAYER);

        UserBoundary createdUser = this.createUsers(1, 3, UserRole.PLAYER).get(0);
        UserBoundary userFromDb = this.getUser(createdUser.getEmail());

        createdUser.setRole(UserRole.PLAYER.name());
        createdUser.setUsername("Updated user");
        createdUser.setAvatar("Update Avatar");

        this.updateUser(createdUser);
        userFromDb = this.getUser(createdUser.getEmail());

        assertThat(createdUser).isEqualToComparingOnlyGivenFields(userFromDb, "Role", "Username", "Avatar");
    }

    /*
     * Scenario: Login the only existing user.
     * Given - The server is up and the DB contain one users.
     * When - The user invokes a GET request through the URL: /acs/users/login/{userEmail}.
     * Then - The application returns 200 and responds with the corresponding UserBoundary JSON of the request from the DB.
     */
    @Test
    public void testLoginWithNewUserWhenDbIsEmpty() throws Exception {
        UserBoundary createdUser = this.createUsers(1, 0, UserRole.PLAYER).get(0);
        UserBoundary userFromDb = this.getUser(createdUser.getEmail());

        assertThat(createdUser).isEqualToComparingOnlyGivenFields(userFromDb, "email", "role", "username", "avatar");
    }

    /*
     * Scenario: Login with existing user.
     * Given - The server is up and the DB contain users.
     * When - The user invokes a GET request through the URL: /acs/users/login/{userEmail}.
     * Then - The application returns 200 and responds with the corresponding UserBoundary JSON of the request from the DB.
     */
    @Test
    public void testLoginWithNewUserWhenDbPopulated() throws Exception {
        this.createUsers(3, 0, UserRole.PLAYER);
        UserBoundary createdUser = this.createUsers(1, 3, UserRole.PLAYER).get(0);
        UserBoundary userFromDb = this.getUser(createdUser.getEmail());

        assertThat(createdUser).isEqualToComparingOnlyGivenFields(userFromDb, "email", "role", "username", "avatar");
    }

    public UserBoundary getAdmin() {
        try {
            return this.restTemplate.getForObject(this.url + "login/{userEmail}", UserBoundary.class, this.adminEmail);
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
                    .map(user -> this.restTemplate.postForObject(this.url, user, UserBoundary.class))
                    .collect(Collectors.toList())
                    .get(0);
            }
            return null;
        }
    }

    public List<UserBoundary> createUsers(int numberOfUsers, int startFrom, UserRole role) {
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
            .map(user -> this.restTemplate.postForObject(this.url, user, UserBoundary.class))
            .collect(Collectors.toList());
    }

    public UserBoundary getUser(String email) {
        return this.restTemplate.getForObject(this.url + "login/{userEmail}", UserBoundary.class, email);
    }

    public void updateUser(UserBoundary user) {
        this.restTemplate.put(this.url + "{userEmail}", user, user.getEmail());
    }

    public List<UserBoundary> getUsers() {
        UserBoundary adminUser = this.getAdmin();
        return Arrays
            .asList(
                this.restTemplate.getForObject(
                        this.baseUrl + "/admin/users/{adminEmail}",
                        UserBoundary[].class,
                        adminUser.getEmail()
                    )
            )
            .stream()
            .filter(user -> !user.getEmail().equals(adminUser.getEmail()))
            .collect(Collectors.toList());
    }
}
