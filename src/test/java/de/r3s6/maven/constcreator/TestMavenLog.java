package de.r3s6.maven.constcreator;
/*
 * Copyright 2023 Ralf Schandl
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

/**
 * Implementation of Mavens Log that stores the output.
 *
 * @author Ralf Schandl
 */
class TestMavenLog implements Log {

    private List<String> lines = new ArrayList<>();

    public void reset() {
        lines.clear();
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void printOut() {
        for (String string : lines) {
            System.out.println(string);
        }
    }

    public void assertContainsMatching(final String regex) {

        Pattern p = Pattern.compile(regex);

        for (String string : lines) {
            if(p.matcher(string).matches()) {
                return;
            }
        }
        throw new AssertionError("Pattern \"" + regex + "\" not found in output");
    }

    public void assertContainsSubString(final String text) {

        for (String string : lines) {
            if(string.indexOf(text) >= 0) {
                return;
            }
        }
        throw new AssertionError("Substring \"" + text + "\" not found in output");
    }


    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(final CharSequence content, final Throwable error) {
            print("debug", content, error);
    }

    @Override
    public void debug(final CharSequence content) {
        debug(content, null);
    }

    @Override
    public void debug(final Throwable error) {
        debug(null, error);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(final CharSequence content, final Throwable error) {
            print("info", content, error);
    }

    @Override
    public void info(final CharSequence content) {
        info(content, null);
    }

    @Override
    public void info(final Throwable error) {
        info(null, error);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(final CharSequence content, final Throwable error) {
            print("warning", content, error);
    }

    @Override
    public void warn(final CharSequence content) {
        warn(content, null);
    }

    @Override
    public void warn(final Throwable error) {
        warn(null, error);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(final CharSequence content, final Throwable error) {
            print("error", content, error);
    }

    @Override
    public void error(final CharSequence content) {
        error(content, null);
    }

    @Override
    public void error(final Throwable error) {
        error(null, error);
    }

    private void print(final String level, final CharSequence content, final Throwable error) {

        String prefix = "[" + level + "] ";

        if (content != null) {
            lines.add(prefix + content.toString());
        }

        if (error != null) {
            StringWriter sWriter = new StringWriter();
            PrintWriter pWriter = new PrintWriter(sWriter);

            if (content != null) {
                pWriter.print(prefix);
            }

            error.printStackTrace(pWriter);

            for (String string : sWriter.toString().split(System.lineSeparator())) {
                lines.add(string);
            }
        }
    }
}