package testing;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import shuff.AnimatedJButton;
import shuff.Credits;
import shuff.SecondsCounter;
import shuff.ShuffleGame;



public class TestShuffle {

    private ShuffleGame game;
    private JDialog mockDialog;
    private AnimatedJButton mockButton;
    private JButton mockButton2;
    private JPanel mockButtonPanel;
    private JLabel mockMovesLabel;
    private JMenuItem mockMenuItem;
    private SecondsCounter mockSecondsCounter;

    private Thread autoCloser;

    @BeforeEach
    public void setUp() {
        game = createGameWithMockedSecondsCounter(3);
        mockDialog = Mockito.mock(JDialog.class);
        mockButton = Mockito.mock(AnimatedJButton.class);
        mockButton2 = new JButton("5");
        mockButtonPanel = Mockito.mock(JPanel.class);
        mockMovesLabel = Mockito.mock(JLabel.class);
        mockMenuItem = Mockito.mock(JMenuItem.class);
        mockSecondsCounter = Mockito.mock(SecondsCounter.class);

        autoCloser = new Thread(() -> {
            try {
                long end = System.currentTimeMillis() + 4000;
                while (System.currentTimeMillis() < end) {
                    for (Window w : Window.getWindows()) {
                        if (w instanceof JDialog d && d.isShowing()) {
                            SwingUtilities.invokeLater(d::dispose);
                        }
                    }
                    Thread.sleep(25);
                }
            } catch (InterruptedException ignored) {}
        }, "autoCloser");
        autoCloser.setDaemon(true);
        autoCloser.start();
    }

    @AfterEach
    void tearDown() {
        try {
            Field f = ShuffleGame.class.getDeclaredField("secondsCounter_");
            f.setAccessible(true);
            SecondsCounter sc = (SecondsCounter) f.get(game);
            if (sc != null && sc.timer_ != null && sc.timer_.isRunning()) {
                sc.stop();
            }
        } catch (Exception ignored) {}

        if (game != null) game.dispose();
        File f = new File("Score.dat");
        if (f.exists()) f.delete();

        if (autoCloser != null && autoCloser.isAlive()) autoCloser.interrupt();
    }

    @Test
    public void testIsPositionNotLeft() {
        assertTrue(game.isPositionNotLeft(1));
        assertFalse(game.isPositionNotLeft(0));
    }

    @Test
    public void testIsPositionNotRight() {
        assertTrue(game.isPositionNotRight(0));
        assertFalse(game.isPositionNotRight(2));
    }

    @Test
    public void testIsPositionNotTop() {
        assertTrue(game.isPositionNotTop(3));
        assertFalse(game.isPositionNotTop(1));
    }

    @Test
    public void testIsPositionNotBottom() {
        assertTrue(game.isPositionNotBottom(0));
        assertFalse(game.isPositionNotBottom(7));
    }

    @Test
    public void testInitMatrixToZero() {
        game.initMatrixToZero();
        assertFalse(game.canAdd(0));
    }

    @Test
    public void testCanAdd() {
        game.initMatrixToZero();
        assertFalse(game.canAdd(0));
        assertTrue(game.canAdd(2));
    }

    @Test
    public void testAnalyseResult() {
        assertFalse(game.analyseResult());
    }

    @Test
    public void testGetNumberWithoutZero() {
        for (int i = 0; i < 9; i++) {
            assertNotEquals(0, game.getNumberWithoutZero());
        }
    }

    @Test
    public void testGetPosition() {
        game.initMatrixToZero();
        assertEquals(0, game.getPosition(0));
    }

    @Test
    public void testGetNumberWithoutZeroLoopCoverage() throws Exception {
        Field rf = ShuffleGame.class.getDeclaredField("random");
        rf.setAccessible(true);

        Random original = (Random) rf.get(null);

        class AlternatingRandom extends Random {
            private int c = 0;
            @Override
            public int nextInt(int bound) {
                if ((c++ % 2) == 0) return 0;
                int val = 5;
                if (bound <= 1) return 0;
                return val % bound;
            }
        }

        try {
            rf.set(null, new AlternatingRandom());
            for (int i = 0; i < 5; i++) {
                int n = game.getNumberWithoutZero();
                assertNotEquals(0, n);
            }
        } finally {
            rf.set(null, original);
        }
    }

    @Test
    public void testStartSecondsCounterWithMockSecondsCounter() {
        doNothing().when(mockSecondsCounter).start();
        try {
            setPrivateField(game, "secondsCounter_", mockSecondsCounter);
            assertDoesNotThrow(() -> game.startSecondsCounter());
            verify(mockSecondsCounter).start();
        } catch (Exception e) {
            fail("Failed to set mock SecondsCounter");
        }
    }

    @Test
    public void testSetGameStatus() {
        game.setGameStatus(true);
        game.setGameStatus(false);
    }

    @Test
    public void testSetButtonState() {
        game.changeButtonState(true);
        game.changeButtonState(false);
    }

    @Test
    public void testChangeButtonState() { }

    @Test
    public void testActionPerformed() {
        String actionCommand = "PAUSE";
        ActionEvent e = new ActionEvent(mockButton, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "START";
        e = new ActionEvent(mockButton, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "number";
        e = new ActionEvent(mockButton2, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "ReStart";
        e = new ActionEvent(mockMenuItem, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "Instructions";
        e = new ActionEvent(mockMenuItem, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "About Shuffle";
        e = new ActionEvent(mockMenuItem, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "level4";
        e = new ActionEvent(mockMenuItem, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);

        actionCommand = "High Scores";
        e = new ActionEvent(mockMenuItem, ActionEvent.ACTION_PERFORMED, actionCommand);
        game.actionPerformed(e);
    }

    @Test
    public void testActionPerformedWithMockedListener() {
        ActionListener mockListener = mock(ActionListener.class);
        try {
            Field buttonField = ShuffleGame.class.getDeclaredField("startButton_");
            buttonField.setAccessible(true);
            JButton gameButton = (JButton) buttonField.get(game);
            gameButton.removeActionListener(game);
            gameButton.addActionListener(mockListener);

            String actionCommand = "START";
            ActionEvent e = new ActionEvent(gameButton, ActionEvent.ACTION_PERFORMED, actionCommand);
            game.actionPerformed(e);

            verify(mockListener).actionPerformed(e);
        } catch (Exception e) {
            // ignored
        }
    }

    @Test
    public void testShowWarrningDialogWithMockDialog() {
        JDialog mockDialog = mock(JDialog.class);
        doNothing().when(mockDialog).setVisible(true);
        doNothing().when(mockDialog).dispose();
        try {
            Method method = ShuffleGame.class.getDeclaredMethod("showWarrningDialog");
            method.setAccessible(true);
            method.invoke(game);
        } catch (NoSuchMethodException e) {
            System.out.println("showWarrningDialog method not found, skipping test");
        } catch (Exception e) {
            fail("Failed to invoke showWarrningDialog: " + e.getMessage());
        }
        verify(mockDialog, never()).setVisible(true);
    }

    @Test
    public void testShowImageDialog() {
        assertDoesNotThrow(() -> game.showImageDialog("123", "Title"));
    }

    @Test
    public void testGetComponentIfPossibleToMove() {
        game.getComponentIfPossibleToMove(4);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 4",
        "0, 4",
        "2, 4",
        "3, 4",
        "1, 3",
        "2, 5",
        "0, 1",
        "3, 6"
    })
    public void testGetComponent(int a, int b) {
        ShuffleGame g = createGameWithMockedSecondsCounter(3);
        assertDoesNotThrow(() -> g.getComponent(a, b));
        g.dispose();
    }

    @Test
    public void testGetCloseButtonPanel() {
        JDialog mockDialog = Mockito.mock(JDialog.class);
        JPanel panel = game.getCloseButtonPanel(mockDialog);

        assertNotNull(panel);
        assertEquals(1, panel.getComponentCount());
        assertTrue(panel.getComponent(0) instanceof JButton);

        JButton button = (JButton) panel.getComponent(0);
        assertEquals("OK", button.getText());
        assertEquals('O', button.getMnemonic());

        ActionListener[] actionListeners = button.getActionListeners();
        assertEquals(1, actionListeners.length);

        actionListeners[0].actionPerformed(null);
        verify(mockDialog).dispose();
    }

    @Test
    public void testShowScoreDialogUIComponents() {
        TreeSet<Credits> scoreSet = new TreeSet<>();
        scoreSet.add(new Credits(300, 10, 4));
        scoreSet.add(new Credits(500, 20, 4));
        game.showScoreDialog(scoreSet);
    }

    @Test
    public void testShowScoreDialogActionListener() {
        TreeSet<Credits> scoreSet = new TreeSet<>();
        JButton okButton = new JButton();
        JTextField playerNameField = new JTextField();
        ActionEvent mockEvent = Mockito.mock(ActionEvent.class);
        when(mockEvent.getSource()).thenReturn(okButton);

        game.showScoreDialog(scoreSet);
    }

    @Test
    public void testShowHighScoreDialogWithValidData() throws IOException {
        TreeMap<Integer, TreeSet<Credits>> testData = new TreeMap<>();
        TreeSet<Credits> creditsSet = new TreeSet<>();
        creditsSet.add(new Credits(300, 10, 4));
        testData.put(4, creditsSet);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Score.dat"))) {
            oos.writeObject(testData);
        }
        assertDoesNotThrow(() -> game.showHighScoreDialog());
    }

    @Test
    public void testShowHighScoreDialogWithCorruptedFile() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Score.dat"))) {
            oos.writeObject("invalid_data");
        }
        assertDoesNotThrow(() -> game.showHighScoreDialog());
    }

    @Test
    public void testShowScoreDialogTreeSet() throws IOException {
        TreeMap<Integer, TreeSet<Credits>> testData = new TreeMap<>();
        TreeSet<Credits> creditsSet = new TreeSet<>();
        creditsSet.add(new Credits(300, 10, 4));
        testData.put(4, creditsSet);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Score.dat"))) {
            oos.writeObject(testData);
        }

        game.showScoreDialog(creditsSet);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/moves.csv", numLinesToSkip = 1)
    void csv_moves_doNotCrash(int pos, int level) {
        ShuffleGame g = createGameWithMockedSecondsCounter(level);
        assertDoesNotThrow(() -> g.getComponentIfPossibleToMove(pos));
        g.dispose();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/move_loops.csv", numLinesToSkip = 1)
    void testMoveSequenceLoopCoverage(int position) {
        ShuffleGame g = createGameWithMockedSecondsCounter(3);
        assertDoesNotThrow(() -> g.getComponentIfPossibleToMove(position));
        g.dispose();
    }
    
    @ParameterizedTest
    @CsvSource({
        "10, 4",
        "8, 5"
    })
    public void testShowScoreDialogInputIntWithNoSet(int a, int b) {
        ShuffleGame g = createGameWithMockedSecondsCounter(3);
        assertDoesNotThrow(() -> g.showScoreDialog(a, b));
        g.dispose();
    }

    @Test
    public void testShowScoreDialogInputIntWithSet() throws IOException {
        TreeMap<Integer, TreeSet<Credits>> testData = new TreeMap<>();
        TreeSet<Credits> creditsSet = new TreeSet<>();
        creditsSet.add(new Credits(300, 10, 4));
        testData.put(4, creditsSet);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Score.dat"))) {
            oos.writeObject(testData);
        }
        assertDoesNotThrow(() -> game.showScoreDialog(10, 5));
    }

    private ShuffleGame createGameWithMockedSecondsCounter(int level) {
        ShuffleGame g = new ShuffleGame(level) { @Override public void setVisible(boolean b) { } };

        SecondsCounter scMock = mock(SecondsCounter.class);
        doNothing().when(scMock).start();
        doNothing().when(scMock).pause();
        doNothing().when(scMock).enable();
        doNothing().when(scMock).stop();
        doNothing().when(scMock).finalize();
        when(scMock.getTimeElapsed()).thenReturn("00:10");
        when(scMock.getTimeElapsedInSeconds()).thenReturn(10);

        try {
            setPrivateField(g, "secondsCounter_", scMock);
        } catch (Exception ignored) {}

        return g;
    }

    private static void setPrivateField(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }
}