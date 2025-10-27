package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;

@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authService;

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldFindCurrentUser() {
        User user = this.userService.findCurrentUser();
        assertEquals("player1", user.getUsername());
    }

    @Test
    @WithMockUser(username = "prueba")
    void shouldNotFindCorrectCurrentUser() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }


    @Test
    void shouldFindUsersByUsername() {
        User user = this.userService.findUser("raul");
        assertEquals("raul", user.getUsername());
    }

    @Test
    void shouldFindUsersByAuthority() {
        List<User> owners = (List<User>) this.userService.findAllByAuthority("PLAYER");
        assertEquals(49, owners.size());

        List<User> admins = (List<User>) this.userService.findAllByAuthority("ADMIN");
        assertEquals(1, admins.size());

        List<User> users = (List<User>) this.userService.findAll();
        assertEquals(50, users.size());
    }

    @Test
    void shouldNotFindUserByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser("tudorX"));
    }

    @Test
    void shouldFindSingleUser() {
        User user = this.userService.findUser(4);
        assertEquals("javier", user.getUsername());
    }

    @Test
    void shouldNotFindSingleUserWithBadID() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser(100));
    }

    @Test
    void shouldExistUser() {
        assertEquals(true, this.userService.existsUser("aaron"));
    }

    @Test
    void shouldNotExistUser() {
        assertEquals(false, this.userService.existsUser("XX_alberto__XX"));
    }

    @Test
    @Transactional
    void shouldUpdateUser() {
        int idToUpdate = 1;
        String newName = "Change";
        User user = this.userService.findUser(idToUpdate);
        user.setUsername(newName);
        userService.updateUser(user, idToUpdate);
        user = this.userService.findUser(idToUpdate);
        assertEquals(newName, user.getUsername());
    }

    @Test
    @Transactional
    void shouldInsertUser() {
        int count = ((Collection<User>) this.userService.findAll()).size();

        User user = new User();
        user.setUsername("Sam");
        user.setPassword("password");
        user.setFirstName("Samuel");
        user.setLastName("Samuel");
        user.setOnline(false);
        user.setAuthority(authService.findByAuthority("ADMIN"));

        this.userService.saveUser(user);
        assertNotEquals(0, user.getId().longValue());
        assertNotNull(user.getId());

        int finalCount = ((Collection<User>) this.userService.findAll()).size();
        assertEquals(count + 1, finalCount);
    }

    @Test
    @Transactional
    void shouldDeleteUser() {
        int idToDelete = 2;
        userService.deleteUser(idToDelete);
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser(idToDelete));
    }



    @Test
    @Transactional
    void shouldMakeUserOnline() {
        String username = "player1";
        userService.makeOffline(username); 
        assertEquals(false, userService.findUser(username).getOnline());

        userService.makeOnline(username);
        assertEquals(true, userService.findUser(username).getOnline());
    }

    @Test
    @Transactional
    void shouldMakeUserOffline() {
        String username = "player1";
        userService.makeOnline(username);
        assertEquals(true, userService.findUser(username).getOnline());

        userService.makeOffline(username);
        assertEquals(false, userService.findUser(username).getOnline());
    }

    @Test
    void shouldNotSaveUserWithMissingFields() {
        User user = new User();
        user.setUsername("IncompleteUser");

        assertThrows(Exception.class, () -> this.userService.saveUser(user));
    }

    @Test
    void shouldThrowExceptionWhenSavingUserWithDuplicateUsername() {
        User user2 = new User();
        user2.setUsername("player1");
        user2.setPassword("anotherPassword");
        user2.setFirstName("usuario");
        user2.setLastName("2");
        user2.setOnline(false);
        user2.setAuthority(authService.findByAuthority("ADMIN"));

        assertThrows(DataAccessException.class, () -> userService.saveUser(user2));
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        User user = new User();
        user.setUsername("nonExistent");
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(user, -1));
    }
    @Test
    @Transactional
    void testUserServiceExceptionsAndValidations() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userService.findUser((Integer) null),
            "Expected InvalidDataAccessApiUsageException when finding user with null ID");
    
        assertThrows(ResourceNotFoundException.class, () -> userService.findUser(""),
            "Expected ResourceNotFoundException when finding user with empty username");
    
        assertEquals(false, userService.existsUser(null),
            "Expected false when checking existence of user with null username");
    
        assertThrows(ResourceNotFoundException.class, () -> this.userService.deleteUser(9999),
            "Expected ResourceNotFoundException when deleting a non-existent user");
    }

    @Test
    void shouldReturnEmptyListForNonExistentAuthority() {
        List<User> users = (List<User>) userService.findAllByAuthority("NON_EXISTENT_AUTHORITY");
        assertEquals(0, users.size());
    }

    @Test
    @Transactional
    void shouldAddAchievementToUser() {
        User user = userService.findUser("player1");
        int initialAchievementCount = user.getObtainedAchievements().size();

        Achievement achievement = new Achievement();
        achievement.setName("First Victory");

        user.getObtainedAchievements().add(achievement);
        userService.updateUser(user, user.getId());

        User updatedUser = userService.findUser("player1");
        assertEquals(initialAchievementCount + 1, updatedUser.getObtainedAchievements().size());
    }
}
