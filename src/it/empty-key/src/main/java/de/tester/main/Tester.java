package de.tester.main;

import java.io.IOException;
import java.util.ResourceBundle;

import de.tester.constants.messages.Messages;

public class Tester {

    public static void main(final String[] args) throws IOException {

        final ResourceBundle props = Messages.loadBundle();

        System.out.println(props.getString(Messages.WELCOME));
        System.out.println(props.getString(Messages._0SET));
        System.out.println(props.getString(Messages.GOODBY));

    }
}
