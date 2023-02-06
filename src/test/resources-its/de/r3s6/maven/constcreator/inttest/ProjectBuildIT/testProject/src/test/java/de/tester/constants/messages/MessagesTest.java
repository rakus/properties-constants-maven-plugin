package de.tester.constants.messages;
/*
 * Copyright 2021 Ralf Schandl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        assertEquals("messages/messages", Messages.BUNDLE_NAME);
    }

    @Test
    void testPropertyLoad() throws IOException {
        final ResourceBundle bdl = ResourceBundle.getBundle(Messages.BUNDLE_NAME, Locale.CHINESE);
        assertEquals("Welcome to this test", bdl.getString(Messages.WELCOME));
        assertEquals("All zero", bdl.getString(Messages._0SET));
        assertEquals("Thanks for testing", bdl.getString(Messages.GOODBY));

        final ResourceBundle bdlDe = ResourceBundle.getBundle(Messages.BUNDLE_NAME, Locale.GERMAN);
        assertEquals("Willkommen zu diesem Test", bdlDe.getString(Messages.WELCOME));
        assertEquals("Alles Null", bdlDe.getString(Messages._0SET));
        assertEquals("Danke, dass Sie getestet haben", bdlDe.getString(Messages.GOODBY));
    }

}
