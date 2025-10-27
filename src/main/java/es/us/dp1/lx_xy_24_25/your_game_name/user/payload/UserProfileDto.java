package es.us.dp1.lx_xy_24_25.your_game_name.user.payload;

import es.us.dp1.lx_xy_24_25.your_game_name.user.ProfileType; 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent; 
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet; 

public class UserProfileDto {


    private Integer id;
    private String username;



    @Size(max = 1000, message = "La biografía no puede exceder los 1000 caracteres.")
    private String biography;

    @NotBlank(message = "La ubicación es obligatoria.")
    @Size(max = 255, message = "La ubicación no puede exceder los 255 caracteres.")
    private String location;

    @PastOrPresent(message = "La fecha de nacimiento no puede ser en el futuro.")
    private LocalDate birthDate;

    @NotNull(message = "El tipo de perfil es obligatorio.")
    private ProfileType profileType;


    private String colorTheme; 
    @Size(max = 500, message = "La URL de la imagen de perfil es demasiado larga.") 
    private String profilePictureUrl;

    private Set<Integer> favoriteGenreIds = new HashSet<>();
    private Set<Integer> favoritePlatformIds = new HashSet<>();
    private Set<Integer> favoriteSagaIds = new HashSet<>();

    private Set<String> favoriteGenreNames = new HashSet<>();
    private Set<String> favoritePlatformNames = new HashSet<>();
    private Set<String> favoriteSagaNames = new HashSet<>();

    public UserProfileDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Set<Integer> getFavoriteGenreIds() {
        return favoriteGenreIds;
    }

    public void setFavoriteGenreIds(Set<Integer> favoriteGenreIds) {
        this.favoriteGenreIds = favoriteGenreIds;
    }

    public Set<Integer> getFavoritePlatformIds() {
        return favoritePlatformIds;
    }

    public void setFavoritePlatformIds(Set<Integer> favoritePlatformIds) {
        this.favoritePlatformIds = favoritePlatformIds;
    }

    public Set<Integer> getFavoriteSagaIds() {
        return favoriteSagaIds;
    }

    public void setFavoriteSagaIds(Set<Integer> favoriteSagaIds) {
        this.favoriteSagaIds = favoriteSagaIds;
    }

    public Set<String> getFavoriteGenreNames() {
        return favoriteGenreNames;
    }

    public void setFavoriteGenreNames(Set<String> favoriteGenreNames) {
        this.favoriteGenreNames = favoriteGenreNames;
    }

    public Set<String> getFavoritePlatformNames() {
        return favoritePlatformNames;
    }

    public void setFavoritePlatformNames(Set<String> favoritePlatformNames) {
        this.favoritePlatformNames = favoritePlatformNames;
    }

    public Set<String> getFavoriteSagaNames() {
        return favoriteSagaNames;
    }

    public void setFavoriteSagaNames(Set<String> favoriteSagaNames) {
        this.favoriteSagaNames = favoriteSagaNames;
    }
}