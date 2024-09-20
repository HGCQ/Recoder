package yuhan.hgcq.client.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    private List<MemberDTO> followList;
    private List<Long> selectedItems = new ArrayList<>();
    private OnItemClickListener listener;

    public FollowAdapter(List<MemberDTO> followList) {
        this.followList = followList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FollowViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public FollowViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            name = view.findViewById(R.id.name);

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

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View followView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_add, parent, false);
        return new FollowViewHolder(followView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        MemberDTO memberDTO = followList.get(position);
        holder.name.setText(memberDTO.getName());

        if (selectedItems.contains((long) position)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedItems.contains((long) position)) {
                selectedItems.remove(position);
            } else {
                selectedItems.add((long) position);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public List<Long> getSelectedItems() {
        return selectedItems;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void updateList(List<MemberDTO> newList) {
        followList.clear();;
        followList.addAll(newList);
        notifyDataSetChanged();
    }
}