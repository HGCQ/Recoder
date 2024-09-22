package yuhan.hgcq.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    List<ChatDTO> chatList;
    private OnItemClickListener listener;
    private MemberDTO loginMember;

    public ChatAdapter(MemberDTO loginMember, List<ChatDTO> chatList) {
        this.loginMember = loginMember;
        this.chatList = chatList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;

        public ChatViewHolder(@NonNull View view, OnItemClickListener listener, int viewType) {
            super(view);

            message = view.findViewById(R.id.chatting);
            if (viewType == 1) {

            } else {
                name = view.findViewById(R.id.name);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, position);
                        }
                    }
                }
            });
        }
    }

    public void addChat(ChatDTO chatDTO) {
        chatList.add(chatDTO);
        notifyItemInserted(chatList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatDTO chatDTO = chatList.get(position);
        return chatDTO.getWriterId().equals(loginMember.getMemberId()) ? 1 : 0;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView;
        /* 자기 채팅 */
        if (viewType == 1) {
            chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_chat, parent, false);
        } else {
            chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        }
        return new ChatAdapter.ChatViewHolder(chatView, listener, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDTO chatDTO = chatList.get(position);
        holder.message.setText(chatDTO.getMessage());

        if(holder.getItemViewType() == 0) {
            holder.name.setText(chatDTO.getWriterName());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
