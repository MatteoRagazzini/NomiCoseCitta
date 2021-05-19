import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.handler.StaticHandler;
import rabbitMQ.Emitter;
import rabbitMQ.MessageType;

public class WebService extends AbstractVerticle {

    private Emitter emitter;
    private Integer createdGame;

    public void start() {
        emitter = new Emitter();
        createdGame = 0;
        Router router = Router.router(vertx);
        router.mountSubRouter("/eventbus", eventBusHandler());
        router.mountSubRouter("/api", gameApiRouter());
        router.route().handler(staticHandler());

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    private StaticHandler staticHandler() {
        return StaticHandler.create()
                .setCachingEnabled(false);
    }

    private Router gameApiRouter() {

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route().consumes("application/json");
        router.route().produces("application/json");

//        router.route("/game/:id").handler(handler::initAuctionInSharedData);
//        router.get("/game/:id").handler(handler::handleGetAuction);
        router.post("/game/:id").handler(context -> {
            // una volta creata la partita(settings e player)la mando come json al GameManager)
            System.out.println("POST");
            System.out.println(context.getBodyAsJson().encodePrettily());
            emitter.emit(MessageType.CREATE, context.getBodyAsJson().encode());
        });

        router.get("/game/create").handler(context -> {
            createdGame++;
           context.response()
                   .putHeader("content-type", "text/plain")
                   .setStatusCode(200)
                   .end(createdGame.toString());
        });

        return router;
    }

    private Router eventBusHandler() {
        SockJSBridgeOptions options = new SockJSBridgeOptions ()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("game\\.[0-9]+"));
        return SockJSHandler.create(vertx).bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                System.out.println("A socket was created");
            }
            event.complete(true);
        });
    }
}


