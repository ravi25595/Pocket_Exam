package zyrosite.pocketexam.ui;

import static android.content.Context.DOWNLOAD_SERVICE;

import static zyrosite.pocketexam.ui.MainActivity.convertDuration;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import zyrosite.pocketexam.CategoryModel;
import zyrosite.pocketexam.NewTestActivity;
import zyrosite.pocketexam.PdfActivity;
import zyrosite.pocketexam.R;
import zyrosite.pocketexam.ScoreActivity;
import zyrosite.pocketexam.SetModel;
import zyrosite.pocketexam.StudyMaterialModel;
import zyrosite.pocketexam.WebViewActivity;

class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {
    private List<HomePageModel> homePageModelList;
    private Map<String, Object> map = new HashMap<>();
    private List<String> PREVIOUS_TEST_ID, PREVIOUS_TEST_MARKS, PREVIOUS_TEST_TOTAL;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_type_item, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tests_list_item, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_test_item, parent, false);
                break;
            default:
                //view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                holder.setStudyMaterial(position);
                break;
            case 2:
                holder.setData(position);
                break;
            case 3:
                holder.setPreviousResult(position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return homePageModelList.get(position).getType();
    }

    public void setCategoryMap(Map<String, Object> map) {
        this.map = map;
    }

    public void setPreviousResults(List<String> previous_test_id, List<String> previous_test_marks, List<String> previous_test_total) {
        PREVIOUS_TEST_ID = previous_test_id;
        PREVIOUS_TEST_MARKS = previous_test_marks;
        PREVIOUS_TEST_TOTAL = previous_test_total;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setData(final int position) {
            TextView subject = itemView.findViewById(R.id.subject);
            TextView qNd = itemView.findViewById(R.id.questionNduration);
            Button start = itemView.findViewById(R.id.startBtn);
            TextView timeStamp = itemView.findViewById(R.id.timeStamp);

            String duration = convertDuration(homePageModelList.get(position).getSetModel().getDuration());

            subject.setText(homePageModelList.get(position).getSetModel().getSubject());

            qNd.setText(homePageModelList.get(position).getSetModel().getNo_of_questions() + " Questions | " + duration);
            timeStamp.setText(setDateFormat(homePageModelList.get(position).getSetModel().getTimeStamp()));
            start.setOnClickListener(v -> showConfirmDialog(position));
        }

        private void showConfirmDialog(int position) {
            Dialog dialog = new Dialog(itemView.getContext());//, android.R.style.Theme_DeviceDefault_NoActionBar_Overscan);
            dialog.setContentView(R.layout.confirm_test_dialog);
            dialog.show();

            TextView title, no_of_questions, duration;
            Button cancelBtn, startBtn;
            title = dialog.findViewById(R.id.title);
            duration = dialog.findViewById(R.id.test_duration);
            no_of_questions = dialog.findViewById(R.id.no_of_questions);
            cancelBtn = dialog.findViewById(R.id.cancelBtn);
            startBtn = dialog.findViewById(R.id.startBtn);
            SetModel model = homePageModelList.get(position).getSetModel();

            title.setText(model.getSubject());
            duration.setText("Test Duration : " + convertDuration(model.getDuration()));
            no_of_questions.setText("No of Questions : " + model.getNo_of_questions());
            cancelBtn.setOnClickListener(view -> dialog.dismiss());
            startBtn.setOnClickListener(view -> {
                Intent questionIntent = new Intent(itemView.getContext(), NewTestActivity.class);
                questionIntent.putExtra("categoryID", homePageModelList.get(position).getSetModel().getCategoryID());
                questionIntent.putExtra("SetID", homePageModelList.get(position).getSetModel().getSetID());
                questionIntent.putExtra("duration", homePageModelList.get(position).getSetModel().getDuration());
                itemView.getContext().startActivity(questionIntent);
                dialog.dismiss();
            });
        }

        void setStudyMaterial(int position) {
            TextView name = itemView.findViewById(R.id.title);
            TextView category = itemView.findViewById(R.id.categoryName);
            TextView timeStamp = itemView.findViewById(R.id.timestamp);
            CircleImageView icon = itemView.findViewById(R.id.image_view);
            final StudyMaterialModel studyModel = homePageModelList.get(position).getStudyModel();
            name.setText(studyModel.getName());
            timeStamp.setText(setDateFormat(studyModel.getTimeStamp().toDate()));
            category.setText(((CategoryModel) (map.get(studyModel.getCategoryID()))).getName());
            Glide.with(itemView).load(((CategoryModel) map.get(studyModel.getCategoryID())).getUrl()).apply(new RequestOptions().placeholder(R.drawable.loading_animated_gif)).into(icon);
            itemView.setOnClickListener(v -> {
                try {
                    downloadFile(itemView.getContext(), studyModel.getLink(), studyModel.getName(), studyModel.getPath());
                }catch (Exception e){
                    Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        void setPreviousResult(final int position) {
            TextView subject = itemView.findViewById(R.id.subject);
            TextView score = itemView.findViewById(R.id.tv_score);
            TextView qNd = itemView.findViewById(R.id.questionNduration);
            Button start = itemView.findViewById(R.id.startBtn);
            Button review = itemView.findViewById(R.id.view_result);
            TextView timeStamp = itemView.findViewById(R.id.timeStamp);
            int index = PREVIOUS_TEST_ID.indexOf(homePageModelList.get(position).getSetModel().getSetID());
            score.setText(PREVIOUS_TEST_MARKS.get(index) + "\n" + PREVIOUS_TEST_TOTAL.get(index));
            String duration = convertDuration(homePageModelList.get(position).getSetModel().getDuration());
            subject.setText(homePageModelList.get(position).getSetModel().getSubject());
            qNd.setText(homePageModelList.get(position).getSetModel().getNo_of_questions() + " Questions | " + duration);
            timeStamp.setText(setDateFormat(homePageModelList.get(position).getSetModel().getTimeStamp()));
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(itemView.getContext(), NewTestActivity.class);
                    questionIntent.putExtra("categoryID", homePageModelList.get(position).getSetModel().getCategoryID());
                    questionIntent.putExtra("SetID", homePageModelList.get(position).getSetModel().getSetID());
                    questionIntent.putExtra("duration", homePageModelList.get(position).getSetModel().getDuration());
                    itemView.getContext().startActivity(questionIntent);
                }
            });
            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent scoreIntent = new Intent(itemView.getContext(), ScoreActivity.class);
                    scoreIntent.putExtra("SetID", homePageModelList.get(position).getSetModel().getSetID());
                    itemView.getContext().startActivity(scoreIntent);
                }
            });
        }
    }

    private void downloadFile(Context context, String link, String name, String path) throws FileNotFoundException {
        path = path.substring(0, path.length() - name.length() - 1);
        File file = new File(context.getExternalFilesDir(path), name + ".pdf");
        if (file.exists()) {
            /*
            Intent intent = new Intent(context, PdfActivity.class);
            intent.putExtra("path", file.getPath());
            context.startActivity(intent);
             */
            Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
            pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenIntent.setClipData(ClipData.newRawUri("", Uri.parse(file.getPath())));
            pdfOpenIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
            pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |  Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivity(pdfOpenIntent);
        } else {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", link);
            context.startActivity(intent);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link))
                    .setTitle(name)
                    .setDescription("Downloading...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            long downloadID = downloadManager.enqueue(request);
            downloadManager.openDownloadedFile(downloadID);
        }
    }

    private String setDateFormat(Date timeStamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM hh:mm aa");
        return simpleDateFormat.format(timeStamp);
    }
}
