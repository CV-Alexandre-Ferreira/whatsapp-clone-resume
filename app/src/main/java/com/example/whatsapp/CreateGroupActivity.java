package com.example.whatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.whatsapp.adapter.SelectedGroupAdapter;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.databinding.ActivityCreateGroupBinding;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCreateGroupBinding binding;
    private List<User> selectedMembersList = new ArrayList<>();

    private TextView textTotalMembers;
    private SelectedGroupAdapter selectedGroupAdapter;
    private RecyclerView recyclerSelectedMembers;
    private StorageReference storageReference;

    private static final int GALERY_SELECTION = 200;

    private CircleImageView imageGroup;

    private Group group;

    private FloatingActionButton fabSaveGroup;
    private EditText editGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseConfig.getFirebaseStorage();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Group");
        getSupportActionBar().setSubtitle("Define the name");

        group = new Group();

        textTotalMembers = findViewById(R.id.textTotalParticipantes);
        imageGroup = findViewById(R.id.imageGrupo);

        fabSaveGroup = findViewById(R.id.fabSalvarGrupo);
        editGroupName = findViewById(R.id.editNomeGrupo);

        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(i, GALERY_SELECTION);
            }
        });

        recyclerSelectedMembers = findViewById(R.id.recyclerMembrosGrupo);

/*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_cadastro_grupo);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/
/*
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //recover members list
        if (getIntent().getExtras() != null) {
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("membros");
            selectedMembersList.addAll(members);

            textTotalMembers.setText("Participants: " + selectedMembersList.size());
        }

        //RecyclerView config
        selectedGroupAdapter = new SelectedGroupAdapter(selectedMembersList, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        recyclerSelectedMembers.setHasFixedSize(true);
        recyclerSelectedMembers.setAdapter(selectedGroupAdapter);

        //fab config
        fabSaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeGrupo = editGroupName.getText().toString();

                //add logged user to members list
                selectedMembersList.add(UserFirebaseHelper.getDataFromLoggedUser());
                group.setMembers(selectedMembersList);

                group.setName(nomeGrupo);
                group.save();

                Intent i = new Intent(CreateGroupActivity.this, ChatActivity.class);
                i.putExtra("chatGrupo", group);
                startActivity(i);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {

                Uri selectedImageLocal = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageLocal);

                if(image != null){
                    imageGroup.setImageBitmap(image);

                    //get image data for firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //save image on firebase
                    final StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("grupos")
                            //.child(identificadorUusario)
                            .child(group.getId()+".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateGroupActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CreateGroupActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String  url = task.getResult().toString();
                                    group.setPicture(url);
                                }
                            });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    /*

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_cadastro_grupo);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    */

    }
}