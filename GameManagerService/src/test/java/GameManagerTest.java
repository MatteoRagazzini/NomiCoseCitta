import model.game.Game;
import model.game.GameManager;
import model.game.GameState;
import org.junit.jupiter.api.*;
import presentation.Presentation;
import rabbit.MessageType;
import rabbit.RPCClient;

import java.io.IOException;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * To run this tests RabbitMq has to be up
 */
public class GameManagerTest {

    private ExecutorService ex;
    private RPCClient client;

    GameManagerTest() throws IOException, TimeoutException {
        new GameManager();
        client = new RPCClient();
    }

    @BeforeEach
    public void setUp() throws IOException, TimeoutException {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @AfterEach
    public void tearDown() throws IOException {
        client.close();
        ex.shutdownNow();
    }

    public CompletableFuture<String> callAsync(RPCClient client, MessageType type, String json) {
        final CompletableFuture<String> result = new CompletableFuture<>();
        ex.execute(() -> client.call(type,json, result::complete));
        return result;
    }


    @Test String testCreateGame() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> promisedResult = callAsync(client, MessageType.CREATE, getCreateGameJson());
        String id = promisedResult.get(); // this is where the result is awaited
        assertNotEquals("null", id);
        return id;
    }

    @Test void testSingleJoinGame() throws  ExecutionException, InterruptedException {
        CompletableFuture<String> promisedResult = callAsync(client, MessageType.CREATE, getCreateGameJson());
        String id = promisedResult.get(); // this is where the result is awaited
        assertNotEquals("null", id);
        CompletableFuture<String> joinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Pippo"));
        try {
            Game g = Presentation.deserializeAs(joinResult.get(), Game.class);
            assertEquals(id, g.getId());
            assertEquals(1, g.getOnlineUsers().size());
            assertEquals(GameState.WAITING, g.getState());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test void testJoinInWrongGame() throws  ExecutionException, InterruptedException {
        CompletableFuture<String> joinResult = callAsync(client, MessageType.JOIN, getJoinGameJson("-1", "Pippo"));
        assertEquals("null", joinResult.get());
    }

    @Test void testJoinGame() throws  ExecutionException, InterruptedException {
        CompletableFuture<String> promisedResult = callAsync(client, MessageType.CREATE, getCreateGameJson());
        String id = promisedResult.get();
        assertNotEquals("null", id);
        CompletableFuture<String> firstJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Pippo"));
        assertNotEquals("null", firstJoinResult.get());
        CompletableFuture<String> secondJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Alice"));
        assertNotEquals("null", secondJoinResult.get());
        try {
            Game g = Presentation.deserializeAs(secondJoinResult.get(), Game.class);
            assertEquals(id, g.getId());
            assertEquals(2, g.getOnlineUsers().size());
            assertEquals(GameState.WAITING, g.getState());
            assertTrue(g.gameCouldStart());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        CompletableFuture<String> thirdJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Bob"));
        assertEquals("null", thirdJoinResult.get());
    }

    @Test void testStartGame() throws ExecutionException, InterruptedException {
        CompletableFuture<String> promisedResult = callAsync(client, MessageType.CREATE, getCreateGameJson());
        String id = promisedResult.get();
        assertNotEquals("null", id);
        CompletableFuture<String> firstJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Pippo"));
        assertNotEquals("null", firstJoinResult.get());
        CompletableFuture<String> secondJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Alice"));
        assertNotEquals("null", secondJoinResult.get());
        CompletableFuture<String> gameStarted = callAsync(client, MessageType.START, getStartGameJson(id));
        try {
            Game g = Presentation.deserializeAs(gameStarted.get(), Game.class);
            assertEquals(id, g.getId());
            assertEquals(2, g.getOnlineUsers().size());
            assertEquals(GameState.STARTED, g.getState());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test void testUnsuccessfulStartGame() throws ExecutionException, InterruptedException {
        CompletableFuture<String> promisedResult = callAsync(client, MessageType.CREATE, getCreateGameJson());
        String id = promisedResult.get();
        assertNotEquals("null", id);
        CompletableFuture<String> firstJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Pippo"));
        assertNotEquals("null", firstJoinResult.get());
        CompletableFuture<String> gameStarted = callAsync(client, MessageType.START, getStartGameJson(id));
        assertEquals("null", gameStarted.get());
    }

//    @Test void testDisconnection() throws ExecutionException, InterruptedException {
//        CompletableFuture<String> promisedResult = callAsync(client, MessageType.CREATE, getCreateGameJson());
//        String id = promisedResult.get();
//        assertNotEquals("null", id);
//        CompletableFuture<String> firstJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Pippo"));
//        assertNotEquals("null", firstJoinResult.get());
//        CompletableFuture<String> secondJoinResult = callAsync(client, MessageType.JOIN, getJoinGameJson(id, "Alice"));
//        assertNotEquals("null", secondJoinResult.get());
//        try {
//            Game g = Presentation.deserializeAs(secondJoinResult.get(), Game.class);
//            assertEquals(id, g.getId());
//            assertEquals(2, g.getOnlineUsers().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//        CompletableFuture<String> disconnectionResult = callAsync(client, MessageType.DISCONNECT, getDisconnectJson("Pippo"));
//        assertNotEquals("null", disconnectionResult.get());
//        try {
//            Game g = Presentation.deserializeAs(disconnectionResult.get(), Game.class);
//            assertEquals(id, g.getId());
//            assertEquals(1, g.getOnlineUsers().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//
//    }

    private String getCreateGameJson() {
        return "{\"numRounds\": 3," +
                "\"roundsType\": stop," +
                "\"numUsers\": 2," +
                "\"categories\": [" +
                "\"nomi\"," +
                "\"cose\"," +
                "\"citta\"]}";
    }

    private String getJoinGameJson(String id, String userID){
        return "{\"gameID\": "+id+"," +
                "\"userID\": "+userID+"," +
                "\"userAddress\": testAddress}";
    }

    private String getStartGameJson(String id){
        return "{\"gameID\": "+id+"}";
    }


}
