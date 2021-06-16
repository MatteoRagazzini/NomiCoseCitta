import model.game.GameSettings;
import org.junit.jupiter.api.Test;
import presentation.Presentation;

import static org.junit.jupiter.api.Assertions.fail;

public class DeserializerTest {

    @Test void testGameSettingsDeserializer() {
        String jsonSettings = "{ \"numRounds\": 3," +
                "\"roundType\": 0," +
                "\"numUsers\": 4," +
                "\"categories\": [" +
                "\"nomi\"," +
                "\"cose\"," +
                "\"citta\"]}";
        try {
            GameSettings gameSettings = Presentation.deserializeAs(jsonSettings, GameSettings.class);
            System.out.println(gameSettings.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
