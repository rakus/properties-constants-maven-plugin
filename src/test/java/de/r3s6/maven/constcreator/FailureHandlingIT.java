package de.r3s6.maven.constcreator;
/*
 *
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

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension
@MavenRepository
@MavenOption(MavenCLIOptions.BATCH_MODE)
@MavenGoal("generate-sources")
public class FailureHandlingIT {

    @MavenTest
    void invalidBasePackage(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error()
                .anyMatch(s -> s.contains("Configured basePackage \"de.r3s6.assert\" is invalid."));
    }

    @MavenTest
    void invalidClassNameSuffix(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error()
                .anyMatch(s -> s.contains("Configured classNameSuffix \"%percent\" is invalid."));
    }

    @MavenTest
    void invalidTemplate(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(s -> s.contains("Error in template processing: invalid.ftl"));
    }

    @MavenTest
    void notExistingResourceDir(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error()
                .anyMatch(s -> s.matches(".*Configured resourceDir \"[^\"]*src[\\\\/]main[\\\\/]resources\" does not exist.*"));
    }

    @MavenTest
    void resourceDirNotDir(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(
                s -> s.matches(".*Configured resourceDir \"[^\"]*[\\\\/]resourceDir.file\" is not a directory..*"));
    }

    @MavenTest
    void templateNotFound(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(
                s -> s.contains("Code template not found: does-not-exist.ftl"));
    }

    @MavenTest
    void templateSyntaxError(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(
                s -> s.contains("Syntax error in template \"invalid.ftl\""));
    }

    @MavenTest
    void emptyPropertyKey(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(
                s -> s.contains("invalid key: empty key"));
    }

    @MavenTest
    void variousErrors(final MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(
                s -> s.contains("Would create same constant class de.tester.constants.TestOne"));
        assertThat(result).out().error().anyMatch(
                s -> s.contains("invalid.xml[0:0] Error loading properties file"));
        assertThat(result).out().info().anyMatch(
                s -> s.contains("No properties files found - no Java classes to generate"));
        assertThat(result).out().info().anyMatch(
                s -> s.contains("Skipped - skip == true"));
    }

}
