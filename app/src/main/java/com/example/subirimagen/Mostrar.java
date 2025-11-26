package com.example.subirimagen; // Paquete corregido

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

                            if (url != null) {
                                ImageView img = new ImageView(Mostrar.this);
                                img.setAdjustViewBounds(true);
                                img.setPadding(10,10,10,10);

                                // Cargar imagen con Glide
                                Glide.with(Mostrar.this).load(url).into(img);

                                contenedor.addView(img);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}