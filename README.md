# Shuffle Game Testing

Unit testing of a Java Swing sliding-puzzle game (*Shuffle Game*), done for the
*Software Testing* course at ETF Belgrade (2024/25). The game itself is third-party
code (author: P. Sakthivel); this repository contains the tests written for it.

## Scope

The tested classes are:

- **`ShuffleGame`** – board initialisation, tile positions and moves, result checking, game and button state, dialogs (warning, image, score and high score, including a corrupted score file)
- **`SecondsCounter`** – starting, pausing, stopping and reading the game timer
- **`AnimatedJButton`** – the animated tile button
- **`Credits`** – the credits dialog

## Approach

- **JUnit 5** unit tests (`@Test`) for each class
- **Parameterized tests** with `@CsvSource` and `@CsvFileSource` (move sequences and component states in `src/test/resources/*.csv`)
- **Mockito** for isolating dependencies:
  - `SecondsCounter` is mocked inside the game (calls to `start()`, `pause()`, `stop()` ... are verified), with a helper `createGameWithMockedSecondsCounter(...)` reused across tests
  - `JDialog` is mocked to verify that the OK button calls `dispose()`
  - `ActionListener` is mocked to verify that `actionPerformed(e)` is called on the START action

  More details (in Serbian): [dz2_tp_mockito.txt](dz2_tp_mockito.txt)
- **JaCoCo** for measuring code coverage

## Results

Code coverage from the JaCoCo report in [`coverage/`](coverage/index.html):

| | Instructions | Branches |
|:---:|:---:|:---:|
| Total | 90% | 77% |
| `src/main/java` | 89% | 80% |

## Run

```bash
cd ShuffleGameTesting1
mvn test
```

The JaCoCo report is generated in `target/jacoco-reports/`. The tests create Swing
components, so they need a graphical environment (in a headless environment they
fail with `HeadlessException`).

## Structure

```
ShuffleGameTesting1/
  src/main/java/shuff/      tested classes (ShuffleGame, SecondsCounter, AnimatedJButton, Credits)
  src/test/java/testing/    tests (TestShuffle, TestSecondsCounter, TestAnimatedJButton, TestCredits)
  src/test/resources/       CSV input for parameterized tests
  pom.xml
coverage/                   JaCoCo HTML report
*.jar                       Mockito, Byte Buddy and Objenesis libraries
```

## Tech

Java 16 · Maven · JUnit 5 · Mockito · JaCoCo
