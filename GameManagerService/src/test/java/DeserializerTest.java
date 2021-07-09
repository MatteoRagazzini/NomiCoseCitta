import model.User;
import model.game.Game;
import model.game.GameSettings;
import model.game.GameState;
import model.round.Round;
import model.round.RoundStop;
import model.round.RoundType;
import model.round.words.UserWords;
import org.junit.jupiter.api.Test;
import presentation.Presentation;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DeserializerTest {


    @Test void testGameSettingsDeserializer() {
        GameSettings expectedGameSettings = getExpectedGameSettings();
        try {
            GameSettings gameSettingsDeserialized = Presentation.deserializeAs(getTestSettingJson(), GameSettings.class);
            assertEquals(expectedGameSettings.getCategories(), gameSettingsDeserialized.getCategories());
            assertEquals(expectedGameSettings.getNumberOfRounds(), gameSettingsDeserialized.getNumberOfRounds());
            assertEquals(expectedGameSettings.getNumberOfUsers(), gameSettingsDeserialized.getNumberOfUsers());
            assertEquals(expectedGameSettings.getRoundType(), gameSettingsDeserialized.getRoundType());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test void testUserDeserializer() {
        User expectedUser = new User("Pippo", "testAddress");
        try {
            User userDeserialized = Presentation.deserializeAs(getTestUserJson("Pippo"), User.class);
            assertEquals(expectedUser.getNickname(), userDeserialized.getNickname());
            assertEquals(expectedUser.getAddress(), userDeserialized.getAddress());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test void testGameDeserializer() {
        Game expectedGame = getExpectedGame();
        try {
            Game gameDeserialized = Presentation.deserializeAs(getTestGameJson(), Game.class);
            assertEquals(expectedGame.getId(), gameDeserialized.getId());
            assertEquals(expectedGame.getUsers().size(), gameDeserialized.getUsers().size());
            assertEquals(expectedGame.getState(), gameDeserialized.getState());
            assertEquals(expectedGame.getSettings().getRoundType(), gameDeserialized.getSettings().getRoundType());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test void testUserWordsDeserializer() {
        UserWords expectedWords = getExpectedUserWords("Pippo");

        try {
            UserWords userWordsDeserialized = Presentation.deserializeAs(getTestUserWords("Pippo"), UserWords.class);
            assertEquals(expectedWords.getUserID(), userWordsDeserialized.getUserID());
            assertEquals(expectedWords.getGameID(), userWordsDeserialized.getGameID());
            assertEquals(expectedWords.getWords().size(), userWordsDeserialized.getWords().size());
            assertEquals(expectedWords.getWords().get("nomi"), userWordsDeserialized.getWords().get("nomi"));
            assertEquals(expectedWords.getWords().get("cose"), userWordsDeserialized.getWords().get("cose"));
            assertEquals(expectedWords.getWords().get("citta"), userWordsDeserialized.getWords().get("citta"));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test void testRoundDeserializer() {
        String jsonRound = "{ \"game\": "+getTestGameJson()+"," +
                "\"roundWords\": { \"usersWords\": ["
                +getTestUserWords("Pippo")+", "
                + getTestUserWords("Alice") +"]}}";
        Round expectedRound = new RoundStop(getExpectedGame());
        expectedRound.insertUserWord(getExpectedUserWords("Pippo"));
        expectedRound.insertUserWord(getExpectedUserWords("Alice"));

        try {
            Round roundDeserialized = Presentation.deserializeAs(jsonRound, Round.class);
            assertEquals(expectedRound.getGame().getId(), roundDeserialized.getGame().getId());
            assertEquals(expectedRound.getGame().getUsers().size(), roundDeserialized.getGame().getUsers().size());
            assertEquals(expectedRound.getGame().getState(), roundDeserialized.getGame().getState());
            assertEquals(expectedRound.getGame().getSettings().getRoundType(), roundDeserialized.getGame().getSettings().getRoundType());
            assertEquals(expectedRound.getRoundWords().getUsersWords().size(), roundDeserialized.getRoundWords().getUsersWords().size());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    private Game getExpectedGame(){
        Game expectedGame = new Game("3", getExpectedGameSettings());
        expectedGame.setState(GameState.WAITING);
        expectedGame.addNewUser(new User("Pippo", "testAddress"));
        expectedGame.addNewUser(new User("Alice", "testAddress2"));
        return expectedGame;
    }

    private GameSettings getExpectedGameSettings() {
        return new GameSettings(3, RoundType.STOP,
                Arrays.asList("nomi", "cose", "citta"), 4 );
    }

    private UserWords getExpectedUserWords(String userID){
        UserWords expectedWords = new UserWords(userID, "3");
        expectedWords.insertWord("nomi", "Marta");
        expectedWords.insertWord("cose", "Martello");
        expectedWords.insertWord("citta", "Mantova");
        return expectedWords;
    }

    private String getTestSettingJson(){
        return "{ \"numRounds\": 3," +
                "\"roundsType\": stop," +
                "\"numUsers\": 4," +
                "\"categories\": [" +
                "\"nomi\"," +
                "\"cose\"," +
                "\"citta\"]}";
    }

    private String getTestUserJson(String nickname){
        return  "{ \"nickname\": "+nickname+"," +
                "\"address\": testAddress }";
    }

    private String getTestGameJson(){
        return "{ \"gameID\": 3," +
                "\"gameState\": WAITING," +
                "\"settings\": " + getTestSettingJson() + ","+
                "\"users\": [" +
                getTestUserJson("Pippo") +
                "," + getTestUserJson("Alice") +"]}";
    }

    private String getTestUserWords(String userID){
        return  "{ \"userID\": "+userID+"," +
                "\"gameID\": 3," +
                "\"nomi\": Marta," +
                "\"cose\": Martello," +
                "\"citta\": Mantova}";
    }


}
