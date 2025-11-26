package com.example.subirimagen; // Paquete corregido

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class ClaudinaryConfiguracion {
    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) return;

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dfepwnerh"); // tu cloud_name de Cloudinary
        config.put("api_key", "123456789012345"); // REEMPLAZA ESTO CON TU API KEY REAL SI ES NECESARIO
        // Nota: Para subidas "unsigned", a veces no se requiere api_key en el cliente,
        // pero si te da error, verifica tu consola de Cloudinary.

        MediaManager.init(context, config);
        initialized = true;
    }
}