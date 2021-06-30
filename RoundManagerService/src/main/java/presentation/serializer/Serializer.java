package presentation.serializer;

import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.List;

public interface Serializer<T> {
    String serialize(T object);

    default String serializeMany(T... objects) {
        return serializeMany(List.of(objects));
    }

    JsonElement getJsonElement(T object);

    String serializeMany(Collection<? extends T> objects);
}
