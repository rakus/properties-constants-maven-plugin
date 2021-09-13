package de.tester.constants.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

class MessagesTest {

    @Test
    void testConstants() {
        assertEquals("welcome", Messages.WELCOME);
        assertEquals("0set", Messages._0SET);
        assertEquals("goodby", Messages.GOODBY);
    }

    @Test
    void testPropertiesFilenameName() {
        assertEquals("messages/messages", Messages.getBundleName());
    }

    @Test
    void testPropertyLoad() throws IOException {
        final ResourceBundle bdl = Messages.loadBundle(Locale.CHINESE);
        assertEquals("Welcome to this test", bdl.getString(Messages.WELCOME));
        assertEquals("All zero", bdl.getString(Messages._0SET));
        assertEquals("Thanks for testing", bdl.getString(Messages.GOODBY));

        final ResourceBundle bdlDe = Messages.loadBundle(Locale.GERMAN);
        assertEquals("Willkommen zu diesem Test", bdlDe.getString(Messages.WELCOME));
        assertEquals("Alles Null", bdlDe.getString(Messages._0SET));
        assertEquals("Danke, dass Sie getestet haben", bdlDe.getString(Messages.GOODBY));
    }

}
