
buildLogFile = new File(basedir, "build.log")
buildLogText = buildLogFile.text

assert buildLogText.contains("Configures basePackage \"de.r3s6.assert\" is invalid.") : "Invalid basePackage not detected"

println "Test OK"

assert true
