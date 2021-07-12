import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Test class for the WebService. It requires Rabbitmq on the default port in order to work.
 * In particular the test use a dumb RPCServer only to verify if the communication
 * between client-webserver-rpcClient-rabbitmq-rpcServer works with all the possible request
 */
@Disabled
@ExtendWith(VertxExtension.class)
public class WebServiceTest{

    Thread rpcServer;
    WebClient client;

    public WebServiceTest() {
        this.rpcServer = new Thread(()-> new RPCServer(getCallbackMap()));
    }

    @BeforeEach
    @DisplayName("Deploy a verticle")
    void prepare(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new WebService(), testContext.succeedingThenComplete());
        client = WebClient.create(vertx);
        rpcServer.start();
    }

    @Test
    @DisplayName("Testing the Creation of a game")
    void testCreateGame(VertxTestContext testContext) {
        testPostRequest(testContext, "/api/game/create");
    }

    @Test
    @DisplayName("Testing the join of a game")
    void testJoin(VertxTestContext testContext) {
        testPostRequest(testContext, "/api/game/join/1");
    }

    @Test
    @DisplayName("Testing the send and receive of users words")
    void testWordsSending(VertxTestContext testContext) {
        testPostRequest(testContext, "/api/game/words/1");
    }

    @Test
    @DisplayName("Testing the send and receive of users votes")
    void testVotes(VertxTestContext testContext) {
        testPostRequest(testContext, "/api/game/votes/1");
    }

    @Test
    @DisplayName("Testing the start of a Game")
    void testStart(VertxTestContext testContext) {
       testPostRequest(testContext, "/api/game/start/1");
    }

    @AfterEach
    @DisplayName("Check that the verticle is still there")
    void lastChecks(Vertx vertx) {
        Assertions.assertEquals(1, vertx.deploymentIDs().size());
    }

    private void testPostRequest(VertxTestContext testContext, String uri){
        client
                .post(8080, "localhost", "/api/game/start/1")
                .sendJsonObject(new JsonObject())
                .onSuccess(res -> {
                    Assertions.assertEquals("ok", res.body().toString());
                    testContext.completeNow();
                });
    }

    private Map<MessageType, Function<String, String> > getCallbackMap(){
        Map<MessageType, Function<String, String>> map = new HashMap<>();
        map.put(MessageType.JOIN, s -> "ok");
        map.put(MessageType.DISCONNECT, s -> "ok");
        map.put(MessageType.CREATE, s -> "ok");
        map.put(MessageType.START, s -> "ok");
        map.put(MessageType.WORDS, s -> "ok");
        map.put(MessageType.VOTES, s -> "ok");

        return map;
    }
}


