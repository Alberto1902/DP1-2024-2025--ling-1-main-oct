package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS para permitir que el frontend de React
 * se comunique con el backend de Spring Boot.
 */
@Configuration // Le dice a Spring que esta clase contiene configuración
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // <--- Aplica esta configuración a todas las rutas bajo /api/v1/
                .allowedOrigins("http://localhost:3000") // <--- ¡Permite las peticiones desde tu frontend!
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos los encabezados
                .allowCredentials(true); // Permite el envío de cookies y tokens de autenticación (JWT)
    }
}