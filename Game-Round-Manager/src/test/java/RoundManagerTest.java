import model.User;
import model.db.DBManager;
import model.game.*;
import model.round.Round;
import model.round.RoundManager;
import model.round.RoundScores;
import model.round.RoundType;
import org.junit.jupiter.api.*;
import presentation.Presentation;
import rabbit.Emitter;
import rabbit.MessageType;
import rabbit.RPCClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoundManagerTest {

    private final static String GAME_ID = "1000";

    private ExecutorService ex;
    private RPCClient client;
    private Emitter fakeGameManager;
    private DBManager<Round> db;

    @BeforeAll
    private void setUp() throws IOException, TimeoutException {
        new RoundManager();
        client = new RPCClient();
        fakeGameManager = new Emitter("game");
        db = new DBManager<>("NCCRounds", "Rounds", Round.class);
    }

    @AfterAll
    private void cleanDb() {
        db.remove(GAME_ID);
    }

    @BeforeEach
    public void setUpExecutor()  {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @AfterEach
    public void tearDownExecutor() {
        ex.shutdownNow();
    }

    public CompletableFuture<String> callAsync(RPCClient client, MessageType type, String json) {
        final CompletableFuture<String> result = new CompletableFuture<>();
        ex.execute(() -> client.call(type,json, result::complete));
        return result;
    }

    @Test void testStartGameRound() throws Throwable {
        emitStartMessage();
        sleep(500);
        Optional<Round> newRound = db.getElemById(GAME_ID);
        assertTrue(newRound.isPresent());
        assertEquals(RoundType.STOP, newRound.get().getGame().getSettings().getRoundType());
        assertEquals(GameState.STARTED, newRound.get().getGame().getState());
        assertFalse(newRound.get().scoresAvailable());
    }

    @Test void testUserSendWords() throws Exception {
        emitStartMessage();

        CompletableFuture<String> res = callAsync(client, MessageType.WORDS,
                getWordsJson("Pippo", "Bob", "Barattolo", "Bologna"));
        assertEquals("null", res.get());
        Optional<Round> newRound = db.getElemById(GAME_ID);
        assertTrue(newRound.isPresent());
        assertFalse(newRound.get().getRoundWords().allDelivered());

        CompletableFuture<String> res2 = callAsync(client, MessageType.WORDS,
                getWordsJson("Alice", "Bob", "Barattolo", "Bologna"));
        assertNotEquals("null", res2.get());
        newRound = db.getElemById(GAME_ID); //retrieve updated round
        assertTrue(newRound.isPresent());
        assertTrue(newRound.get().getRoundWords().allDelivered());
        assertEquals(GameState.CHECK, newRound.get().getGame().getState());
        assertEquals(2, newRound.get().getRoundWords().getUsersWords().size());
        assertTrue(newRound.get().getRoundWords().getUsersWords().get(0).getWords().containsKey("nomi"));
    }

    @Test void testUserSendVotes() throws Exception {
        testUserSendWords();
        CompletableFuture<String> res = callAsync(client, MessageType.VOTES,
               getUserEvaluationJson("Pippo", "Alice"));
        assertEquals("null", res.get());
        Optional<Round> newRound = db.getElemById(GAME_ID);
        assertTrue(newRound.isPresent());
        assertFalse(newRound.get().getRoundWords().allEvaluationAreDelivered());

        CompletableFuture<String> res2 = callAsync(client, MessageType.VOTES,
                getUserEvaluationJson("Alice", "Pippo"));
        assertNotEquals("null", res2.get());
        RoundScores scores = Presentation.deserializeAs(res2.get(), RoundScores.class);
        newRound = db.getElemById(GAME_ID);
        assertTrue(newRound.isPresent());
        assertEquals(10, scores.getUserScores().get(0).getTotalScore());
    }


    private void emitStartMessage(){
        Game g = new Game(GAME_ID,
                new GameSettings(3, RoundType.STOP, Arrays.asList("nomi", "cose", "citta"), 2));
        g.addNewUser(new User("Pippo", "TestAddressPippo"));
        g.addNewUser(new User("Alice", "TestAddressAlice"));
        g.setState(GameState.STARTED);
        fakeGameManager.emit(MessageType.START, Presentation.serializerOf(Game.class).serialize(g));
    }

    private String getWordsJson(String userID, String nomi, String cose, String citta){
        return "{\"gameID\": "+GAME_ID+"," +
                "\"userID\": "+userID+"," +
                "\"nomi\": "+nomi+"," +
                "\"cose\": "+cose+"," +
                "\"citta\": "+citta+"}";
    }

    private String getUserEvaluationJson(String voterID, String userID){
        return "{\"gameID\": "+GAME_ID+"," +
                "\"voterID\": "+voterID+"," +
                "\"votes\": [" +
                getVoteJson(voterID)+"," +
                getVoteJson(userID)+"]}";
    }

    private String getVoteJson(String userID){
        return  "{\"userID\": "+userID+"," +
                "\"nomi\": "+"ok"+"," +
                "\"cose\": "+"no"+"," +
                "\"citta\": "+"ok"+"}";
    }
}
