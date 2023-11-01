package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupAdapter.MyViewHolder>{

    private List<User> selectedContacts;
    private Context context;

    public SelectedGroupAdapter(List<User> contactsList, Context c) {
        this.selectedContacts = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public SelectedGroupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_selected_group, parent, false);
        return new SelectedGroupAdapter.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedGroupAdapter.MyViewHolder holder, int position) {

        User user = selectedContacts.get(position);

        holder.name.setText(user.getName());

        if(user.getFoto() != null){
            Uri uri = Uri.parse(user.getFoto());
            Glide.with(context).load( uri ).into(holder.picture);
        }else {

           holder.picture.setImageResource(R.drawable.padrao);

        }


    }

    @Override
    public int getItemCount() {
        return selectedContacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView picture;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.imageViewFotoMembroSelecionado);
            name = itemView.findViewById(R.id.textNomeMembroSelecionado);
        }
    }
}
