package zyrosite.pocketexam;

import static zyrosite.pocketexam.ScoreActivity.FILE_NAME;
import static zyrosite.pocketexam.ScoreActivity.KEY_1;
import static zyrosite.pocketexam.ScoreActivity.KEY_2;
import static zyrosite.pocketexam.ScoreActivity.KEY_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewTestActivity extends AppCompatActivity {
    private Dialog loadingDialog;
    private int duration, currentPosition;
    private String categoryID, SetID;
    private CountDownTimer countDownTimer;
    private TextView timer, question;
    private Button[] OPTIONS;
    private List<String> ANSWERS;
    private List<QuestionModel> questionModelList;
    private ListItemSelectionAdapter selectionAdapter;
    private RecyclerView recyclerView;
    private Button previous, next, submit;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<String> PREVIOUS_TEST_ID, PREVIOUS_TEST_MARKS, PREVIOUS_TEST_TOTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        categoryID = getIntent().getStringExtra("categoryID");
        SetID = getIntent().getStringExtra("SetID");
        duration = getIntent().getIntExtra("duration", 300000);

        timer = findViewById(R.id.timer);
        question = findViewById(R.id.question);
        LinearLayout optionsContainer = findViewById(R.id.options_container);
        OPTIONS = new Button[4];
        for (int i = 0; i < 4; i++) {
            OPTIONS[i] = (Button) optionsContainer.getChildAt(i);
        }
        previous = findViewById(R.id.previousBtn);
        next = findViewById(R.id.nextBtn);
        submit = findViewById(R.id.submitBtn);
        ANSWERS = new ArrayList<>();
        questionModelList = new ArrayList<>();
        populateQuestionList();
        setOptionListeners();
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAnimation(--currentPosition);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAnimation(++currentPosition);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTest();
            }
        });
    }

    private void setOptionListeners() {
        for (int i=0; i<4; i++){
            int finalI = i;
            OPTIONS[i].setOnClickListener(view -> {
                if (ANSWERS.get(currentPosition).isEmpty()){
                    Toast.makeText(this, "clear others", Toast.LENGTH_LONG).show();
                }
                ANSWERS.set(currentPosition, OPTIONS[finalI].getText().toString().substring(3));
                OPTIONS[finalI].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8888FF")));
            });
        }
    }

    private void submitTest() {
        int correct, incorrect, attempted, unattempted;
        correct = incorrect = attempted = unattempted = 0;
        for (int i=0; i<ANSWERS.size(); i++){
            if (ANSWERS.get(i).isEmpty())
                unattempted++;
            else if (ANSWERS.get(i).equals(questionModelList.get(i).getCorrectAns())){
                correct++;
                attempted++;
            }else {
                incorrect++;
                attempted++;
            }
        }
        getPreviousTestResults();
    }

    private void getPreviousTestResults() {
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        String json = preferences.getString(KEY_1, "");
        String json1 = preferences.getString(KEY_2, "");
        String json2 = preferences.getString(KEY_3, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        PREVIOUS_TEST_ID = gson.fromJson(json, type);
        PREVIOUS_TEST_MARKS = gson.fromJson(json1, type);
        PREVIOUS_TEST_TOTAL = gson.fromJson(json2, type);
        if (PREVIOUS_TEST_ID == null) {
            PREVIOUS_TEST_ID = new ArrayList<>();
            PREVIOUS_TEST_MARKS = new ArrayList<>();
            PREVIOUS_TEST_TOTAL = new ArrayList<>();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(NewTestActivity.this, "You cannot close this During Test", Toast.LENGTH_LONG).show();
        //super.onBackPressed();
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        selectionAdapter = new ListItemSelectionAdapter(ANSWERS.size(), currentPosition);
        selectionAdapter.setItemSelectedListener(new ListItemSelectionAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                playAnimation(currentPosition = position);
                if(position == 0){
                    next.setAlpha(1);
                    next.setEnabled(true);
                }else if (position == ANSWERS.size()-1){
                    previous.setAlpha(1);
                    previous.setEnabled(true);
                }
                //Toast.makeText(NewTestActivity.this, "position : "+currentPosition, Toast.LENGTH_LONG).show();
            }
        });
        recyclerView.setAdapter(selectionAdapter);
    }

    private void populateQuestionList() {
        ((TextView) loadingDialog.findViewById(R.id.textView)).setText("Downloading...");
        loadingDialog.show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Categories").document(categoryID)
                .collection(SetID).orderBy("setNo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean FIRST_TIME = true;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (FIRST_TIME) {
                            FIRST_TIME = false;
                            continue;
                        }
                        ANSWERS.add("");
                        questionModelList.add(new QuestionModel(
                                document.get("question").toString(),
                                document.get("optionA").toString(),
                                document.get("optionB").toString(),
                                document.get("optionC").toString(),
                                document.get("optionD").toString(),
                                document.get("correctAns").toString(),
                                (int) (long) document.get("setNo")
                        ));
                    }
                    setRecyclerView();
                    playAnimation(0);
                    setTimer();
                } else {
                    Toast.makeText(NewTestActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    private void setTimer() {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int hour, min, sec;
                millisUntilFinished /= 1000;
                sec = (int) millisUntilFinished % 60;
                millisUntilFinished /= 60;
                min = (int) millisUntilFinished % 60;
                millisUntilFinished /= 60;
                hour = (int) millisUntilFinished % 12;
                String time = hour==0?min + "m:" + sec + "s" : hour + "h:" + min + "m:" + sec + "s";
                timer.setText(time);
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                submitTest();
            }
        };
        countDownTimer.start();
    }

    private void playAnimation(int position) {
        recyclerView.scrollToPosition(position);
        selectionAdapter.update(position);
        selectionAdapter.notifyItemChanged(position);
        question.setText("Q." + (position + 1) + ". " + questionModelList.get(position).getQuestion());
        OPTIONS[0].setText("A. " + questionModelList.get(position).getOptionA());
        OPTIONS[1].setText("B. " + questionModelList.get(position).getOptionB());
        OPTIONS[2].setText("C. " + questionModelList.get(position).getOptionC());
        OPTIONS[3].setText("D. " + questionModelList.get(position).getOptionD());
        for (int i = 0; i < 4; i++) {
            if (OPTIONS[i].getText().toString().substring(3).equals(ANSWERS.get(position))) {
                OPTIONS[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8888FF")));
            } else {
                OPTIONS[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }
        }
        if (currentPosition == 0) {
            previous.setAlpha(0.5f);
            previous.setEnabled(false);
        } else if (currentPosition == ANSWERS.size() - 1) {
            next.setAlpha(0.5f);
            next.setEnabled(false);
        } else if (!previous.isEnabled()) {
            previous.setAlpha(1);
            previous.setEnabled(true);
        }else if (!next.isEnabled()){
            next.setAlpha(1);
            next.setEnabled(true);
        }
    }
}