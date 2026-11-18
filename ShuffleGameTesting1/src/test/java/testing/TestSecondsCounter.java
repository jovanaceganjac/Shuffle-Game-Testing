package testing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;  
import javax.swing.*;     
import java.awt.event.ActionEvent;

import shuff.SecondsCounter;
import shuff.ShuffleGame;

public class TestSecondsCounter {

    @Test
    void testInitCounter() {
        SecondsCounter counter = new SecondsCounter();
        counter.initCounter();
        assertEquals(0, counter.getTimeElapsedInSeconds(), "Counter should start at 0");
    }

    @Test
    void testGetMinitueString() {
        SecondsCounter counter = new SecondsCounter();
        assertEquals("00 : 00", counter.getMinitueString(0));
        assertEquals("00 : 09", counter.getMinitueString(9));
        assertEquals("01 : 00", counter.getMinitueString(60));
        assertEquals("01 : 05", counter.getMinitueString(65));
        assertEquals("10 : 10", counter.getMinitueString(610));
    }

    @Test
    void testGetTimeElapsed() {
        SecondsCounter counter = new SecondsCounter();
        counter.counter = 125; // 2 minutes 5 seconds
        assertEquals("02 Minutes & 05 Seconds ", counter.getTimeElapsed());
    }

    @Test
    void testActionPerformedIncrementsCounter() {
        SecondsCounter counter = new SecondsCounter();
        JLabel label = new JLabel();
        counter.setLabel(label);
        counter.counter = 0;
        counter.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        assertEquals(1, counter.getTimeElapsedInSeconds());
        assertEquals("00 : 01", label.getText());
    }

    @Test
    void testStartSetsLabelAndStartsTimer() {
        SecondsCounter counter = new SecondsCounter();
        JLabel label = new JLabel();
        counter.setLabel(label);
        counter.start();
        assertEquals(ShuffleGame.INIT_TIME, label.getText(), "Label should be initialized with INIT_TIME");
        assertTrue(counter.timer_.isRunning(), "Timer should be running after start()");
    }

    @Test
    void testPauseStopsTimer() {
        SecondsCounter counter = new SecondsCounter();
        JLabel label = new JLabel();
        counter.setLabel(label);
        counter.start();
        counter.pause();
        assertFalse(counter.timer_.isRunning(), "Timer should be stopped after pause()");
    }

    @Test
    void testStopResetsCounterAndStopsTimer() {
        SecondsCounter counter = new SecondsCounter();
        JLabel label = new JLabel();
        counter.setLabel(label);
        counter.start();
        counter.counter = 10;
        counter.stop();
        assertEquals(0, counter.counter, "Counter should be reset to 0 after stop()");
        assertFalse(counter.timer_.isRunning(), "Timer should be stopped after stop()");
    }

    @Test
    void testReStartResetsAndStartsTimer() {
        SecondsCounter counter = new SecondsCounter();
        JLabel label = new JLabel();
        counter.setLabel(label);
        counter.counter = 50;
        counter.reStart();
        assertEquals(0, counter.counter, "Counter should be reset to 0 after reStart()");
        assertTrue(counter.timer_.isRunning(), "Timer should be running after reStart()");
        assertEquals(ShuffleGame.INIT_TIME, label.getText(), "Label should be reset after reStart()");
    }

    @Test
    void testEnableStartsTimer() {
        SecondsCounter counter = new SecondsCounter();
        counter.timer_.stop();
        counter.enable();
        assertTrue(counter.timer_.isRunning(), "Timer should be running after enable()");
    }

    @Test
    void testFinalizeSetsTimerNull() {
        SecondsCounter counter = new SecondsCounter();
        counter.finalize();
        assertNull(counter.timer_, "Timer should be null after finalize()");
    }
}
