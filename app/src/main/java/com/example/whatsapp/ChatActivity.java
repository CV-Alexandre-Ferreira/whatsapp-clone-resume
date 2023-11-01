package com.example.whatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.adapter.MessagesAdapter;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.databinding.ActivityChatBinding;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private CircleImageView circleImageViewPicture;
    private User receiverUser;
    private ImageView imageCamera;
    private User senderUser;
    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private ImageView imageChatContact;
    private EditText editMessage;
    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;

    private List<Message> messages = new ArrayList<>();

    private static final int CAMERA_SELECTION = 100;

    private DatabaseReference database;
    private StorageReference storage;

    //id from user sender and receiver
    private String userSenderId;
    private String userReceiverId;

    private DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessages;

    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSenderId = UserFirebaseHelper.getUserId();
         senderUser = UserFirebaseHelper.getDataFromLoggedUser();

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setTitle("");

        textViewName = findViewById(R.id.textViewNomeChat);
        circleImageViewPicture = findViewById(R.id.imageViewChat);
        imageChatContact = findViewById(R.id.imageViewChat);
        editMessage = findViewById(R.id.editMensagem);
        recyclerMessages = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera);

        //get data from receiver user
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey("chatGrupo")){
                group = (Group) bundle.getSerializable("chatGrupo");
                userReceiverId = group.getId();
                textViewName.setText(group.getName());

                String picture = group.getPicture();
                if(picture!= null){
                    Uri url = Uri.parse(picture);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageViewPicture);
                }else {
                    circleImageViewPicture.setImageResource(R.drawable.padrao);
                }


            }else{

                receiverUser = (User) bundle.getSerializable("chatContato");
                textViewName.setText(receiverUser.getName());
                String foto = receiverUser.getFoto();
                if(foto!= null){
                    Uri url = Uri.parse(receiverUser.getFoto());
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageViewPicture);
                }else {
                    circleImageViewPicture.setImageResource(R.drawable.padrao);
                }

                //get data from receiver user
                userReceiverId = Base64Custom.encodeBase64(receiverUser.getEmail());
                /*************/

            }
        }

        //adapter config
        adapter = new MessagesAdapter(messages, getApplicationContext());

        //Configuraçao recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setAdapter(adapter);

        database = FirebaseConfig.getFirebaseDatabase();
        storage = FirebaseConfig.getFirebaseStorage();
        messagesRef = database.child("mensagens")
                .child(userSenderId)
                .child(userReceiverId);

        //Click event camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null) startActivityForResult(i, CAMERA_SELECTION);

            }
        });

        /*

        imagemContatoConversa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }); */


        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_chat);
        // appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Bitmap image = null;

            try{
                switch (requestCode){
                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }

                if(image != null){

                    //get image data for firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //CreateImageName
                    String imageName = UUID.randomUUID().toString();

                    //firebase reference config
                    final StorageReference imageRef = storage.child("imagens")
                            .child("fotos")
                            .child(userSenderId)
                            .child(imageName);

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String downloadUrl = task.getResult().toString();

                                    if(receiverUser != null){//default message

                                        Message message = new Message();
                                        message.setUserId(userSenderId);
                                        message.setMessage("image.jpeg");
                                        message.setImage( downloadUrl );

                                        //save message sender
                                        saveMessage(userSenderId, userReceiverId, message);
                                        //save message receiver
                                        saveMessage(userReceiverId, userSenderId, message);

                                    }else{//group message

                                        for(User member: group.getMembers() ){

                                            String userSenderId = Base64Custom.encodeBase64(member.getEmail());
                                            String userGroupLoggedId = UserFirebaseHelper.getUserId();

                                            Message message = new Message();
                                            message.setUserId(userGroupLoggedId);
                                            message.setMessage("image.jpeg");
                                            message.setName( senderUser.getName() );
                                            message.setImage( downloadUrl );

                                            //save message for member
                                            saveMessage(userSenderId, userReceiverId, message);

                                            //save chat
                                            saveChat( userSenderId, userReceiverId, receiverUser,  message, true);


                                        }

                                    }

                                    Toast.makeText(ChatActivity.this, "Sucesso ao enviar image", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    });

                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public void sendMessage(View view){
        String textMessage = editMessage.getText().toString();
        if(!textMessage.isEmpty()){

            if(receiverUser != null){

                Message message = new Message();
                message.setUserId(userSenderId);
                message.setMessage(textMessage);

                //save message for sender
                saveMessage(userSenderId, userReceiverId, message );

                //save message for receiver
                saveMessage(userReceiverId, userSenderId, message );

                //save chat for sender
                saveChat(userSenderId, userReceiverId, receiverUser, message, false);

                //save message for receiver
                saveChat(userReceiverId, userSenderId, senderUser, message, false);

            }else{
                for(User member: group.getMembers() ){

                    String groupSenderId = Base64Custom.encodeBase64(member.getEmail());
                    String loggedUserGroupId = UserFirebaseHelper.getUserId();

                    Message message = new Message();
                    message.setUserId(loggedUserGroupId);
                    message.setMessage(textMessage);
                    message.setName( senderUser.getName() );

                    //save message for member
                    saveMessage(groupSenderId, userReceiverId, message);

                    //save chat
                    saveChat( groupSenderId, userReceiverId, receiverUser,  message, true);


                }
            }



        }
    }

    private void saveChat(String senderId, String receiverId, User showcaseUser, Message msg, boolean isGroup){
        Chat senderChat = new Chat();
        senderChat.setSenderId(senderId);
        senderChat.setReceiverId(receiverId);
        senderChat.setLastMessage(msg.getMessage());
        if(isGroup){

            //group chat
            senderChat.setIsGroup("true");
            senderChat.setGroup(group);


        }else {

            //default chat
            senderChat.setShowcaseUser(showcaseUser);
            senderChat.setIsGroup("false");
        }
        senderChat.save();

    }

    private void saveMessage(String senderId, String idDestinatario, Message msg) {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference messageRef = database.child("mensagens");

        messageRef.child(senderId)
                .child(idDestinatario)
                        .push()
                                .setValue(msg);

        //clean text
        editMessage.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesRef.removeEventListener(childEventListenerMessages);
    }

    private void getMessages(){

        messages.clear();

        childEventListenerMessages = messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

    /*

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_chat);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
     */