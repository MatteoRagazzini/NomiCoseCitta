package presentation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.GameSettings;
import model.JoinRequest;
import model.User;

public class JoinRequestDeserializer extends AbstractJsonDeserializer{
    @Override
    protected Object deserializeJson(JsonElement jsonElement) {
        var req = new JoinRequest();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("userID") && jobj.get("userID").isJsonPrimitive()){
                req.setUser(new User(jobj.get("userID").getAsString()));
            }
            if(jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive()){
                req.setGameID(jobj.get("gameID").getAsString());
            }
        }
        return req;
    }
}
