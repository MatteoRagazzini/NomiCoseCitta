package presentation.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.game.GameScores;
import model.round.RoundScores;
import presentation.Presentation;
import presentation.serializer.AbstractJsonSerializer;

import java.util.function.Predicate;

public class GameScoresSerializer extends AbstractJsonSerializer<GameScores> {
    @Override
    protected JsonElement toJsonElement(GameScores object) {
        var jobj = new JsonObject();
        var jarr = new JsonArray();
       var jround = new JsonArray();
        jobj.addProperty("totalRoundsNumber", object.getScores().size());
        object.getUsersGameScores().forEach((u,gs) -> {
            var j = new JsonObject();
            var a = new JsonArray();
            j.addProperty("userID", u);
            gs.getRoundsTotalScores().forEach(a::add);
            j.add("roundsScores", a);
            j.addProperty("total", gs.getTotal());
            jarr.add(j);
        });
        object.forEach(rs -> jround.add(Presentation.serializerOf(RoundScores.class).getJsonElement(rs)));
        jobj.add("roundScores", jround);
        jobj.add("usersScores", jarr);
        jobj.addProperty("winner", object.getWinner());
        return jobj;
    }
}
