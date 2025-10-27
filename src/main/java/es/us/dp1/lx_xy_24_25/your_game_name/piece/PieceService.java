package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.fight.Fight;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.builder.PieceBuilderDirector;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.square.SquareService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PieceService {

    private PieceRepository pieceRepository;
    private final SquareService squareService;
    private final GameSessionService gameSessionService;
    private final PieceBuilderDirector builder;

    @Autowired
    public PieceService(PieceRepository pieceRepository, SquareService squareService, GameSessionService gameSessionService, PieceBuilderDirector builder) {
        this.pieceRepository = pieceRepository;
        this.squareService = squareService;
        this.gameSessionService = gameSessionService;
        this.builder = builder;
    }

    @Transactional
    public Piece createPiece(Piece piece) {
        return pieceRepository.save(piece);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Piece> createAllPieces(GameSession game) {
        List<Piece> pieces = new ArrayList<>();
        List<User> users = game.getPlayers();
        int randomIndex = (int) (Math.random() * users.size());
        if(!game.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalArgumentException("Game must be in progress");
        }
        for(int i = 0; i < users.size(); i++) {
            Piece p = builder.ofType(false, users.get(i))
                .withGame(game)
                .withPlayerOrder(randomIndex)
                .withImage("/piece" + i + ".png")
                .withUser(users.get(i))
                .withPosition(null).build();
            randomIndex++;
            if(randomIndex == users.size()) {
                randomIndex = 0;
            }
            p = createPiece(p);
            pieces.add(p);
        }
        for(int i = 0; i < 11-users.size(); i++){
            Piece p = builder.ofType(false, null)
                .withGame(game)
                .withPlayerOrder(null)
                .withImage("/piecenpc.png")
                .withUser(null)
                .withPosition(null)
                .build();
            p = createPiece(p);
            pieces.add(p);
        }
        Square safeArea = squareService.findSquare(1);
        Piece c = builder.ofType(true, null)
            .withGame(game)
            .withPlayerOrder(null)
            .withImage("/pieceNC.png")
            .withUser(null)
            .withPosition(safeArea)
            .build();
        c = createPiece(c);
        pieces.add(c);
        return pieces;
    }

    @Transactional
    public Piece updatePiece(Piece piece, Integer id) {
        Piece updatedPiece = pieceRepository.findById(id).get();
        updatedPiece.setPlayerOrder(piece.getPlayerOrder());
        updatedPiece.setWord(piece.getWord());
        updatedPiece.setStrength(piece.getStrength());
        updatedPiece.setPosition(piece.getPosition());
        updatedPiece.setHand(piece.getHand());
        updatedPiece.setBag(piece.getBag());
        updatedPiece.setState(piece.getState());
        pieceRepository.save(updatedPiece);
        return updatedPiece;
    }

    @Transactional
    public Piece resetActionPoints(Piece piece, Integer id) {
        piece.setActionPoints(0);
        return updatePiece(piece, id);
    }

    @Transactional(readOnly = true)
    public Piece findPiece(Integer id) {
        return pieceRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Piece findPieceByUserIdAndGameId(Integer userId, Integer gameId) {
        return pieceRepository.findByUserIdAndGameId(userId, gameId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Piece> findPiecesByGameId(Integer gameId) {
        return pieceRepository.findByGameId(gameId);
    }

    @Transactional(readOnly = true)
    public Piece findPieceByGameIdAndPlayerOrder(Integer gameId, Integer playerOrder) {
        return pieceRepository.findByGameIdAndPlayerOrder(gameId, playerOrder).orElse(null);
    }

    @Transactional
    public Piece movePiece(Square square, Piece piece, User user) {
        Piece userPiece = findPieceByUserIdAndGameId(user.getId(), piece.getGame().getId());
        if(userPiece.getPlayerOrder() != piece.getGame().getTurn() || userPiece.getActionPoints() == 0) {
            throw new IllegalArgumentException("It is not your turn");
        }
        else {    
            Piece newPiece = userPiece.getState().movePiece(square, piece);
            Piece updatedPiece = updatePiece(newPiece, piece.getId());
            return updatedPiece;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Square> getPossibleLaunches(Piece piece, String word){
        String w = "";
        List<Card> cards = piece.getBag();
        for(int j = 0; j < word.length(); j++){
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getLetter().toLowerCase().equals(Character.toString(word.charAt(j)))) {
                    w = w + word.charAt(j);                   
                    break;
                }
            }
        }
        if(!w.equals(word)) {
            throw new IllegalArgumentException("Word cannot be formed with the letters in the bag");
        }
        w = w.substring(0,1).toUpperCase() + w.substring(1).toLowerCase();
        List<Square> squares= this.squareService.findByNameContains(w);
        return squares;
    }

    @Transactional
    public Piece setInitialPosition(Piece piece, Square square){
        List<Piece> pieces = findPiecesByGameId(piece.getGame().getId());
        List<Integer> availableSquares = IntStream.range(2,36).boxed().collect(Collectors.toList());
        for(Piece p : pieces) {
            if(p.getUser()!=null && p.getPosition() != null) {
                Integer positionId = p.getPosition().getId();
                availableSquares.remove(positionId);
            }
        }
        if(!availableSquares.contains(square.getId())) {
            int randomIndex = (int) (Math.random() * availableSquares.size());
            square = this.squareService.findSquare(availableSquares.get(randomIndex));
        }
        piece.setPosition(square);
        Piece updatedPiece = updatePiece(piece, piece.getId());
        return updatedPiece;
    }
    
    @Transactional
    public List<Piece> setAllNonPlayerInitialPositions(Integer gameId) {
        List<Integer> availableSquares = IntStream.range(2,36).boxed().collect(Collectors.toList());
        List<Piece> pieces = findPiecesByGameId(gameId);
        for(Piece piece : pieces) {
            if(piece.getUser()!=null) {
                Integer positionId = piece.getPosition().getId();
                availableSquares.remove(positionId);
            }
        }
        for (Piece piece : pieces) {
            if (piece.getUser() == null && !piece.getIsCampbell()) {
                int randomIndex = (int) (Math.random() * availableSquares.size());
                Square square = this.squareService.findSquare(availableSquares.get(randomIndex));
                piece.setPosition(square);
                updatePiece(piece, piece.getId());
                availableSquares.remove(randomIndex);
            }
        }
        return pieces;
    }

    @Transactional
    public Piece receiveCard(Card card, Piece piece){
        GameSession game = piece.getGame();
        if(piece.getPlayerOrder()!=game.getTurn()){
            throw new IllegalArgumentException("It is not your turn");
        }
        Piece newPiece = piece.getState().receiveCard(card);
        return updatePiece(newPiece, piece.getId());
    }

    @Transactional
    public Piece receiveCardAfterFight(Card card, Piece piece){
        List<Card> cards = piece.getHand();
        cards.add(card);
        piece.setHand(cards);
        return updatePiece(piece, piece.getId());
    }

    @Transactional
    public Piece receiveInitialCards(List<Card> cards, Piece piece){
        List<Card> updatedCards = cards;
        piece.setHand(updatedCards);
        return updatePiece(piece, piece.getId());
    }

    @Transactional
    public Piece putCardsInBag(List<Card> cards, Piece piece){
        Piece newPiece = piece.getState().putCardsInBag(cards);
        return updatePiece(newPiece, piece.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Card> discardCards(String word, Piece piece){
        if (!isAValidWord(word)) {
            throw new IllegalArgumentException("That is not a valid word");
        }
        Map<Piece, List<Card>> m= piece.getState().discardCards(word);
        Piece newPiece = (Piece) m.keySet().toArray()[0];
        List<Card> cards = m.get(newPiece);
        GameSession game = piece.getGame();
        if(cards.size() == 0){
            game.setTurn((game.getTurn()+1) % game.getPlayers().size());
            gameSessionService.updateGameSession(game, game.getId());
        }
        updatePiece(piece, piece.getId());
        return cards;
    }

    public boolean isAValidWord(String word) {
        if (word.length() < 2) {
            return false;
        } else if (word.length() == 2) {
            return true;
        } else if (word.length() > 2) {
            List<String> words = readDictionary("src/main/resources/dictionary.txt");
            if (words.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public List<String> readDictionary(String path) {
        List<String> lineas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return lineas;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer escape(Piece piece) {
        GameSession game = piece.getGame();
        Square square = piece.getPosition();
        String bag = piece.getWord().toUpperCase();
        Integer dice;
        if (square.getEscapeWord() == null) {
            dice = handleEscapeWithoutEscapeWord(piece, game, bag);
        } else {
            dice = handleEscapeWithEscapeWord(piece, game, square, bag);
        }

        updatePiece(piece, piece.getId());

        return dice;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer handleEscapeWithoutEscapeWord(Piece piece, GameSession game, String bag) {
        if (bag.equals("CAMPBELL") || bag.equals("EMPEROR")) {
            piece.setActionPoints(piece.getActionPoints() - 1);
            return processEscapeAttempt(piece, game);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot escape from this square");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer handleEscapeWithEscapeWord(Piece piece, GameSession game, Square square, String bag) {
        if (square.getEscapeWord().toUpperCase().equals(bag)) {
            piece.setActionPoints(piece.getActionPoints() - 1);
            return processEscapeAttempt(piece, game);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot escape from this square");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer processEscapeAttempt(Piece piece, GameSession game) {
        int diceNumber = (int) (Math.random() * 6) + 1;
        if (diceNumber < piece.getStrength()) {
            endGameSuccessfully(game, piece);
        } else {
            piece.setStrength(piece.getStrength() + 1);
            piece.setActionPoints(0);
        }
        return diceNumber;
    }

    @Transactional(rollbackFor = Exception.class)
    public void endGameSuccessfully(GameSession game, Piece piece) {
        game.setEnd(LocalDateTime.now());
        game.setStatus("FINISHED");
        game.setWinner(piece.getUser());
        gameSessionService.updateGameSession(game, game.getId());
    }

    @Transactional
    public Piece catapulted(Piece piece){
        Integer whiteDice = (int) (Math.random() * 6) + 1;
        Integer blackDice = (int) (Math.random() * 6) + 1;
        Square square = this.squareService.findSquare(blackDice, whiteDice);
        piece.setPosition(square);
        return updatePiece(piece, piece.getId());
    }

    @Transactional(readOnly = true)
    public List<Piece> findPiecesInSquare(GameSession game, Square square) {
        return pieceRepository.findByGameIdAndPositionId(game.getId(), square.getId());
    }

    @Transactional
    public Piece defineActionPoints(Piece piece){
        GameSession game = piece.getGame();
        if(piece.getPlayerOrder()!=game.getTurn()){
            throw new IllegalArgumentException("It is not your turn");
        }
        Piece newPiece = piece.getState().defineActionPoints();
        return updatePiece(newPiece, piece.getId());
    }

    @Transactional
    public Piece isFighting(Piece piece) {
        piece.setIsFighting(true);
        return updatePiece(piece, piece.getId());
    }

    @Transactional
    public Piece isAttacking(Piece piece) {
        piece.setIsAttacking(true);
        return updatePiece(piece, piece.getId());
    }

    @Transactional
    public Piece isDefending(Piece piece) {
        piece.setIsDefending(true);
        return updatePiece(piece, piece.getId());
    }

    @Transactional
    public Piece getAttacker(Integer gameId){
        List<Piece> pieces = findPiecesByGameId(gameId);
        for(Piece piece : pieces){
            if(piece.getIsAttacking()){
                return piece;
            }
        }
        return null;
    }

    @Transactional
    public Piece getDefender(Integer gameId){
        List<Piece> pieces = findPiecesByGameId(gameId);
        for(Piece piece : pieces){
            if(piece.getIsDefending()){
                return piece;
            }
        }
        return null;
    }

    @Transactional
    public Piece stealFromOtherPlayerHand(Piece winner, Piece loser){
        GameSession game = winner.getGame();
        Fight fight = game.getCurrenFight();
        if(!fight.getAttacker().equals(winner) && !fight.getDefender().equals(winner)){
            throw new IllegalArgumentException("Winner must be either attacker or defender");
        }
        if(fight.getAttacker().equals(null)&& fight.getDefender().equals(null)){
            throw new IllegalArgumentException("Fight must have an attacker and a defender");
        }
        List<Card> cards = loser.getHand();
        if(cards.size() == 0){
            return winner;
            }
        int randomIndex = (int) (Math.random() * cards.size());
        Card card = cards.get(randomIndex);
        cards.remove(randomIndex);
        winner.getHand().add(card);
        updatePiece(winner, winner.getId());
        loser.setHand(cards);
        updatePiece(loser, loser.getId());
        game.setCurrenFight(null);
        gameSessionService.updateGameSession(game, game.getId());
        return winner;

    }

    @Transactional
    public Piece stealFromOtherPlayerBag(Piece winner, Piece loser, Integer cardId){
        Card card = null;
        GameSession game = winner.getGame();
        Fight fight = game.getCurrenFight();
        if(!fight.getAttacker().equals(winner) && !fight.getDefender().equals(winner)){
            throw new IllegalArgumentException("Winner must be either attacker or defender");
        }
        if(fight.getAttacker().equals(null)&& fight.getDefender().equals(null)){
            throw new IllegalArgumentException("Fight must have an attacker and a defender");
        }
        for(Card c : loser.getBag()){
            if(c.getId().equals(cardId)){
                card = c;
            }
        }
        List<Card> cards = loser.getBag();
        if(cards.size() <= 2){
            return winner;
            }
        if(loser.getBag().contains(card)){
            cards.remove(card);
        }
        else{
            throw new IllegalArgumentException("Card must be in the bag");
        }
        winner.getHand().add(card);
        updatePiece(winner, winner.getId());
        loser.setBag(cards);
        updatePiece(loser, loser.getId());
        game.setCurrenFight(null);
        gameSessionService.updateGameSession(game, game.getId());
        return winner;
    }

    @Transactional
    public Card discardAfterLosing(Piece loser, Integer cardId, Boolean fromHand){
        Card card = null;
        GameSession game = loser.getGame();
        Fight fight = game.getCurrenFight();
        if(!fight.getAttacker().equals(loser) && !fight.getDefender().equals(loser)){
            throw new IllegalArgumentException("Loser must be either attacker or defender");
        }
        if(fight.getAttacker().equals(null)&& fight.getDefender().equals(null)){
            throw new IllegalArgumentException("Fight must have an attacker and a defender");
        }

        if(fromHand){
            for(Card c : loser.getHand()){
                if(c.getId().equals(cardId)){
                    card = c;
                }
            }
            List<Card> cards = loser.getHand();
            if(cards.size() == 0){
                throw new IllegalArgumentException("Hand must have at least one card");
            }
            if(loser.getHand().contains(card)){
                cards.remove(card);
            }
            else{
                throw new IllegalArgumentException("Card must be in the hand");
            }
            loser.setHand(cards);
            updatePiece(loser, loser.getId());
        }
        else{
            for(Card c : loser.getBag()){
                if(c.getId().equals(cardId)){
                    card = c;
                }
            }
            List<Card> cards = loser.getBag();
            if(cards.size() <= 2){
                throw new IllegalArgumentException("Bag must have at least two cards");
            }
            if(loser.getBag().contains(card)){
                cards.remove(card);
            }
            else{
                throw new IllegalArgumentException("Card must be in the bag");
            }
            loser.setBag(cards);
            updatePiece(loser, loser.getId());
        }

        game.setCurrenFight(null);
        gameSessionService.updateGameSession(game, game.getId());
        return card;
    }
}
