
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
