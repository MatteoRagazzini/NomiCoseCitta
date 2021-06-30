package presentation.serializer.roundSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.round.RoundScores;
import model.round.UserScore;
import presentation.Presentation;
import presentation.serializer.AbstractJsonSerializer;

public class RoundScoresSerializer extends AbstractJsonSerializer<RoundScores> {
    @Override
    protected JsonElement toJsonElement(RoundScores object) {
        JsonObject jobj = new JsonObject();
        JsonArray jArrCategories  = new JsonArray();
        JsonArray usersScores = new JsonArray();
        object.getCategories().forEach(jArrCategories::add);
        jobj.add("categories", jArrCategories);
        object.getUserScores().forEach(u -> usersScores.add(Presentation.serializerOf(UserScore.class).getJsonElement(u)));
        jobj.add("usersScores", usersScores);
        return jobj;
    }
}
