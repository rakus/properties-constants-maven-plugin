
buildLogFile = new File(basedir, "build.log")
buildLogText = buildLogFile.text

assert buildLogText.contains("testOne.properties[1:1] Would creates same constant class de.tester.constants.TestOne as test_one.properties") : "Duplicate class not detected"

assert buildLogText.contains("invalid.xml[1:1] Error loading properties file") : "Invalid XML not detected"

assert buildLogText.contains("No properties files found - no Java classes to generate") : "Not Files found not detected"

assert buildLogText.contains("Skipped - skip == true") : "Not Files found not detected"

println "Test OK"

assert true
