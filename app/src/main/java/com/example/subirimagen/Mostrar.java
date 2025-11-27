package com.example.subirimagen; // Corregido el paquete

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

public class Mostrar extends AppCompatActivity { // Nombre de clase con Mayúscula

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
                        contenedor.removeAllViews(); // Limpia la lista antes de agregar nuevas

                        for (DataSnapshot ds : snapshot.getChildren()){
                            String url = ds.getValue(String.class);

                            if (url != null) {
                                ImageView img = new ImageView(Mostrar.this);

                                // 1. IMPORTANTE: Definir LayoutParams para que la imagen tenga tamaño
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, // Ancho: ocupa todo
                                        LinearLayout.LayoutParams.WRAP_CONTENT  // Alto: se ajusta a la imagen
                                );
                                params.setMargins(0, 0, 0, 30); // Margen inferior para separar imágenes
                                img.setLayoutParams(params);

                                // 2. Configuración de la imagen
                                img.setAdjustViewBounds(true); // Permite que el alto se ajuste a la imagen
                                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                img.setPadding(10,10,10,10);

                                // 3. Cargar con Glide
                                Glide.with(Mostrar.this)
                                        .load(url)
                                        .placeholder(R.drawable.ic_launcher_background) // (Opcional) Muestra algo mientras carga
                                        .into(img);

                                contenedor.addView(img);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}