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

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Genre;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.GenreRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Platform;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.PlatformRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Saga;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.SagaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.payload.UserProfileDto;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@Service
public class UserService {

	@Autowired
    private UserRepository userRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private SagaRepository sagaRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;	
	}

	@Transactional
	public User saveUser(User user) throws DataAccessException {
		if (user.getLocation() == null || user.getLocation().isBlank()) {
			user.setLocation("unknown");
		}
		userRepository.save(user);
		return user;
	}

	@Transactional(readOnly = true)
	public User findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}	

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
	}

	public Boolean existsUser(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Page<User> findAllPages(Pageable pageable) {
		return userRepository.findAllPages(pageable);
	}

	public Iterable<User> findAllByAuthority(String auth) {
		return userRepository.findAllByAuthority(auth);
	}

	@Transactional
	public User updateUser(@Valid User user, Integer idToUpdate) {
		User toUpdate = findUser(idToUpdate);
		BeanUtils.copyProperties(user, toUpdate, "id");
		userRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deleteUser(Integer id) {
		User toDelete = findUser(id);
		this.userRepository.delete(toDelete);
	}

	@Transactional
	public User makeOnline(String username) {
		User user = findUser(username);
		user.setOnline(true);
		userRepository.save(user);
		return user;
	}
	
	@Transactional
	public User makeOffline(String username) {
		User user = findUser(username);
		user.setOnline(false);
		userRepository.save(user);
		return user;
	}

	@Transactional(readOnly = true)
	public Page<Object[]> findAllByWins(Pageable pageable) {
		return userRepository.findAllByWins(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Object[]> findAllByGamesPlayed(Pageable pageable) {
		return userRepository.findAllByGamesPlayed(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Object[]> findAllByWinRatio(Pageable pageable) {
		return userRepository.findAllByWinRatio(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Object[]> findAllByGamesPlayedFriends(Integer id, Pageable pageable) {
		return userRepository.findAllByGamesPlayedFriends(id, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Object[]> findAllByWinsFriends(Integer id, Pageable pageable) {
		return userRepository.findAllByWinsFriends(id, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Object[]> findAllByWinRatioFriends(Integer id, Pageable pageable) {
		return userRepository.findAllByWinRatioFriends(id, pageable);
	}

	@Transactional(readOnly = true)
	public Integer countPlayers() {
		return userRepository.countPlayers();
	}

	@Transactional(readOnly = true)
	public Integer countOnlinePlayers() {
		return userRepository.countOnlinePlayers();
	}

	public User updateProfile(Integer userId, UserProfileDto profileDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        user.setBiography(profileDto.getBiography());
        user.setLocation(profileDto.getLocation());
        user.setBirthDate(profileDto.getBirthDate());
        user.setProfilePictureUrl(profileDto.getProfilePictureUrl());
        user.setProfileType(profileDto.getProfileType());

        if (profileDto.getFavoriteGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(profileDto.getFavoriteGenreIds()));
            user.setFavoriteGenres(genres);
        }
        if (profileDto.getFavoritePlatformIds() != null) {
            Set<Platform> platforms = new HashSet<>(platformRepository.findAllById(profileDto.getFavoritePlatformIds()));
            user.setFavoritePlatforms(platforms);
        }
        if (profileDto.getFavoriteSagaIds() != null) {
            Set<Saga> sagas = new HashSet<>(sagaRepository.findAllById(profileDto.getFavoriteSagaIds()));
            user.setFavoriteSagas(sagas);
        }

        return userRepository.save(user);
    }

	public UserProfileDto getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));
        return convertUserToUserProfileDto(user);
    }

	private UserProfileDto convertUserToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setBiography(user.getBiography());
        dto.setLocation(user.getLocation());
        dto.setBirthDate(user.getBirthDate());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setProfileType(user.getProfileType());
        if (user.getFavoriteGenres() != null) {
            dto.setFavoriteGenreIds(user.getFavoriteGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
            dto.setFavoriteGenreNames(user.getFavoriteGenres().stream().map(Genre::getName).collect(Collectors.toSet()));
        }
        return dto;
    }
}
