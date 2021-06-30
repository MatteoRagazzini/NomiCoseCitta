package presentation.serializer.roundSerializer;

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
//        var tots = new JsonArray();
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
//        object.forEach(rs -> jarr.add(Presentation.serializerOf(RoundScores.class).getJsonElement(rs)));
        jobj.add("usersScores", jarr);
//        object.getTotals().forEach((u,t)-> {
//            var j = new JsonObject();
//            j.addProperty("userID", u);
//            j.addProperty("total", t);
//            tots.add(j);
//        });
        jobj.addProperty("winner", object.getWinner());
//        jobj.add("totals", tots);
        return jobj;
    }
}
