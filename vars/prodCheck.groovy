def call(prodbuild,password,platform,branch){
    if ((prodbuild == true && password == 'm0bitel#123' && platform != 'weblogic') || (prodbuild == true && password == 'WLpr0d*' && platform == 'weblogic') || branch != 'production'){
        return true
    }else{
        return false
    }
}