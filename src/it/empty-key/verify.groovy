
buildLogFile = new File(basedir, "build.log")
buildLogText = buildLogFile.text

assert buildLogText.contains("File contains entry with empty key") : "Empty key not detected"

println "Test OK"
assert true
