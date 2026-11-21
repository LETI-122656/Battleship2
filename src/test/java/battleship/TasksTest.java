package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TasksTest {

    @Test
    @DisplayName("Verificar se o menu de ajuda apresenta os comandos disponíveis")
    void menuHelp() {

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(
                    new PrintStream(output, true, StandardCharsets.UTF_8)
            );

            Tasks.menuHelp();

        } finally {
            System.setOut(originalOut);
        }

        String text = output.toString(StandardCharsets.UTF_8);

        assertTrue(text.contains("AJUDA DO MENU"));
        assertTrue(text.contains("gerafrota"));
        assertTrue(text.contains("lefrota"));
        assertTrue(text.contains("estado"));
        assertTrue(text.contains("mapa"));
        assertTrue(text.contains("rajada"));
        assertTrue(text.contains("simula"));
        assertTrue(text.contains("tiros"));
        assertTrue(text.contains("desisto"));
    }

    @Test
    @DisplayName("Verificar leitura de uma posição numérica")
    void readPosition() {

        try (Scanner scanner = new Scanner("3 5")) {

            Position position = Tasks.readPosition(scanner);

            assertNotNull(position);
            assertEquals(new Position(3, 5), position);
        }
    }

    @Test
    @DisplayName("Verificar leitura de uma posição com valores zero")
    void readPositionWithZeros() {

        try (Scanner scanner = new Scanner("0 0")) {

            Position position = Tasks.readPosition(scanner);

            assertNotNull(position);
            assertEquals(new Position(0, 0), position);
        }
    }

    @Test
    @DisplayName("Verificar erro quando faltam coordenadas numéricas")
    void readPositionMissingCoordinate() {

        try (Scanner scanner = new Scanner("3")) {

            assertThrows(
                    java.util.NoSuchElementException.class,
                    () -> Tasks.readPosition(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando uma coordenada não é um número")
    void readPositionInvalidNumber() {

        try (Scanner scanner = new Scanner("A B")) {

            assertThrows(
                    java.util.InputMismatchException.class,
                    () -> Tasks.readPosition(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar leitura de posição clássica compacta")
    void readClassicPositionCompact() {

        try (Scanner scanner = new Scanner("A3")) {

            IPosition position = Tasks.readClassicPosition(scanner);

            assertNotNull(position);
            assertEquals(new Position('A', 3), position);
        }
    }

    @Test
    @DisplayName("Verificar leitura de posição clássica com espaço")
    void readClassicPositionWithSpace() {

        try (Scanner scanner = new Scanner("B 5")) {

            IPosition position = Tasks.readClassicPosition(scanner);

            assertNotNull(position);
            assertEquals(new Position('B', 5), position);
        }
    }

    @Test
    @DisplayName("Verificar leitura de posição clássica em minúsculas")
    void readClassicPositionLowercase() {

        try (Scanner scanner = new Scanner("c7")) {

            IPosition position = Tasks.readClassicPosition(scanner);

            assertNotNull(position);
            assertEquals(new Position('C', 7), position);
        }
    }

    @Test
    @DisplayName("Verificar leitura de posição clássica com dois algarismos")
    void readClassicPositionTwoDigits() {

        try (Scanner scanner = new Scanner("D10")) {

            IPosition position = Tasks.readClassicPosition(scanner);

            assertNotNull(position);
            assertEquals(new Position('D', 10), position);
        }
    }

    @Test
    @DisplayName("Verificar erro quando não é introduzida nenhuma posição")
    void readClassicPositionEmpty() {

        try (Scanner scanner = new Scanner("")) {

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );

            assertEquals(
                    "Nenhuma posição válida encontrada!",
                    exception.getMessage()
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando o formato da posição é inválido")
    void readClassicPositionInvalid() {

        try (Scanner scanner = new Scanner("123ABC")) {

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );

            assertEquals(
                    "Formato inválido. Use 'A3', 'A 3' ou similar.",
                    exception.getMessage()
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando a posição contém apenas uma letra")
    void readClassicPositionOnlyLetter() {

        try (Scanner scanner = new Scanner("A")) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando a posição contém caracteres especiais")
    void readClassicPositionSpecialCharacters() {

        try (Scanner scanner = new Scanner("@5")) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando a posição contém apenas números")
    void readClassicPositionOnlyNumbers() {

        try (Scanner scanner = new Scanner("123")) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando a posição tem duas letras")
    void readClassicPositionTwoLetters() {

        try (Scanner scanner = new Scanner("AB3")) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );
        }
    }

    @Test
    @DisplayName("Verificar erro quando falta a linha numa posição com espaço")
    void readClassicPositionMissingRow() {

        try (Scanner scanner = new Scanner("B X")) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> Tasks.readClassicPosition(scanner)
            );
        }
    }
    @Test
    @DisplayName("Verificar se o menu termina quando o utilizador desiste")
    void menuDesistir() {

        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(
                    new java.io.ByteArrayInputStream(
                            "desisto\n".getBytes(StandardCharsets.UTF_8)
                    )
            );

            System.setOut(
                    new PrintStream(output, true, StandardCharsets.UTF_8)
            );

            Tasks.menu();

            String text = output.toString(StandardCharsets.UTF_8);

            assertTrue(text.contains("Bons ventos!"));

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}