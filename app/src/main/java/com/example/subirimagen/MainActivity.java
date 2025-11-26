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

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnSeleccionar, btnSubir, btnVerImagen;
    Uri imagenUri;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INICIALIZAR CLOUDINARY
        ClaudinaryConfiguracion.init(this);

        imgPreview = findViewById(R.id.imgPreview);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnSubir = findViewById(R.id.btnSubir);
        btnVerImagen = findViewById(R.id.btnVerImagen);

        // BASE DE DATOS REALTIME
        databaseReference = FirebaseDatabase.getInstance().getReference("imagenes");

        btnSeleccionar.setOnClickListener(v -> abrirGaleria());
        btnSubir.setOnClickListener(v -> subirCloudinary());
        btnVerImagen.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Mostrar.class));
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            imagenUri = data.getData();
            Glide.with(this).load(imagenUri).into(imgPreview);
        }
    }

    private void subirCloudinary() {

        if(imagenUri == null){
            Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Subiendo a Cloudinary...");
        dialog.show();

        MediaManager.get().upload(imagenUri)
                .unsigned("ezmy0qai") // Tu preset unsigned
                .option("folder", "mi_carpeta") // Carpeta opcional
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String url = resultData.get("secure_url").toString();
                        Log.d("Cloudinary", "URL subida: " + url);

                        // Guardar URL en Firebase
                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(url);

                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Imagen subida correctamente", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error: "+ error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }
}
MainActivity.java
Mostrando MainActivity.java.