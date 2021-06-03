package presentation;


import model.Game;
import model.GameSettings;
import model.request.JoinRequest;
import model.request.StartRequest;

import java.util.HashMap;
import java.util.Map;

public class Presentation {
    private static final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
    private static final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();

    // this is a "static initializer": it is called upon class loading
    static {
        registerAllSerializersAndDeserializers();
    }

    private static void registerAllSerializersAndDeserializers() {
        deserializers.put(Game.class, new GameDeserializer());
        deserializers.put(GameSettings.class, new GameSettingsDeserializer());
        deserializers.put(JoinRequest.class, new JoinRequestDeserializer());
        deserializers.put(StartRequest.class, new StartRequestDeserializer());
        serializers.put(Game.class, new GameStatusSerializer());
        serializers.put(GameSettings.class, new GameSettingsSerializer());
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
