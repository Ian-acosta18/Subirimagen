package com.example.subirimagen;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfiguracion {
    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) return;

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dh3ytvnwf");
        config.put("api_key", "346822317582425");

        // Usa getApplicationContext() para seguridad
        MediaManager.init(context.getApplicationContext(), config);
        initialized = true;
    }
}