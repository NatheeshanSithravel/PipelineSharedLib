def call(def configName) {
    if (configName == "WeblogicMob_ADF_Test") {
        ""
    } else if (configName == "WeblogicMob_Non_ADF_Test") {
        "-Non-ADF"
    } else if (configName == "WeblogicMob_ADF_Prod") {
        ""
    } else if (configName == "WeblogicMob_Non_ADF_Prod") {
        "-Non-ADF"
    }
}
