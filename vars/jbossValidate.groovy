def call(def configName, def branch) {
    if (branch == "staging" || branch == "development" || branch == "uat" || branch == "staging2" || branch == "Staging" || branch == "jbossmigration") {
        if (configName == "Test_mCashDC" ||configName == "Test_mCashDC-DR" ||configName == "mcasch-dev-dc.mobitel.lk-DR" || configName == "jbossDC.mobitel.lk" || configName == "jbossDCNew.mobitel.lk-DR" || configName == "jbossDC.mobitel.lk-DR" ||configName == "ipgtest.mobitel.lk" ||configName == "ipgtest.mobitel.lk-DR" ||configName == "ipgtestNew.mobitel.lk-DR" || configName == "jboss7test-DMZ" || configName == " testjboss-140" || configName == "testjbossnewdc" ||configName == "testjbossnewdc-DR" || configName == "railwaytestdc" ||configName == "railwaytestdc-DR" || configName == "Test_mCashDC_JAR" || configName == "TestJars" || configName == "ECL-Test-JBossDC" || configName ==  "mCash-JBoss8-Test-DC" || configName ==  "IntApps-JBoss8-Test-DC" || configName ==  "Test_FinX_JBossDC" || configName == "JBoss_Temp_DC_Test" || configName == "Railway_Temp_DC_Test" || configName == "JBoss-MADPDC-Test" || configName == "jbossDCNew.mobitel.lk-DR-Clone" || configName == "Test_FinX_JBossDC_Clone") {
            return true;
        }
        return false;
    } else if (branch == "production" || branch == "Production" || branch == "dr" || branch == "production2" || branch == "production3" ) {
        if (configName == "mCashDC" || configName == "JBoss-SelfCare-DC-Prod"  || configName == "JBoss-SelfCare-DC-Prod-DR"  || configName == "jbosscgdc.mobitel.lk" || configName == "jbosscgdc.mobitel.lk-DR" ||configName == "CRM_DC" ||configName == "CRM_DC-DR" || configName == "JBoss7DC-DMZ" || configName == "JBoss7DC-DMZ-DR" ||configName == "JbossTelcoDC" || configName == "mCashProdJB12C" || configName == 'mCashProdJAR' || configName == "mCashDC-DR" || configName == 'mCashProdJB12C-DR' || configName == 'JBoss-MADP-Prod' || configName == 'FinX_JBossDC' || configName == 'ECL_CRM-JBossDC' || configName == 'mCash-JBoss8-DC'|| configName == 'IntApps-JBoss8-DC' || configName == 'ECL_JBossDC' || configName == 'JBoss-MADPDC-Prod' || configName == 'Jboss-Mobitel-SLT-DC') {
            return true;
        }
    }
    return false;
}
