package auctions;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.nio.charset.StandardCharsets;


public class AuctionServiceVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(AuctionServiceVerticle.class);
    private static final String QUEUE_PLAYER_NAME = "users";

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.mountSubRouter("/eventbus", eventBusHandler());
        router.mountSubRouter("/api", auctionApiRouter());
        router.route().failureHandler(errorHandler());
        router.route().handler(staticHandler());

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    private Router eventBusHandler() {
        SockJSBridgeOptions options = new SockJSBridgeOptions()
            .addOutboundPermitted(new PermittedOptions().setAddressRegex("auction\\.[0-9]+"));
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        return SockJSHandler.create(vertx).bridge(options, event -> {
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_PLAYER_NAME, false, false, false, null);
                String message = "Hello World!";
                if (event.type() ==  BridgeEventType.SOCKET_CREATED) {
                    System.out.println("A new Socket was created");
                    message = "new";
                }else if (event.type() == BridgeEventType.SOCKET_CLOSED) {
                    logger.info("A socket was closed");
                    message = "closed";
                }
                channel.basicPublish("", QUEUE_PLAYER_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + "'");
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.complete(true);
        });
    }

    private Router auctionApiRouter() {
        AuctionRepository repository = new AuctionRepository(vertx.sharedData());
        AuctionValidator validator = new AuctionValidator(repository);
        AuctionHandler handler = new AuctionHandler(repository, validator);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route().consumes("application/json");
        router.route().produces("application/json");

        router.route("/auctions/:id").handler(handler::initAuctionInSharedData);
        router.get("/auctions/:id").handler(handler::handleGetAuction);
        router.patch("/auctions/:id").handler(handler::handleChangeAuctionPrice);

        return router;
    }

    private ErrorHandler errorHandler() {
        return ErrorHandler.create(vertx);
    }

    private StaticHandler staticHandler() {
        return StaticHandler.create()
            .setCachingEnabled(false);
    }
}
