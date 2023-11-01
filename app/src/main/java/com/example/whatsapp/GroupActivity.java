package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.whatsapp.adapter.ContactsAdapter;
import com.example.whatsapp.adapter.SelectedGroupAdapter;
import com.example.whatsapp.config.FirebaseConfig;

import com.example.whatsapp.databinding.ActivityGroupBinding;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    //private ActivityGrupoBinding binding;
    private ActivityGroupBinding binding;
    private RecyclerView recyclerMembers, recyclerSelectedMembers;
    private ContactsAdapter cotactsAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    private DatabaseReference usersRef;
    private ValueEventListener valueEventListenerMembers;
    private List<User> membersList = new ArrayList<>();
    private List<User> selectedMembersList = new ArrayList<>();
    private FloatingActionButton fabProceedSignUp;
    private FirebaseUser currentUser;

    public void updateMembersToolbar(){

        int totalSelected = selectedMembersList.size();
        int total = membersList.size() +totalSelected;
        getSupportActionBar().setSubtitle(totalSelected + " de " + total +" selecionados");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Group");

        usersRef = FirebaseConfig.getFirebaseDatabase().child("usuarios");
        currentUser = UserFirebaseHelper.getCurrentUser();

        fabProceedSignUp = findViewById(R.id.fabAvancarGrupo);

        recyclerMembers = findViewById(R.id.recyclerMembros);
        recyclerSelectedMembers = findViewById(R.id.recyclerMembrosSelecionados);

        //adapter Config
        cotactsAdapter = new ContactsAdapter(membersList, getApplicationContext());

        //recyclerView config for contacts
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembers.setLayoutManager(layoutManager);
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setAdapter(cotactsAdapter);

        recyclerSelectedMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerSelectedMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User selectedUser = selectedMembersList.get(position);

                                //remove listing of selected members
                                selectedMembersList.remove(selectedUser);
                                selectedGroupAdapter.notifyDataSetChanged();

                                //add to member listing
                                membersList.add(selectedUser);
                                cotactsAdapter.notifyDataSetChanged();

                                updateMembersToolbar();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        recyclerMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User selectedUser = membersList.get(position);

                                //remove list selected user
                                membersList.remove(selectedUser);
                                cotactsAdapter.notifyDataSetChanged();

                                //add user on new selected list
                                selectedMembersList.add(selectedUser);
                                selectedGroupAdapter.notifyDataSetChanged();

                                updateMembersToolbar();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        //recyclerView config for selected members
        selectedGroupAdapter = new SelectedGroupAdapter(selectedMembersList, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
          getApplicationContext(),
          LinearLayoutManager.HORIZONTAL,
          false
        );
        recyclerSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        recyclerSelectedMembers.setHasFixedSize(true);
        recyclerSelectedMembers.setAdapter(selectedGroupAdapter);

/*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_grupo);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/

        binding.fabAvancarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivity.this, CreateGroupActivity.class);
                i.putExtra("membros", (Serializable) selectedMembersList);
                startActivity(i);
            }
        });
    }

    public void getContacts(){
        valueEventListenerMembers = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data: snapshot.getChildren()){


                    User user = data.getValue(User.class);
                    String currentUserEmail = currentUser.getEmail();
                    if(!currentUserEmail.equals(user.getEmail())) membersList.add(user);

                }
                cotactsAdapter.notifyDataSetChanged();
                updateMembersToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getContacts();

    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerMembers);
    }
/*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_grupo);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/
}