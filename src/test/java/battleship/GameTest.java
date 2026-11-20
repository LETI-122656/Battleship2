package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(new Fleet());
    }

    @AfterEach
    void tearDown() {
        game = null;
    }

    @Test
    @DisplayName("Verificar inicialização do jogo")
    void constructor() {
        assertNotNull(game);
        assertNotNull(game.getMyFleet());
        assertNotNull(game.getAlienFleet());
        assertNotNull(game.getMyMoves());
        assertNotNull(game.getAlienMoves());

        assertTrue(game.getMyMoves().isEmpty());
        assertTrue(game.getAlienMoves().isEmpty());

        assertEquals(0, game.getInvalidShots());
        assertEquals(0, game.getRepeatedShots());
        assertEquals(0, game.getHits());
        assertEquals(0, game.getSunkShips());
        assertEquals(0, game.getRemainingShips());
    }

    @Test
    @DisplayName("Verificar tiro fora do tabuleiro")
    void fireInvalidPosition() {
        IGame.ShotResult result =
                game.fireSingleShot(new Position(-1, 5), false);

        assertFalse(result.valid());
        assertFalse(result.repeated());
        assertNull(result.ship());
        assertFalse(result.sunk());

        assertEquals(1, game.getInvalidShots());
    }

    @Test
    @DisplayName("Verificar tiro repetido")
    void fireRepeatedPosition() {
        Position position = new Position(2, 3);

        game.fireSingleShot(position, false);
        IGame.ShotResult result = game.fireSingleShot(position, true);

        assertTrue(result.valid());
        assertTrue(result.repeated());
        assertNull(result.ship());

        assertEquals(1, game.getRepeatedShots());
    }

    @Test
    @DisplayName("Verificar tiro na água")
    void fireShotInWater() {
        IGame.ShotResult result =
                game.fireSingleShot(new Position(2, 3), false);

        assertTrue(result.valid());
        assertFalse(result.repeated());
        assertNull(result.ship());
        assertFalse(result.sunk());

        assertEquals(0, game.getHits());
        assertEquals(0, game.getSunkShips());
    }

    @Test
    @DisplayName("Verificar que um tiro ainda não foi realizado")
    void repeatedShotBeforeFiring() {
        assertFalse(game.repeatedShot(new Position(2, 3)));
    }

    @Test
    @DisplayName("Verificar identificação de tiro realizado anteriormente")
    void repeatedShotAfterFiring() {
        List<IPosition> positions = List.of(
                new Position(2, 3),
                new Position(2, 4),
                new Position(2, 5)
        );

        game.fireShots(positions);

        assertTrue(game.repeatedShot(new Position(2, 3)));
        assertFalse(game.repeatedShot(new Position(4, 4)));
    }

    @Test
    @DisplayName("Verificar registo de uma rajada de três tiros")
    void fireShotsValid() {
        List<IPosition> positions = List.of(
                new Position(2, 3),
                new Position(2, 4),
                new Position(2, 5)
        );

        game.fireShots(positions);

        assertEquals(1, game.getAlienMoves().size());
        assertEquals(3, game.getAlienMoves().get(0).getShots().size());
    }

    @Test
    @DisplayName("Verificar registo de duas rajadas")
    void fireTwoMoves() {
        game.fireShots(List.of(
                new Position(1, 1),
                new Position(1, 2),
                new Position(1, 3)
        ));

        game.fireShots(List.of(
                new Position(2, 1),
                new Position(2, 2),
                new Position(2, 3)
        ));

        assertEquals(2, game.getAlienMoves().size());
    }

    @Test
    @DisplayName("Verificar erro quando a rajada tem menos de três tiros")
    void fireShotsTooFew() {
        List<IPosition> positions = List.of(
                new Position(1, 1),
                new Position(1, 2)
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> game.fireShots(positions)
        );

        assertEquals(
                "Must fire exactly 3 shots per move.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Verificar erro quando a rajada tem mais de três tiros")
    void fireShotsTooMany() {
        List<IPosition> positions = List.of(
                new Position(1, 1),
                new Position(1, 2),
                new Position(1, 3),
                new Position(1, 4)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> game.fireShots(positions)
        );
    }

    @Test
    @DisplayName("Verificar tiros repetidos na mesma rajada")
    void fireShotsWithRepeatedPositions() {
        List<IPosition> positions = List.of(
                new Position(2, 3),
                new Position(2, 3),
                new Position(2, 4)
        );

        game.fireShots(positions);

        assertEquals(1, game.getRepeatedShots());
        assertEquals(1, game.getAlienMoves().size());
    }

    @Test
    @DisplayName("Verificar leitura de três posições compactas")
    void readEnemyFireCompact() {
        try (Scanner scanner = new Scanner("A3 B4 C5\n")) {
            String json = game.readEnemyFire(scanner);

            assertNotNull(json);
            assertEquals(1, game.getAlienMoves().size());
        }
    }

    @Test
    @DisplayName("Verificar leitura de três posições com espaços")
    void readEnemyFireWithSpaces() {
        try (Scanner scanner = new Scanner("A 3 B 4 C 5\n")) {
            String json = game.readEnemyFire(scanner);

            assertNotNull(json);
            assertEquals(1, game.getAlienMoves().size());
        }
    }

    @Test
    @DisplayName("Verificar erro quando faltam posições na rajada")
    void readEnemyFireTooFew() {
        try (Scanner scanner = new Scanner("A3 B4\n")) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> game.readEnemyFire(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando uma coluna não tem linha")
    void readEnemyFireIncompletePosition() {
        try (Scanner scanner = new Scanner("A 3 B\n")) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> game.readEnemyFire(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando uma posição tem formato inválido")
    void readEnemyFireInvalidFormat() {
        try (Scanner scanner = new Scanner("ABC B4 C5\n")) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> game.readEnemyFire(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar serialização de tiros em JSON")
    void jsonShotsValid() throws Exception {
        List<IPosition> shots = List.of(
                new Position('A', 3),
                new Position('B', 4),
                new Position('C', 5)
        );

        String json = Game.jsonShots(shots);

        JsonNode root = new ObjectMapper().readTree(json);

        assertTrue(root.isArray());
        assertEquals(3, root.size());

        assertEquals("A", root.get(0).get("row").asText());
        assertEquals(3, root.get(0).get("column").asInt());
    }

    @Test
    @DisplayName("Verificar serialização de lista vazia em JSON")
    void jsonShotsEmpty() throws Exception {
        String json = Game.jsonShots(List.of());

        JsonNode root = new ObjectMapper().readTree(json);

        assertTrue(root.isArray());
        assertEquals(0, root.size());
    }

    @Test
    @DisplayName("Verificar número de navios restantes")
    void getRemainingShips() {
        IFleet fleet = game.getMyFleet();

        Ship ship1 = new Barge(
                Compass.NORTH,
                new Position(1, 1)
        );

        Ship ship2 = new Frigate(
                Compass.EAST,
                new Position(5, 5)
        );

        fleet.addShip(ship1);
        assertEquals(1, game.getRemainingShips());

        fleet.addShip(ship2);
        assertEquals(2, game.getRemainingShips());

        ship2.sink();
        assertEquals(1, game.getRemainingShips());
    }

    @Test
    @DisplayName("Verificar apresentação da mensagem de fim de jogo")
    void over() {
        assertDoesNotThrow(() -> game.over());
    }

    @Test
    @DisplayName("Verificar apresentação do tabuleiro do jogador")
    void printMyBoard() {
        assertDoesNotThrow(
                () -> game.printMyBoard(false, true)
        );
    }

    @Test
    @DisplayName("Verificar apresentação do tabuleiro adversário")
    void printAlienBoard() {
        assertDoesNotThrow(
                () -> game.printAlienBoard(false, false)
        );
    }
}