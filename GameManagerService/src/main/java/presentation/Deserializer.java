package presentation;

import java.util.List;

public interface Deserializer<T> {

    T deserialize(String string);
    List<T> deserializeMany(String string);
}
