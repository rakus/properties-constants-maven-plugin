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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * Handler for Freemarker templates.
 *
 * @author Ralf Schandl
 */
public class TemplateHandler {

    private static final Version FREEMARKER_VERSION = new Version("2.3.32");

    private Configuration configuration = new Configuration(FREEMARKER_VERSION);

    TemplateHandler(final File projectBasedir, final String encoding) {

        configuration.setObjectWrapper(new DefaultObjectWrapper(FREEMARKER_VERSION));
        configuration.setDefaultEncoding(encoding);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setIncompatibleImprovements(FREEMARKER_VERSION);

        final ClassTemplateLoader classTl = new ClassTemplateLoader(getClass(), "/");
        final FileTemplateLoader projectTl;
        final FileTemplateLoader absolutTl;
        try {
            projectTl = new FileTemplateLoader(projectBasedir);
            absolutTl = new FileTemplateLoader(new File("/"));
        } catch (IOException e) {
            // should not be reached. The project basedir and the root dir should always
            // exist.
            throw new IllegalStateException(e.getMessage(), e);
        }

        final MultiTemplateLoader multiTl = new MultiTemplateLoader(
                new TemplateLoader[] { classTl, projectTl, absolutTl });
        configuration.setTemplateLoader(multiTl);
    }

    /**
     * Processes the given template with the model and writes the result to the
     * PrintWriter.
     *
     * @param templateName name of the Freemarker template
     * @param model        the model object
     * @param pw           Printwriter to write formatted template result
     *
     * @throws IOException       if the template could not be found or read or a IO
     *                           problem during write
     * @throws TemplateException if something is wrong with the template
     */
    public void process(final String templateName, final Object model, final PrintWriter pw)
            throws IOException, TemplateException {
        final Template template = configuration.getTemplate(templateName);
        template.process(model, pw);
    }

}
