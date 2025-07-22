def call(def pom) {
  
    def pack = pom.packaging
    if (pack == 'pom') {
        pack = 'war'
    }
    if (pom.build && pom.build.finalName) {
        "${pom.build.finalName}.${pack}"
    } else {
        "${pom.artifactId}-${pom.version}.${pack}"
    }
}
