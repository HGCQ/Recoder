package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    List<ChatDTO> chatList;
    private OnItemClickListener listener;
    private MemberDTO loginMember;
    private Context context;
    private String serverIp;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ChatAdapter(MemberDTO loginMember, Context context, List<ChatDTO> chatList) {
        this.loginMember = loginMember;
        this.chatList = chatList;
        this.context = context;
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
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
        public ImageView profile;
        public LinearLayout container;

        public ChatViewHolder(@NonNull View view, OnItemClickListener listener, int viewType) {
            super(view);

            message = view.findViewById(R.id.chatting);
            name = view.findViewById(R.id.name);
            profile = view.findViewById(R.id.basicProfile);
            container = view.findViewById(R.id.container);

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
        chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

        return new ChatAdapter.ChatViewHolder(chatView, listener, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDTO chatDTO = chatList.get(position);
        holder.message.setText(chatDTO.getMessage());

        if (holder.getItemViewType() == 0) { // 상대의 채팅
            holder.name.setText(chatDTO.getWriterName());
            holder.name.setVisibility(View.VISIBLE);
            handler.post(() -> {
                String path = serverIp+chatDTO.getImage();
                Glide.with(context)
                       .load(path)
                        .into(holder.profile);
            });
            holder.profile.setVisibility(View.VISIBLE);

            // 상대의 채팅은 왼쪽 정렬
            holder.container.setGravity(Gravity.START); // 왼쪽 정렬
            holder.message.setBackgroundResource(R.drawable.text);

            holder.message.setTranslationX(-200);

        } else { // 자신의 채팅
            holder.name.setVisibility(View.GONE); // 이름 숨기기
            holder.profile.setVisibility(View.GONE); // 프로필 숨기기

            // 자신의 채팅은 오른쪽 정렬
            holder.container.setGravity(Gravity.END); // 오른쪽 정렬
            holder.message.setTranslationX(-20);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
