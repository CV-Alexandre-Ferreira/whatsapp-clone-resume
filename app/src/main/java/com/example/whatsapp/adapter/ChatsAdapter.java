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
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    private List<Chat> chats;
    private Context context;

    public ChatsAdapter(List<Chat> lista, Context c) {
        this.chats = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemList = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.adapter_contacts, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.lastMessage.setText(chat.getLastMessage());


        if(chat.getIsGroup().equals("true")){

            Group group = chat.getGroup();
            holder.name.setText(group.getName());

            if(group.getPicture() != null){

                Uri uri = Uri.parse(group.getPicture());
                Glide.with(context).load( uri ).into(holder.picture);

            }else holder.picture.setImageResource(R.drawable.padrao);


        }else {

            User user = chat.getShowcaseUser();
            if(user != null) {

                holder.name.setText(user.getName());

                if(user.getFoto() != null){

                    Uri uri = Uri.parse(user.getFoto());
                    Glide.with(context).load( uri ).into(holder.picture);

                }else holder.picture.setImageResource(R.drawable.padrao);
            }

        }


    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView picture;
        TextView name, lastMessage;

        public MyViewHolder(View itemView){
            super(itemView);

            picture = itemView.findViewById(R.id.imageViewFotoContato);
            name = itemView.findViewById(R.id.textNomeContato);
            lastMessage = itemView.findViewById(R.id.textEmailContato);
        }
    }

}
