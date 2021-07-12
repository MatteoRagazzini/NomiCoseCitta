package presentation.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.request.DisconnectRequest;

public class UserDisconnectionDeserializer extends AbstractJsonDeserializer{
    @Override
    protected Object deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("userAddress") && jobj.get("userAddress").isJsonPrimitive()){
                return new DisconnectRequest(jobj.get("userAddress").getAsString());
            }
        }
        throw new RuntimeException("cannot deserialize disconnection request");
    }
}
