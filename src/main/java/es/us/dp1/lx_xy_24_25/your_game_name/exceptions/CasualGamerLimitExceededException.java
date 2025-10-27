package es.us.dp1.lx_xy_24_25.your_game_name.exceptions;

/**
 * Excepción lanzada cuando un jugador Casual Gamer excede sus limitaciones
 */
public class CasualGamerLimitExceededException extends RuntimeException {

    private final String limitType;
    
    public CasualGamerLimitExceededException(String limitType, String message) {
        super(message);
        this.limitType = limitType;
    }
    
    public String getLimitType() {
        return limitType;
    }
}
