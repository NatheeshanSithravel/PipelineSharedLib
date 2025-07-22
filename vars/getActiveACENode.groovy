def call(def host1, host2, def port) {
    try {
        def response = sh script: "curl http://${host1}:${port}/apiv2/system 2>/dev/null", returnStdout: true
        return host1
    } catch (Exception e) { }

    try {
        def response = sh script: "curl http://${host2}:${port}/apiv2/system 2>/dev/null", returnStdout: true
        return host2
    } catch (Exception e) { }
    
    throw new Exception("Couldn't connect to the active node!")
}
