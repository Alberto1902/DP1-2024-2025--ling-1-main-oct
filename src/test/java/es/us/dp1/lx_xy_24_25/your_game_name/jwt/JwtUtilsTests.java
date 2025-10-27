package es.us.dp1.lx_xy_24_25.your_game_name.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.lang.reflect.Field;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

class JwtUtilsTests {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    private final String jwtSecret = "testSecret";
    private final int jwtExpirationMs = 60000; 
    private final String username = "testUser";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    
        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, jwtSecret);
    
        Field expirationField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtils, jwtExpirationMs);
        
    }
    

    @Test
    void testGenerateTokenFromUsernameWithAuthority_Success() {
        Authorities authority = new Authorities();
        authority.setAuthority("ADMIN");

        String token = jwtUtils.generateTokenFromUsername(username, authority);

        assertNotNull(token);
        String extractedUsername = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGenerateTokenFromUsernameWithUserDetails_Success() {
        String token = jwtUtils.generateTokenFromUsername(username, userDetails);

        assertNotNull(token);
        String extractedUsername = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGetUserNameFromJwtToken_Success() {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateJwtToken_Success() {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_Expired() {
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * jwtExpirationMs))
                .setExpiration(new Date(System.currentTimeMillis() - jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    void testValidateJwtToken_InvalidSignature() {
        String tokenWithInvalidSignature = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(tokenWithInvalidSignature));
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        String malformedToken = "invalid.token.structure";

        assertFalse(jwtUtils.validateJwtToken(malformedToken));
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void testValidateJwtToken_NullToken() {
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}
