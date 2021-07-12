package presentation.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.request.JoinRequest;

public class UserInLobbyRequestDeserializer extends AbstractJsonDeserializer{
    @Override
    protected Object deserializeJson(JsonElement jsonElement) {
        var req = new JoinRequest();
        if(jsonElement.isJsonObject()){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("userID") && jobj.get("userID").isJsonPrimitive()){
                req.setUserID(jobj.get("userID").getAsString());
            }
            if(jobj.has("userAddress") && jobj.get("userAddress").isJsonPrimitive()){
                req.setUserAddress(jobj.get("userAddress").getAsString());
            }
            if(jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive()){
                req.setGameID(jobj.get("gameID").getAsString());
            }
        }
        return req;
    }
}
