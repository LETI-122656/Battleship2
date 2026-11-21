package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;

class MoveTest {

    @Test
    @DisplayName("Verificar se getNumber devolve o número correto da jogada")
    void getNumber() {
        Move move = new Move(5, List.of(), List.of());

        assertEquals(5, move.getNumber());
    }

    @Test
    @DisplayName("Verificar se getShots devolve a lista de tiros")
    void getShots() {
        List<IPosition> shots = List.of();

        Move move = new Move(1, shots, List.of());

        assertSame(shots, move.getShots());
    }

    @Test
    @DisplayName("Verificar se getShotResults devolve os resultados dos tiros")
    void getShotResults() {
        List<IGame.ShotResult> results = List.of();

        Move move = new Move(1, List.of(), results);

        assertSame(results, move.getShotResults());
    }

    @Test
    @DisplayName("Verificar a representação textual da jogada")
    void testToString() {
        Move move = new Move(3, List.of(), List.of());

        String expected = "Move{number=3, shots=0, results=0}";

        assertEquals(expected, move.toString());
    }

    @Test
    @DisplayName("Verificar a representação textual com listas preenchidas")
    void testToStringWithShots() {
        List<IPosition> shots = List.of(
                new Position(1, 1),
                new Position(2, 2)
        );

        Move move = new Move(2, shots, List.of());

        assertEquals(
                "Move{number=2, shots=2, results=0}",
                move.toString()
        );
    }

    @Test
    @DisplayName("Processar uma jogada sem resultados e sem modo verbose")
    void processEnemyFire() {
        Move move = new Move(1, List.of(), List.of());

        String json = move.processEnemyFire(false);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\""));
        assertTrue(json.contains("\"repeatedShots\""));
        assertTrue(json.contains("\"missedShots\""));
        assertTrue(json.contains("\"outsideShots\""));
        assertTrue(json.contains("\"sunkBoats\""));
        assertTrue(json.contains("\"hitsOnBoats\""));
    }

    @Test
    @DisplayName("Processar uma jogada vazia com modo verbose")
    void processEnemyFireVerbose() {
        Move move = new Move(2, List.of(), List.of());

        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\""));
        assertTrue(json.contains("\"outsideShots\""));
    }

    @Test
    @DisplayName("Verificar que duas jogadas podem ter números diferentes")
    void differentMoveNumbers() {
        Move move1 = new Move(1, List.of(), List.of());
        Move move2 = new Move(10, List.of(), List.of());

        assertEquals(1, move1.getNumber());
        assertEquals(10, move2.getNumber());
        assertNotEquals(move1.getNumber(), move2.getNumber());
    }

    @Test
    @DisplayName("Verificar uma jogada com número zero")
    void zeroMoveNumber() {
        Move move = new Move(0, List.of(), List.of());

        assertEquals(0, move.getNumber());
    }

    @Test
    @DisplayName("Verificar que as listas de tiros e resultados estão vazias")
    void emptyLists() {
        Move move = new Move(1, List.of(), List.of());

        assertTrue(move.getShots().isEmpty());
        assertTrue(move.getShotResults().isEmpty());
    }

    @Test
    @DisplayName("Verificar um tiro válido na água")
    void validShotInWater() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, null, false);

        Move move = new Move(1, List.of(), List.of(result));

        String json = move.processEnemyFire(false);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(1, data.get("validShots").asInt());
        assertEquals(1, data.get("missedShots").asInt());
        assertEquals(0, data.get("repeatedShots").asInt());
        assertEquals(2, data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar um tiro repetido")
    void repeatedShot() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(true, true, null, false);

        Move move = new Move(1, List.of(), List.of(result));

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(2, data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar um tiro inválido")
    void invalidShot() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(false, false, null, false);

        Move move = new Move(1, List.of(), List.of(result));

        String json = move.processEnemyFire(false);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(0, data.get("missedShots").asInt());
        assertEquals(3, data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar três tiros válidos na água")
    void threeValidShots() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, null, false);

        Move move = new Move(
                1,
                List.of(),
                List.of(result, result, result)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(3, data.get("validShots").asInt());
        assertEquals(3, data.get("missedShots").asInt());
        assertEquals(0, data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar combinação de tiros válidos e repetidos")
    void mixedShots() throws Exception {

        IGame.ShotResult valid =
                new IGame.ShotResult(true, false, null, false);

        IGame.ShotResult repeated =
                new IGame.ShotResult(true, true, null, false);

        Move move = new Move(
                1,
                List.of(),
                List.of(valid, repeated, valid)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(2, data.get("validShots").asInt());
        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(2, data.get("missedShots").asInt());
        assertEquals(0, data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar três tiros repetidos")
    void threeRepeatedShots() throws Exception {

        IGame.ShotResult repeated =
                new IGame.ShotResult(true, true, null, false);

        Move move = new Move(
                1,
                List.of(),
                List.of(repeated, repeated, repeated)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(3, data.get("repeatedShots").asInt());
        assertEquals(0, data.get("outsideShots").asInt());
    }
    @Test
    @DisplayName("Verificar um tiro que acerta num barco sem o afundar")
    void shotHitsBoat() throws Exception {

        IShip ship = mock(IShip.class);
        when(ship.getCategory()).thenReturn("Caravel");

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, ship, false);

        Move move = new Move(1, List.of(), List.of(result));

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(1, data.get("validShots").asInt());
        assertEquals(0, data.get("missedShots").asInt());
        assertEquals(1, data.get("hitsOnBoats").size());
        assertEquals("Caravel",
                data.get("hitsOnBoats").get(0).get("type").asText());
        assertEquals(1,
                data.get("hitsOnBoats").get(0).get("hits").asInt());
    }

    @Test
    @DisplayName("Verificar um tiro que afunda um barco")
    void shotSinksBoat() throws Exception {

        IShip ship = mock(IShip.class);
        when(ship.getCategory()).thenReturn("Caravel");

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, ship, true);

        Move move = new Move(1, List.of(), List.of(result));

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(1, data.get("validShots").asInt());
        assertEquals(1, data.get("sunkBoats").size());
        assertEquals("Caravel",
                data.get("sunkBoats").get(0).get("type").asText());
        assertEquals(1,
                data.get("sunkBoats").get(0).get("count").asInt());
        assertEquals(0, data.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar vários acertos no mesmo tipo de barco")
    void multipleHitsOnBoat() throws Exception {

        IShip ship = mock(IShip.class);
        when(ship.getCategory()).thenReturn("Caravel");

        IGame.ShotResult hit =
                new IGame.ShotResult(true, false, ship, false);

        Move move = new Move(
                1,
                List.of(),
                List.of(hit, hit, hit)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(3, data.get("validShots").asInt());
        assertEquals(1, data.get("hitsOnBoats").size());
        assertEquals(3,
                data.get("hitsOnBoats").get(0).get("hits").asInt());
    }

    @Test
    @DisplayName("Verificar dois barcos afundados da mesma categoria")
    void multipleSunkBoats() throws Exception {

        IShip ship1 = mock(IShip.class);
        IShip ship2 = mock(IShip.class);

        when(ship1.getCategory()).thenReturn("Caravel");
        when(ship2.getCategory()).thenReturn("Caravel");

        IGame.ShotResult sunk1 =
                new IGame.ShotResult(true, false, ship1, true);

        IGame.ShotResult sunk2 =
                new IGame.ShotResult(true, false, ship2, true);

        Move move = new Move(
                1,
                List.of(),
                List.of(sunk1, sunk2)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(2, data.get("validShots").asInt());
        assertEquals(1, data.get("sunkBoats").size());
        assertEquals(2,
                data.get("sunkBoats").get(0).get("count").asInt());
    }

    @Test
    @DisplayName("Verificar combinação de acertos, tiros na água e repetidos")
    void mixedShotsWithBoat() throws Exception {

        IShip ship = mock(IShip.class);
        when(ship.getCategory()).thenReturn("Caravel");

        IGame.ShotResult hit =
                new IGame.ShotResult(true, false, ship, false);

        IGame.ShotResult water =
                new IGame.ShotResult(true, false, null, false);

        IGame.ShotResult repeated =
                new IGame.ShotResult(true, true, null, false);

        Move move = new Move(
                1,
                List.of(),
                List.of(hit, water, repeated)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(2, data.get("validShots").asInt());
        assertEquals(1, data.get("missedShots").asInt());
        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(0, data.get("outsideShots").asInt());
        assertEquals(1, data.get("hitsOnBoats").size());
    }
    @Test
    @DisplayName("Verificar dois tiros repetidos e um tiro válido")
    void twoRepeatedShotsAndOneValidShot() throws Exception {

        IGame.ShotResult repeated =
                new IGame.ShotResult(true, true, null, false);

        IGame.ShotResult valid =
                new IGame.ShotResult(true, false, null, false);

        Move move = new Move(
                1,
                List.of(),
                List.of(repeated, repeated, valid)
        );

        String json = move.processEnemyFire(true);

        JsonNode data = new ObjectMapper().readTree(json);

        assertEquals(1, data.get("validShots").asInt());
        assertEquals(2, data.get("repeatedShots").asInt());
        assertEquals(1, data.get("missedShots").asInt());
        assertEquals(0, data.get("outsideShots").asInt());
    }
}