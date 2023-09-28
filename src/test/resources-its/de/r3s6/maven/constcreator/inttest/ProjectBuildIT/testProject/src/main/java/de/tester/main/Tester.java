package de.tester.main;
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

import java.io.IOException;
import java.util.ResourceBundle;

import de.tester.constants.messages.Messages;

/**
 * Test class.
 */
public class Tester {

    /**
     * Test Main.
     * @param args command line args
     * @throws IOException if getBundle fails
     */
    public static void main(final String[] args) throws IOException {

        final ResourceBundle props = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

        System.out.println(props.getString(Messages.WELCOME));
        System.out.println(props.getString(Messages._0SET));
        System.out.println(props.getString(Messages.GOODBY));

    }
}
