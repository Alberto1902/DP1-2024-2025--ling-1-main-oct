/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Genre;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.GenreRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Platform;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.PlatformRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Saga;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.SagaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.user.payload.UserProfileDto;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.JwtResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
class UserRestController {

	private final UserService userService;
	private final AchievementService achievementService;
	private final AuthoritiesService authService;
	private final GameSessionService gameSessionService;
	private final JwtUtils jwtUtils;

	@Autowired
	public UserRestController(UserService userService, AuthoritiesService authService,
			AchievementService achievementService, JwtUtils jwtUtils, GameSessionService gameSessionService) {
		this.userService = userService;
		this.authService = authService;
		this.jwtUtils = jwtUtils;
		this.achievementService = achievementService;
		this.gameSessionService = gameSessionService;
	}

	@Autowired
    private GenreRepository genreRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private SagaRepository sagaRepository;

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(genreRepository.findAll());
    }

	@GetMapping("/platforms")
    public ResponseEntity<List<Platform>> getAllPlatforms() {
        return ResponseEntity.ok(platformRepository.findAll());
    }

	@GetMapping("/sagas")
    public ResponseEntity<List<Saga>> getAllSagas() {
        return ResponseEntity.ok(sagaRepository.findAll());
    }

	@Operation(summary = "Get all users", description = "Returns a list of all users. It shall be available for ADMIN users")
	@Parameter(name = "auth", description = "Filter users by authority")
	@GetMapping
	public ResponseEntity<List<User>> findAll(@RequestParam(required = false) String auth) {
		List<User> res;
		if (auth != null) {
			res = (List<User>) userService.findAllByAuthority(auth);
		} else
			res = (List<User>) userService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Operation(summary = "Get all users", description = "Returns a list of all users. It shall be available for ADMIN users")
	@Parameter(name = "page", description = "Page number")
	@Parameter(name = "size", description = "Number of elements per page")
	@GetMapping("/pages")
	public ResponseEntity<Page<User>> findAllPages(@RequestParam int page, @RequestParam int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<User> res = userService.findAllPages(pageable);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}


	@Operation(summary = "Get all authorities", description = "Returns a list of all authorities. It shall be available for ADMIN users")
	@GetMapping("authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@Operation(summary = "Get a user by its username", description = "Returns a user given its username. It shall be available for users with the role of ADMIN or the user itself")
	@GetMapping(value = "{username}")
	@PreAuthorize("#username == authentication.name or hasAuthority('ADMIN')")
	public ResponseEntity<User> findByUsername(@PathVariable("username") String id) {
		return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
	}

	@Operation(summary = "Create a new user", description = "Creates a new user. It is only available for users with the role of ADMIN")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User object to be published", required = true)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		User savedUser = userService.saveUser(user);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

	@Operation(summary = "Modify a user", description = "Modifies a user given its id. It is only available for users with the role of ADMIN or the user itself")
	@Parameter(name = "userId", description = "User id")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User object to be modified", required = true)
	@PutMapping(value = "{userId}")
	@PreAuthorize("#username == authentication.name or hasAuthority('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<JwtResponse> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		UserDetailsImpl userDetails = UserDetailsImpl.build(user);
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		String token = jwtUtils.generateTokenFromUsername(user.getUsername(), userDetails);

		this.userService.updateUser(user, id);

		return ResponseEntity.ok().body(new JwtResponse(token, userDetails.getId(), userDetails.getUsername(), roles));
	}

	@Operation(summary = "Delete a user", description = "Deletes a user given its id. It is only available for users with the role of ADMIN or the user itself")
	@Parameter(name = "userId", description = "User id to be deleted")
	@DeleteMapping(value = "{userId}")
	@PreAuthorize("#username == authentication.name or hasAuthority('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		if (userService.findCurrentUser().getId() != id) {
			userService.deleteUser(id);
			return new ResponseEntity<>(new MessageResponse("User deleted!"), HttpStatus.OK);
		} else
			throw new AccessDeniedException("You can't delete yourself!");

	}

	@Operation(summary = "Claim an achievement", description = "Allows the user to claim an achievement. It shall be available for players")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Achievement object to be claimed", required = true)
	@PutMapping("claimAchievement")
	@PreAuthorize("#username == authentication.name")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> claimAchievement(@RequestBody @Valid Achievement a) {
		User currentUser = userService.findCurrentUser();
		List<Achievement> obtainedAchievements = currentUser.getObtainedAchievements();
		Integer currentWins = gameSessionService.findTotalWinsByUser(currentUser.getId());
		Integer currentMatches = gameSessionService.findTotalGamesByUser(currentUser.getId());
		Integer timePlayed = gameSessionService.findMinutesPlayedByUser(currentUser.getId());
		Achievement existingAchievement = achievementService.getById(a.getId());
		if (!obtainedAchievements.contains(existingAchievement)) {
			if (a.getMetric() == Metric.GAMES_PLAYED) {
				if (currentMatches >= existingAchievement.getThreshold()) {
					currentUser.getObtainedAchievements().add(existingAchievement);
					userService.saveUser(currentUser);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else if (a.getMetric() == Metric.VICTORIES) {
				if (currentWins >= existingAchievement.getThreshold()) {
					currentUser.getObtainedAchievements().add(existingAchievement);
					userService.saveUser(currentUser);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else if (a.getMetric() == Metric.DEFEATS) {
				if (currentMatches - currentWins >= existingAchievement.getThreshold()) {
					currentUser.getObtainedAchievements().add(existingAchievement);
					userService.saveUser(currentUser);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			else if (a.getMetric() == Metric.TIME_PLAYED){
				if (timePlayed >= existingAchievement.getThreshold()) {
					currentUser.getObtainedAchievements().add(existingAchievement);
					userService.saveUser(currentUser);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			User updatedUser = userService.findCurrentUser();
			return new ResponseEntity<>(updatedUser, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@Operation(summary = "Make a user online", description = "Make a user online It shall be available for all users")
	@Parameter(name = "username", description = "Username of the user whose wants to be online")
	@PutMapping("/makeOnline")
	@PreAuthorize("#username == authentication.name")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> makeOnline(@RequestParam(value = "username") String username) {
		return new ResponseEntity<>(userService.makeOnline(username), HttpStatus.OK);
	}

	@Operation(summary = "Make a user offline", description = "Make a user offline It shall be available for all users")
	@Parameter(name = "username", description = "Username of the user whose wants to be offline")
	@PutMapping("/makeOffline")
	@PreAuthorize("#username == authentication.name")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> makeOffline(@RequestParam(value = "username") String username) {
		return new ResponseEntity<>(userService.makeOffline(username), HttpStatus.OK);
	}

 @Operation(summary = "Get the list of all players alongside some feats ordered by wins", description = "Get the list of all players alongside some feats ordered by wins. It shall be available for users of all roles")
 @Parameter(name = "page", description = "Page number")
 @Parameter(name = "size", description = "Number of elements per page")
 @GetMapping("/rankingByWins")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Object[]>> rankingByWins(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.findAllByWins(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the list of all players alongside some feats ordered by games played", description = "Get the list of all players alongside some feats ordered by games played. It shall be available for users of all roles")
	@Parameter(name = "page", description = "Page number")
	@Parameter(name = "size", description = "Number of elements per page")
	@GetMapping("/rankingByGamesPlayed")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Object[]>> rankingByGamesPlayed(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.findAllByGamesPlayed(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the list of all players alongside some feats ordered by win ratio", description = "Get the list of all players alongside some feats ordered by win ratio. It shall be available for users of all roles")
	@Parameter(name = "page", description = "Page number")
	@Parameter(name = "size", description = "Number of elements per page")
    @GetMapping("/rankingByWinRatio")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Object[]>> rankingByWinRatio(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.findAllByWinRatio(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the list of all friends alongside some feats ordered by games played", description = "Get the list of all friends alongside some feats ordered by games played. It shall be available for users of all roles")
    @Parameter(name = "id", description = "Id of the user whose friends we want to retrieve")
	@Parameter(name = "page", description = "Page number")
	@Parameter(name = "size", description = "Number of elements per page")
    @GetMapping("/rankingByGamesPlayedFriends")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Object[]>> rankingByGamesPlayedFriends(@RequestParam(value = "id") Integer id, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.findAllByGamesPlayedFriends(id, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the list of all friends alongside some feats ordered by wins", description = "Get the list of all friends alongside some feats ordered by wins. It shall be available for users of all roles")
    @Parameter(name = "id", description = "Id of the user whose friends we want to retrieve")
	@Parameter(name = "page", description = "Page number")
	@Parameter(name = "size", description = "Number of elements per page")
    @GetMapping("/rankingByWinsFriends")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Object[]>> rankingByWinsFriends(@RequestParam(value = "id") Integer id, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.findAllByWinsFriends(id, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the list of all friends alongside some feats ordered by win ratio", description = "Get the list of all friends alongside some feats ordered by win ratio. It shall be available for users of all roles")
    @Parameter(name = "id", description = "Id of the user whose friends we want to retrieve")
	@Parameter(name = "page", description = "Page number")
	@Parameter(name = "size", description = "Number of elements per page")
    @GetMapping("/rankingByWinRatioFriends")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Object[]>> rankingByWinRatioFriends(@RequestParam(value = "id") Integer id, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.findAllByWinRatioFriends(id, pageable), HttpStatus.OK);
    }

	@Operation(summary = "Count all existing players", description = "Returns the number of all existing players. It shall be available for users of all roles")
	@GetMapping("/countPlayers")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Integer> countPlayers() {
		return new ResponseEntity<Integer>(userService.countPlayers(), HttpStatus.OK);
	}

	@Operation(summary = "Count all online players", description = "Returns the number of all online players. It shall be available for users of all roles")
	@GetMapping("/countOnlinePlayers")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Integer> countOnlinePlayers() {
		return new ResponseEntity<Integer>(userService.countOnlinePlayers(), HttpStatus.OK);
	}

	@GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Integer userId) {
        UserProfileDto profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

	@PutMapping("/{userId}/profile")
	@PreAuthorize("#userId == authentication.principal.id")
	public ResponseEntity<UserProfileDto> updateProfile(@PathVariable Integer userId,
														@Valid @RequestBody UserProfileDto profileDto) {
		User updatedUser = userService.updateProfile(userId, profileDto);
		UserProfileDto updatedProfileDto = userService.getUserProfile(updatedUser.getId());
		return ResponseEntity.ok(updatedProfileDto);
	}
}
