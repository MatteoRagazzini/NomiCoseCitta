package presentation;


import model.User;
import model.game.Game;
import model.game.GameSettings;
import model.request.DisconnectRequest;
import model.request.UserInLobbyRequest;
import model.request.StartRequest;
import model.round.RoundWords;
import model.round.UserWords;
import presentation.deserializer.*;
import presentation.deserializer.roundDeserializer.UserWordsDeserializer;
import presentation.serializer.GameSettingsSerializer;
import presentation.serializer.GameSerializer;
import presentation.serializer.Serializer;
import presentation.serializer.UserSerializer;
import presentation.serializer.roundSerializer.RoundWordsSerializer;
import presentation.serializer.roundSerializer.UserWordsSerializer;

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

        serializers.put(Game.class, new GameSerializer());
        serializers.put(User.class, new UserSerializer());
        serializers.put(GameSettings.class, new GameSettingsSerializer());
        serializers.put(RoundWords.class, new RoundWordsSerializer());
        serializers.put(UserWords.class, new UserWordsSerializer());
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
