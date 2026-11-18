package testing;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import shuff.AnimatedJButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

public class TestAnimatedJButton {

    @BeforeAll
    static void setUp() {
        // da Swing ne pokušava da otvara prozor tokom testiranja
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void testConstructor() {
        AnimatedJButton btn = new AnimatedJButton("Hello", 3);
        assertEquals("Hello", btn.getText());
    }

    @Test
    void testSetLabelNameAndActionPerformed() {
        AnimatedJButton btn = new AnimatedJButton("Hello", 2);
        btn.setLabelName("World");

        btn.actionPerformed(null); // simuliramo tick timera
        assertTrue(btn.getText().contains("W"));
    }

    @Test
    void testGetStringDifferentCases() {
        assertEquals("H e l l o", AnimatedJButton.getString("Hello", 1));
        assertEquals("H  e  l  l  o", AnimatedJButton.getString("Hello", 2));
        assertEquals("A", AnimatedJButton.getString("A", 5));  // samo jedno slovo
        assertEquals("", AnimatedJButton.getString("", 3));    // prazan string
    }

    @Test
    void testActionPerformedCyclesForwardAndBackward() {
        AnimatedJButton btn = new AnimatedJButton("Hi", 1);

        // dovoljno puta da i++ pređe u else granu i vrati se nazad
        for (int t = 0; t < 5; t++) {
            btn.actionPerformed(null);
        }

        assertNotNull(btn.getText());
        assertTrue(btn.getText().contains("H"));
    }

    @Test
    void testMouseListenerEmptyMethods() {
        AnimatedJButton btn = new AnimatedJButton("Test", 1);

        MouseEvent click = new MouseEvent(btn, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);
        MouseEvent press = new MouseEvent(btn, MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);
        MouseEvent release = new MouseEvent(btn, MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);

        assertDoesNotThrow(() -> btn.mouseClicked(click));
        assertDoesNotThrow(() -> btn.mousePressed(press));
        assertDoesNotThrow(() -> btn.mouseReleased(release));
    }

    
    @Test
    void testMouseEnterAndExit() {
        AnimatedJButton btn = new AnimatedJButton("Test", 1);

        MouseEvent enter = new MouseEvent(btn, MouseEvent.MOUSE_ENTERED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);
        btn.mouseEntered(enter);

        assertEquals(Cursor.HAND_CURSOR, btn.getCursor().getType());

        MouseEvent exit = new MouseEvent(btn, MouseEvent.MOUSE_EXITED,
                System.currentTimeMillis(), 0, 0, 0, 1, false);
        btn.mouseExited(exit);

        assertEquals(Cursor.DEFAULT_CURSOR, btn.getCursor().getType());
        assertEquals("Test", btn.getText()); // resetuje se na originalni tekst
    }
}
