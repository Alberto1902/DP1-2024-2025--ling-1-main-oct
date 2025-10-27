package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

public class UserStatisticsDTO {
    private int gamesPlayed;
    private int victories;
    private int defeats;
    private double winRatio;
    private double lossRatio;
    private double averageGameDuration; // In minutes

    // Constructor to easily create instances
    public UserStatisticsDTO(int gamesPlayed, int victories, int defeats, double winRatio, double lossRatio, double averageGameDuration) {
        this.gamesPlayed = gamesPlayed;
        this.victories = victories;
        this.defeats = defeats;
        this.winRatio = winRatio;
        this.lossRatio = lossRatio;
        this.averageGameDuration = averageGameDuration;
    }

    // Default constructor (often needed by JSON deserializers)
    public UserStatisticsDTO() {
    }

    // Getters for all fields (Crucial for Jackson to serialize to JSON)
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getVictories() {
        return victories;
    }

    public int getDefeats() {
        return defeats;
    }

    public double getWinRatio() {
        return winRatio;
    }

    public double getLossRatio() {
        return lossRatio;
    }

    public double getAverageGameDuration() {
        return averageGameDuration;
    }

    // Setters (optional, but good practice if you ever need to set them from external sources)
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setVictories(int victories) {
        this.victories = victories;
    }

    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }

    public void setWinRatio(double winRatio) {
        this.winRatio = winRatio;
    }

    public void setLossRatio(double lossRatio) {
        this.lossRatio = lossRatio;
    }

    public void setAverageGameDuration(double averageGameDuration) {
        this.averageGameDuration = averageGameDuration;
    }
}