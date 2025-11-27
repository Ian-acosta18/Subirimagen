package com.example.subirimagen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ImageView imgProfile;
    Button btnSelectPhoto, btnRegister;
    EditText etName, etEmail, etPass;
    Uri imageUri;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicialización corregida
        ClaudinaryConfiguracion.init(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");

        imgProfile = findViewById(R.id.imgProfileRegister);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnRegister = findViewById(R.id.btnDoRegister);
        etName = findViewById(R.id.etNameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPass = findViewById(R.id.etPassRegister);

        btnSelectPhoto.setOnClickListener(v -> abrirGaleria());
        btnRegister.setOnClickListener(v -> validarYRegistrar());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).circleCrop().into(imgProfile);
        }
    }

    private void validarYRegistrar() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "¡Debes elegir una foto de perfil!", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creando perfil...");
        dialog.setCancelable(false);
        dialog.show();

        MediaManager.get().upload(imageUri)
                .unsigned("cazojzj8")
                .option("folder", "perfiles_usuarios")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String fotoUrl = resultData.get("secure_url").toString();
                        crearUsuarioFirebase(email, pass, name, fotoUrl, dialog);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error al subir foto: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void crearUsuarioFirebase(String email, String pass, String name, String fotoUrl, ProgressDialog dialog) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre", name);
                        map.put("email", email);
                        map.put("fotoPerfil", fotoUrl);
                        map.put("uid", user.getUid());

                        mDatabase.child(user.getUid()).setValue(map)
                                .addOnSuccessListener(aVoid -> {
                                    dialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error Auth: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}