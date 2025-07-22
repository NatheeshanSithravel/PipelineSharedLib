def call(def configName) {
    if (configName == "WeblogicMob_ADF_Test") {
        "t3://192.168.6.160:7001"
    } else if (configName == "WeblogicMob_Non_ADF_Test") {
        "t3://192.168.6.161:7001"
    } else if (configName == "WeblogicMob_ADF_Prod") {
        "t3://192.168.1.81:7001"
    } else if (configName == "WeblogicMob_Non_ADF_Prod") {
        "t3://192.168.1.82:7001"
    } else if (configName == "Weblogic_181") {
        "t3://192.168.6.181:7001"
    } else if (configName == "Weblogic_182") {
        "t3://192.168.6.182:7001"
    } else if (configName == "Weblogic_181_DC1_PROD") {
        "t3://192.168.41.181:7001"
    } else if (configName == "Weblogic_182_DC1_PROD") {
        "t3://192.168.41.182:7001"
    } else if (configName == "Weblogic_181_DC2_PROD") {
        "t3://172.27.41.181:7001"
    } else if (configName == "Weblogic_182_DC2_PROD") {
        "t3://172.27.41.182:7001"
    }
}
