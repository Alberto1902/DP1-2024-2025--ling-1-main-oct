package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card sampleCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleCard = new Card();
        sampleCard.setId(1);
        sampleCard.setLetter("A");
        sampleCard.setTitle("Test Title");
        sampleCard.setImage("test_image.png");
        sampleCard.setLetterImage("letter_image.png");
    }

    @Test 
    void shouldFindCardById() {
        when(cardRepository.findById(1)).thenReturn(Optional.of(sampleCard));
        Card foundCard = cardService.findCard(1);
        assertNotNull(foundCard);
        assertEquals(1, foundCard.getId());
    }

    @Test 
    void shouldFindAllCards() {
        when(cardRepository.findAll()).thenReturn(Arrays.asList(sampleCard));
        List<Card> cards = cardService.findCards();
        assertNotNull(cards);
        assertTrue(cards.size() > 0);
    }
}
