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

        // Inicialización de la configuración de Cloudinary
        ClaudinaryConfiguracion.init(this);

        imgPreview = findViewById(R.id.imgPreview);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnSubir = findViewById(R.id.btnSubir);
        btnVerImagen = findViewById(R.id.btnVerImagen);

        // Referencia a la base de datos Realtime Database
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
        dialog.setCancelable(false); // Evita que el usuario cancele tocando fuera
        dialog.show();

        MediaManager.get().upload(imagenUri)
                .unsigned("cazojzj8")
                .option("folder", "mi_carpeta")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // Opcional: acciones al iniciar
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Opcional: actualizar barra de progreso
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Obtener la URL de la imagen subida
                        String url = resultData.get("secure_url").toString();
                        Log.d("Cloudinary", "URL subida: " + url);

                        // Guardar la URL en Firebase Database
                        String key = databaseReference.push().getKey();
                        if (key != null) {
                            databaseReference.child(key).setValue(url);
                        }

                        // IMPORTANTE: Ejecutar cambios de UI en el hilo principal
                        // Esto evita que la app se cierre (crash) por tocar la UI desde background
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Imagen subida correctamente", Toast.LENGTH_LONG).show();

                                // Opcional: Reiniciar la vista para subir otra
                                imgPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                                imagenUri = null;
                            }
                        });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        // IMPORTANTE: Ejecutar cambios de UI en el hilo principal
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Error: "+ error.getDescription(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Manejo de reprogramación si es necesario
                    }
                })
                .dispatch();
    }
}