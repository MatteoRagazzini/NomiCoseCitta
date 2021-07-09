package model.game;

import com.rabbitmq.client.DeliverCallback;
import model.db.DBManager;
import model.request.DisconnectRequest;
import model.request.StartRequest;
import model.request.UserInLobbyRequest;
import presentation.Presentation;

import rabbit.*;

import java.util.*;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private final Emitter emitter;
    private final DBManager<Game> gameDb;

    public GameManager() {
        games = new ArrayList<>();
        emitter = new Emitter("game");
        gameDb = new DBManager<>("NCCGames", "Games", Game.class);
        games.addAll(gameDb.getAllElement());
        GameIDSupplier.getInstance().setGameCreated(games.stream()
                .map(g -> Integer.parseInt(g.getId()))
                .max(Comparator.comparingInt(i -> i))
                .orElse(0));
        new SimpleConsumer("round",MessageType.UPDATE, updateGame());
        new Thread(() -> new RPCServer(getCallbackMap())).start();
    }

    private DeliverCallback updateGame() {
        return (consumerTag, message) -> {
            try {
                Game gameUpdated = Presentation.deserializeAs(new String(message.getBody(),
                        "UTF-8"), Game.class);
                gameDb.update(gameUpdated.getId(), gameUpdated);
                games.removeIf(g -> g.getId().equals(gameUpdated.getId()));
                games.add(gameUpdated);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private Map<MessageType, Function<String,String>> getCallbackMap(){
        Map<MessageType, Function<String,String>> map = new HashMap<>();
        map.put(MessageType.JOIN, joinGame());
        map.put(MessageType.DISCONNECT, disconnectGame());
        map.put(MessageType.CREATE, createGame());
        map.put(MessageType.START, startNextGamePhase());
        return map;
    }

    private Function<String, String> createGame() {
        return (message) -> {
            try {
                games.add(Presentation.deserializeAs(message, Game.class));
                gameDb.insert(getLastGame());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getLastGame().getId();
        };
    }

    private Function<String, String> startNextGamePhase() {
        return message -> {
            try {
                var startReq = Presentation.deserializeAs(message, StartRequest.class);
                var game = getGameById(startReq.getGameID());
                if(game.isPresent() && game.get().gameCouldStart() && !game.get().isStarted()){
                    if(game.get().hasNextRound()) {
                        game.get().setState(GameState.STARTED);
                        emitter.emit(MessageType.START, Presentation.serializerOf(Game.class).serialize(game.get()));
                        gameDb.update(game.get().getId(), game.get());
                        return Presentation.serializerOf(Game.class).serialize(game.get());
                    }else {
                        game.get().setState(GameState.FINISHED);
                        gameDb.update(game.get().getId(), game.get());
                        emitter.emit(MessageType.FINISH, game.get().getId());
                        return Presentation.serializerOf(GameScores.class).serialize(game.get().getScores());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private Function<String, String> joinGame() {
        return message -> {
            try {
                var request = Presentation.deserializeAs(message, UserInLobbyRequest.class);
                var game = getGameById(request.getGameID());
                if(game.isPresent()){
                    if(!game.get().userAlreadyPresent(request.getUser())) {
                        if (game.get().addNewUser(request.getUser())) {
                            gameDb.update(game.get().getId(), game.get());
                            sendGameUpdateToRoundManager(game.get());
                            return Presentation.serializerOf(Game.class).serialize(game.get());
                        }
                    } else {
                        return "XXX";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }


    private Function<String, String> disconnectGame() {
        return message -> {
            try {
                var req = Presentation.deserializeAs(message, DisconnectRequest.class);
                var game = games.stream().filter(g -> g.removeUser(req.getUserAddress())).findFirst();
                if(game.isPresent()){
                    sendGameUpdateToRoundManager(game.get());
                    if(game.get().isFinished() && game.get().getOnlineUsers().isEmpty()){
                        gameDb.remove(game.get().getId());
                    }else{
                        gameDb.update(game.get().getId(), game.get());
                    }
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private void sendGameUpdateToRoundManager(Game game){
        if(game.roundIsStarted()) {
            emitter.emit(MessageType.DISCONNECT, Presentation.serializerOf(Game.class).serialize(game));
        }
    }


    private Game getLastGame(){
        return games.get(games.size() - 1);
    }

    public Optional<Game> getGameById(String id){
        return games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();
    }
}
