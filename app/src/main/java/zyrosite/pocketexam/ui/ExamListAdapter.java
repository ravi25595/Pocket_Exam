package zyrosite.pocketexam.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import zyrosite.pocketexam.CategoryModel;
import zyrosite.pocketexam.R;

public class ExamListAdapter extends RecyclerView.Adapter<ExamListAdapter.ViewHolder> {
    private final List<CategoryModel> categoryModels;
    private boolean checked;

    public ExamListAdapter(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
        checked = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(categoryModels.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView icon;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
        }

        public void setData(CategoryModel categoryModel) {
            Glide.with(itemView.getContext()).load(categoryModel.getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animated_gif)).into(icon);
            title.setText(categoryModel.getName());
            if (MainActivity.PREFERRED_EXAMS == null)
                MainActivity.PREFERRED_EXAMS = new ArrayList<>();
            else if (MainActivity.PREFERRED_EXAMS.contains(categoryModel.getId())) {
                checked = true;
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.successGreen));
            }
            itemView.setOnClickListener(v -> {
                if (checked) {
                    MainActivity.PREFERRED_EXAMS.remove(categoryModel.getId());
                    itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
                } else {
                    MainActivity.PREFERRED_EXAMS.add(categoryModel.getId());
                    itemView.setBackgroundColor(itemView.getResources().getColor(R.color.successGreen));
                }
                checked = !checked;
            });
        }
    }
}
