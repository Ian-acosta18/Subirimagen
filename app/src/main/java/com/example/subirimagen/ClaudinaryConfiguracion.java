package com.example.subirimagen; // Corregido el paquete

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class ClaudinaryConfiguracion {
    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) return;

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dh3ytvnwf");
        config.put("api_key", "346822317582425");

        MediaManager.init(context, config);
        initialized = true;
    }
}