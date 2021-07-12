package presentation.serializer.roundSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.game.Game;
import model.game.GameIDSupplier;
import model.round.Round;
import model.round.words.RoundWords;
import presentation.Presentation;
import presentation.serializer.AbstractJsonSerializer;

public class RoundSerializer extends AbstractJsonSerializer<Round> {
    @Override
    protected JsonElement toJsonElement(Round object) {
        var jobj = new JsonObject();
        jobj.addProperty("gameID", object.getGame().getId());
        jobj.add("game", Presentation.serializerOf(Game.class).getJsonElement(object.getGame()));
        jobj.add("roundWords", Presentation.serializerOf(RoundWords.class).getJsonElement(object.getRoundWords()));
        return jobj;
    }
}
