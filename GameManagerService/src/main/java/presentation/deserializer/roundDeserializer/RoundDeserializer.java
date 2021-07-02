package presentation.deserializer.roundDeserializer;

import com.google.gson.JsonElement;
import model.game.Game;
import model.round.Round;
import model.round.RoundStop;
import model.round.RoundTimer;
import model.round.RoundType;
import model.round.words.UserWords;
import presentation.Presentation;
import presentation.deserializer.AbstractJsonDeserializer;
import presentation.serializer.AbstractJsonSerializer;

public class RoundDeserializer extends AbstractJsonDeserializer<Round> {
    @Override
    protected Round deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            var jobj = jsonElement.getAsJsonObject();
            if(jobj.has("game")){
                try {
                    var game = Presentation.deserializeAs(jobj.getAsJsonObject("game").toString(), Game.class);
                    var round = game.getSettings().getRoundType()== RoundType.STOP?
                            new RoundStop(game) :
                            new RoundTimer(game);
                    if(jobj.has("roundWords")){
                        jobj.getAsJsonObject("roundWords")
                                .getAsJsonArray("usersWords")
                                .forEach(uw -> {
                                    try {
                                        round.getRoundWords().insertUserWords(Presentation.deserializeAs(uw.toString(), UserWords.class));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                        return round;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
        throw new RuntimeException("Cannot deserialize as Round");
    }
}
