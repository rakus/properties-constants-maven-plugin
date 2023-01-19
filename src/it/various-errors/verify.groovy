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

buildLogFile = new File(basedir, "build.log")
buildLogText = buildLogFile.text

// Depending on file sorting:
// testOne.properties[1:1] Would create same constant class de.tester.constants.TestOne as test_one.properties
// test_one.properties[1:1] Would create same constant class de.tester.constants.TestOne as testOne.properties
assert buildLogText.contains("Would create same constant class de.tester.constants.TestOne") : "Duplicate class not detected"

assert buildLogText.contains("invalid.xml[0:0] Error loading properties file") : "Invalid XML not detected"

assert buildLogText.contains("No properties files found - no Java classes to generate") : "Not Files found not detected"

assert buildLogText.contains("Skipped - skip == true") : "Skip property ignored"

println "Test OK"

assert true
