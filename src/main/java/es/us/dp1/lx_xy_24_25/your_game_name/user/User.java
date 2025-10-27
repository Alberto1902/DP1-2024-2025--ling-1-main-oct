package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*; 
import jakarta.validation.constraints.NotBlank;
import es.us.dp1.lx_xy_24_25.your_game_name.model.Person;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Genre;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Platform;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.Saga;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "appusers")
@EqualsAndHashCode(of = {"username"}, callSuper = false)
public class User extends Person {

	@NotNull
	@Column(unique = true)
	String username;

	String password;

	@Column(nullable = true)
	Boolean online = false;

	@Column(name = "biography", length = 1000)
    private String biography;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "authority")
	Authorities authority;

	@NotBlank(message = "Location cannot be blank")
    @Column(name = "location")
    private String location;

	@Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_picture")
	String profilePictureUri = "/napoleonColores.png";

	@Enumerated(EnumType.STRING)
    @Column(name = "profile_type")
    private ProfileType profileType;

	@Column(name = "profile_picture_color")
	String colorTheme = "#101010";

	@Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "daily_games_played")
    private Integer dailyGamesPlayed = 0;

    @Column(name = "last_game_date")
    private LocalDate lastGameDate;

	String theme;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "user_achievements",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "achievement_id"))
	private List<Achievement> obtainedAchievements = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_genres",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> favoriteGenres;

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_platforms",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "platform_id"))
    private Set<Platform> favoritePlatforms;

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_sagas",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "saga_id"))
    private Set<Saga> favoriteSagas;

	public Boolean hasAuthority(String auth) {
		return authority.getAuthority().equals(auth);
	}

	public Boolean hasAnyAuthority(String... authorities) {
		Boolean cond = false;
		for (String auth : authorities) {
			if (auth.equals(authority.getAuthority()))
				cond = true;
		}
		return cond;
	}

	 public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Authorities getAuthority() {
        return authority;
    }

    public void setAuthority(Authorities authority) {
        this.authority = authority;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Set<Genre> getFavoriteGenres() {
        return favoriteGenres;
    }

    public void setFavoriteGenres(Set<Genre> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
    }

    public Set<Platform> getFavoritePlatforms() {
        return favoritePlatforms;
    }

    public void setFavoritePlatforms(Set<Platform> favoritePlatforms) {
        this.favoritePlatforms = favoritePlatforms;
    }

    public Set<Saga> getFavoriteSagas() {
        return favoriteSagas;
    }

    public void setFavoriteSagas(Set<Saga> favoriteSagas) {
        this.favoriteSagas = favoriteSagas;
    }

    public Integer getDailyGamesPlayed() {
        return dailyGamesPlayed;
    }

    public void setDailyGamesPlayed(Integer dailyGamesPlayed) {
        this.dailyGamesPlayed = dailyGamesPlayed;
    }

    public LocalDate getLastGameDate() {
        return lastGameDate;
    }

    public void setLastGameDate(LocalDate lastGameDate) {
        this.lastGameDate = lastGameDate;
    }

}
