
buildLogFile = new File(basedir, "build.log")
buildLogText = buildLogFile.text

assert buildLogText.contains("invalid key: empty key") : "Empty key not detected"

println "Test OK"
assert true
