package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.Permission;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


//Apesar de vc conseguir puxar os dados do usuario direto do database, usar o FirebaseUser é muito mais inteligente

public class ConfigsActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtoncamera, imageButtonGalery;
    private static final int CAMERA_SELECTION = 100;
    private static final int GALERY_SELECTION = 200;
    private StorageReference storageReference;
    private CircleImageView circleImageViewProfile;
    private EditText editUserName;
    private ImageView imageUpdateName;

    private String userId;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configs);


        storageReference = FirebaseConfig.getFirebaseStorage();

        imageButtoncamera = findViewById(R.id.imageButtonCamera);
        imageButtonGalery = findViewById(R.id.imageButtonGaleria);
        circleImageViewProfile = findViewById(R.id.circleImageViewFotoPerfil);
        editUserName = findViewById(R.id.editNomeUsuario);
        imageUpdateName = findViewById(R.id.imageAtualizarNome);

        //Initial config
        userId = UserFirebaseHelper.getUserId();
        loggedUser = UserFirebaseHelper.getDataFromLoggedUser();

        //Validate Permissions
        Permission.validatePermissions(permissoesNecessarias, this, 1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //select main as parent on manifest
        getSupportActionBar().setTitle("Configurations");

        //get user data
        FirebaseUser user = UserFirebaseHelper.getCurrentUser();
        Uri url = user.getPhotoUrl();
        if(url != null){

            Glide.with(ConfigsActivity.this)
                    .load(url)
                    .into(circleImageViewProfile);

        }else {
            circleImageViewProfile.setImageResource(R.drawable.padrao);
        }

        editUserName.setText(user.getDisplayName());




        imageButtoncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null) startActivityForResult(i, CAMERA_SELECTION);
            }
        });

        imageButtonGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null) startActivityForResult(i, GALERY_SELECTION);
                //TODO: Fix startActivityForResult
                else startActivityForResult(i, GALERY_SELECTION);
            }
        });

        imageUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editUserName.getText().toString();
                boolean returnBoolean = UserFirebaseHelper.updateUserName(name);
                if(returnBoolean){

                    loggedUser.setName(name);
                    loggedUser.update();

                    Toast.makeText(ConfigsActivity.this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;

            try {

                switch (requestCode){
                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;

                    case GALERY_SELECTION:
                        Uri selectedImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        break;
                }

                if(image != null){
                    circleImageViewProfile.setImageBitmap(image);

                    //recover image data for firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //save image on firebase
                   final StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child(identificadorUusario)
                            .child(userId +".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigsActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigsActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    updateUserImage(url);
                                }
                            });
                        }
                    });
                }

            }catch (Exception e){e.printStackTrace();}
        }
    }

    public void updateUserImage(Uri url){
        boolean returnBoolean = UserFirebaseHelper.updateUserPicture(url);
        if(returnBoolean){

            loggedUser.setFoto(url.toString());
            loggedUser.update();
            Toast.makeText(this, "Sua foto foi alterada", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permissionResult: grantResults ){
            if(permissionResult == PackageManager.PERMISSION_DENIED){
                validationPermissionAlert();
            }
        }
    }

    private void validationPermissionAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setCancelable(false);
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
            AlertDialog dialog = builder.create();
            dialog.show();
    }
}