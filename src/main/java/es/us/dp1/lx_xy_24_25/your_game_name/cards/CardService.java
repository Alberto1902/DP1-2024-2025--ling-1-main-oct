package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CardService {

    private CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public Card findCard(Integer id) {
        return cardRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public List<Card> findCards() {
        return cardRepository.findAll();
    }

}
