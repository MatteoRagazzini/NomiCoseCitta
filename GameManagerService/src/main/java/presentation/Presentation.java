package presentation;


import model.User;
import model.game.Game;
import model.game.GameScores;
import model.game.GameSettings;
import model.request.DisconnectRequest;
import model.request.UserInLobbyRequest;
import model.request.StartRequest;
import model.round.Round;
import model.round.RoundScores;
import model.round.UserRoundScore;
import model.round.words.Evaluation;
import model.round.words.RoundWords;
import model.round.words.UserWords;
import model.round.words.Vote;
import presentation.deserializer.*;
import presentation.deserializer.roundDeserializer.*;
import presentation.serializer.*;
import presentation.serializer.roundSerializer.*;

import java.util.HashMap;
import java.util.Map;

public class Presentation {
    private static final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
    private static final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();

    static {
        registerAllSerializersAndDeserializers();
    }

    private static void registerAllSerializersAndDeserializers() {
        deserializers.put(Game.class, new GameDeserializer());
        deserializers.put(GameSettings.class, new GameSettingsDeserializer());
        deserializers.put(User.class, new UserDeserializer());
        deserializers.put(UserInLobbyRequest.class, new UserInLobbyRequestDeserializer());
        deserializers.put(DisconnectRequest.class, new UserDisconnectionDeserializer());
        deserializers.put(StartRequest.class, new StartRequestDeserializer());
        deserializers.put(UserWords.class, new UserWordsDeserializer());
        deserializers.put(Evaluation.class, new EvaluationDeserializer());
        deserializers.put(Vote.class, new VoteDeserializer());
        deserializers.put(RoundScores.class, new RoundScoresDeserializer());
        deserializers.put(UserRoundScore.class, new UserScoreDeserializer());
        deserializers.put(Round.class, new RoundDeserializer());


        serializers.put(Game.class, new GameSerializer());
        serializers.put(User.class, new UserSerializer());
        serializers.put(GameSettings.class, new GameSettingsSerializer());
        serializers.put(RoundWords.class, new RoundWordsSerializer());
        serializers.put(UserWords.class, new UserWordsSerializer());
        serializers.put(UserRoundScore.class, new UserScoreSerializer());
        serializers.put(RoundScores.class, new RoundScoresSerializer());
        serializers.put(GameScores.class, new GameScoresSerializer());
        serializers.put(Round.class, new RoundSerializer());
    }

    public static <T> Serializer<T> serializerOf(Class<T> klass) {
        if (!serializers.containsKey(klass)) {
            serializers.keySet().stream()
                    .filter(key -> key.isAssignableFrom(klass))
                    .map(serializers::get)
                    .findAny()
                    .map(klass::cast)
                    .orElseThrow(() -> new IllegalArgumentException("No available serializer for class: " + klass.getName()));
        }
        return (Serializer<T>) serializers.get(klass);
    }

    public static <T> Deserializer<T> deserializerOf(Class<T> klass) {
        if (!deserializers.containsKey(klass)) {
            deserializers.keySet().stream()
                    .filter(key -> key.isAssignableFrom(klass))
                    .map(deserializers::get)
                    .findAny()
                    .map(klass::cast)
                    .orElseThrow(() -> new IllegalArgumentException("No available deserializer for class: " + klass.getName()));
        }
        return (Deserializer<T>) deserializers.get(klass);
    }

    public static <T> T deserializeAs(String string, Class<T> type) throws Exception {
        try {
            return deserializerOf(type).deserialize(string);
        } catch (PresentationException e) {
            throw new Exception("Cannot deserialize " + string + " as " + type.getSimpleName());
        }
    }
}
