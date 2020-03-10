package com.prpr.androidpprog2.entregable.utils;

import java.util.HashMap;

public class CloudinaryConfigs {

    public static HashMap getConfigurations() {
        HashMap  config = new HashMap();
        config.put("cloud_name", "username" );
        config.put("api_key", "your_api_key");
        config.put("api_secret", "your_api_secret");
        return config;
    }
}
