package zyrosite.pocketexam;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemSelectionAdapter extends RecyclerView.Adapter<ListItemSelectionAdapter.ViewHolder> {
    private int size, currentPosition;
    private ItemSelectedListener itemSelectedListener;
    private int[] ANSWER_TYPE;

    public ListItemSelectionAdapter(int size, int currentPosition) {
        this.size = size;
        this.currentPosition = currentPosition;
        ANSWER_TYPE = new int[size];
        for (int i = 0; i < size; i++) {
            ANSWER_TYPE[i] = 0;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    public void setAnswer_type(int ANSWER_TYPE) {
        this.ANSWER_TYPE[currentPosition] = ANSWER_TYPE;
    }
    public void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }
    @Override
    public int getItemCount() {
        return size;
    }

    public void update(int position) {
        currentPosition = position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        TextView text = itemView.findViewById(R.id.textview);
        public void setData(int position) {
            text.setText("" + (position + 1));
            if (currentPosition == position) {
                itemView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8888FF")));
            } else if (ANSWER_TYPE[position] == 0) {
                itemView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
            } else if (ANSWER_TYPE[position] == 1) {
                itemView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#11A10C")));
            } else if (ANSWER_TYPE[position] == 2) {
                itemView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemSelectedListener.onItemSelected(position);
                }
            });
        }
    }
    public interface ItemSelectedListener {
        void onItemSelected(int position);
    }
}
