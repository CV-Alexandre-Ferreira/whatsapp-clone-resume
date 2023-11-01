package com.example.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.GroupActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.ContactsAdapter;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerViewContactsList;
    private ContactsAdapter adapter;
    private ArrayList<User> contactsList = new ArrayList<>();
    private DatabaseReference usersRef;
    private ValueEventListener valueEventListenerContacts;
    private FirebaseUser currentUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //Initial Configs
        recyclerViewContactsList = view.findViewById(R.id.recyclerListaContatos);
        usersRef = FirebaseConfig.getFirebaseDatabase().child("usuarios");
        currentUser = UserFirebaseHelper.getCurrentUser();

        //Set adapter
        adapter = new ContactsAdapter(contactsList, getActivity() );

        //set recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContactsList.setLayoutManager(layoutManager);
        recyclerViewContactsList.setHasFixedSize(true);
        recyclerViewContactsList.setAdapter(adapter);

        //set recyclerview click event
        recyclerViewContactsList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewContactsList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User selectedUser = contactsList.get(position);
                                boolean header = selectedUser.getEmail().isEmpty();

                                if(header){

                                    Intent i = new Intent(getActivity(), GroupActivity.class);
                                    startActivity(i);

                                }else{
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("chatContato", selectedUser );
                                    startActivity(i);


                                }
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

        addNewGroupMenu();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getContacts();

    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContacts);
    }

    public void getContacts(){
        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cleanContactsList();

                for(DataSnapshot data: snapshot.getChildren()){


                    User user = data.getValue(User.class);
                    String currentUserEmail = currentUser.getEmail();
                    if(!currentUserEmail.equals(user.getEmail())) contactsList.add(user);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void cleanContactsList(){
        contactsList.clear();
        addNewGroupMenu();
    }
    public void addNewGroupMenu(){

        User groupItem = new User();
        groupItem.setName("New Group");
        groupItem.setEmail("");

        contactsList.add(groupItem);
    }
}