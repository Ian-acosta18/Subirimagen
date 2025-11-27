package com.example.subirimagen;

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

        // Inicialización de Cloudinary
        try {
            ClaudinaryConfiguracion.init(this);
        } catch (Exception e) {
            Log.e("Config", "Error iniciando Cloudinary", e);
        }

        imgPreview = findViewById(R.id.imgPreview);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnSubir = findViewById(R.id.btnSubir);
        btnVerImagen = findViewById(R.id.btnVerImagen);

        // Referencia a Firebase
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
        dialog.setCancelable(false); // Evita cancelaciones accidentales
        dialog.show();

        MediaManager.get().upload(imagenUri)
                .unsigned("cazojzj8")
                .option("folder", "mi_carpeta")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // 1. Procesar datos (esto puede ir en segundo plano)
                        String url = "";
                        if (resultData != null && resultData.get("secure_url") != null) {
                            url = resultData.get("secure_url").toString();
                            Log.d("Cloudinary", "URL subida: " + url);

                            // Guardar en Firebase
                            String key = databaseReference.push().getKey();
                            if (key != null) {
                                databaseReference.child(key).setValue(url);
                            }
                        }

                        // 2. Actualizar la Interfaz (OBLIGATORIO USAR runOnUiThread)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Verificamos que la actividad siga viva antes de cerrar el dialogo
                                if (!isFinishing() && !isDestroyed()) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Imagen guardada con éxito", Toast.LENGTH_LONG).show();

                                    // Reiniciar vista
                                    imgPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                                    imagenUri = null;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        // También usamos runOnUiThread para el error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing() && !isDestroyed()) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error: " + error.getDescription(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) { }
                })
                .dispatch();
    }
}