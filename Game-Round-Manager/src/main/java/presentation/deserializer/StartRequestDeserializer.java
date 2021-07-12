package presentation.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.request.StartRequest;

public class StartRequestDeserializer extends AbstractJsonDeserializer{
    @Override
    protected Object deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive()){
                return new StartRequest(jobj.get("gameID").getAsString());
            }
        }

        throw new RuntimeException("cannot deserialize start game request");
    }
}
