Ir al contenido principal
        Google Classroom
        Classroom
        Desarrollo de Aplicaciones Móviles
        Inicio
        Calendar
        Cursos en los que te has inscrito
        Tareas pendientes
        A
        A-ESTRUCTURA DE DATOS
        EDA_19_24
        C
        Cálculo de Varias Variables O25
        F
        Fundamentos Matemáticos
        1-C TIID
        D
        Desarrollo de Aplicaciones Móviles
        T
        Tutoría Gen2024
        sep-dic24
        Clases archivadas
        Ajustes
        Subir una imagen a la base de datos
        Subir una imagen a la base de datos
        Yesenia Pérez Reyes
        •
        11:36
        100 puntos
        activity_mostrar.xml
        XML

        Mostrar.java
        Java

        activity_main.xml
        XML

        MainActivity.java
        Java

        ClaudinaryConfiguracion.java
        Java

        Comentarios de la clase
        Tu trabajo
        Asignado
        Comentarios privados
        package com.example.subiirimagenes;

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
        config.put("api_key", "123456789012345");

        MediaManager.init(context, config);
        initialized = true;
    }
}

ClaudinaryConfiguracion.java
Mostrando ClaudinaryConfiguracion.java.