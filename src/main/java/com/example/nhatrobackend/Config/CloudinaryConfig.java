package com.example.nhatrobackend.Config;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dtfuaiper");
        config.put("api_key", "172967973947844");
        config.put("api_secret", "b01hnXfo4oPLZ-Qipaa7cD25tlI");
        return new Cloudinary(config);
    }
}
