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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

public class Mostrar extends AppCompatActivity {

    LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        contenedor = findViewById(R.id.contenedorImagenes);

        FirebaseDatabase
                .getInstance()
                .getReference("imagenes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        contenedor.removeAllViews();

                        for (DataSnapshot ds : snapshot.getChildren()){
                            String url = ds.getValue(String.class);

                            ImageView img = new ImageView(Mostrar.this);
                            img.setAdjustViewBounds(true);
                            img.setPadding(10,10,10,10);

                            Glide.with(Mostrar.this).load(url).into(img);

                            contenedor.addView(img);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
Mostrar.java
Mostrando MainActivity.java.