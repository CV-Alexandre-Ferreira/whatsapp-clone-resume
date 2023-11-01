package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Message> messages;
    private Context context;
    private static final int SENDER_TYPE = 0;
    private static final int RECEIVER_TYPE = 1;

    public MessagesAdapter(List<Message> list, Context c) {
        this.messages = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        if(viewType == SENDER_TYPE){

            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_menssage_sender, parent, false);

        }else if (viewType == RECEIVER_TYPE){

            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_receiver, parent, false);
        }

        return new MyViewHolder(item);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Message message = messages.get(position);
        String msg = message.getMessage();
        String image = message.getImage();

        if(image != null) {
            Uri url = Uri.parse(image);
            Glide.with(context).load(url).into(holder.image);

            String nome = message.getName();
            if(!nome.isEmpty()) {
                holder.name.setText(nome);
            }else{holder.name.setVisibility(View.GONE);}
            //Esconder texto
            holder.message.setVisibility(View.GONE);


        }else{
            holder.message.setText(msg);

            String nome = message.getName();
            if(!nome.isEmpty()) {
                holder.name.setText(nome);
            }else{holder.name.setVisibility(View.GONE);}
            //Esconder a image
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message mensagem = messages.get(position);
        String idUsuario = UserFirebaseHelper.getUserId();

        if(idUsuario.equals(mensagem.getUserId())) {
            return SENDER_TYPE;
        }
        return RECEIVER_TYPE;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        ImageView image;
        TextView name;
        public MyViewHolder(View itemView){
            super(itemView);

            message = itemView.findViewById(R.id.textMensagemTexto);
            image = itemView.findViewById(R.id.imageMensagemFoto);
            name = itemView.findViewById(R.id.textNomeExibicao);
        }
    }
}
