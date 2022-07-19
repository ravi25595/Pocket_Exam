package zyrosite.pocketexam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {
    public static final String FILE_NAME = "PREVIOUSTESTRESULTS";
    public static final String KEY_1 = "TESTIDS";
    public static final String KEY_2 = "MARKS";
    public static final String KEY_3 = "TOTAL";

    private String SetID;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private TextView score, total;
    private Button doneBtn;
    private TextView TOTAL, CORRECT, INCORRECT, ATTEMPTED, UNATTEMPTED;
    private List<QuestionModel> QUESTIONS;
    private List<String> ANSWERS;
    private GridView gridView;
    private List<String> PREVIOUS_TEST_ID, PREVIOUS_TEST_MARKS, PREVIOUS_TEST_TOTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.score);
        total = findViewById(R.id.total);
        doneBtn = findViewById(R.id.doneBtn);

        TOTAL = findViewById(R.id.total_questions);
        CORRECT = findViewById(R.id.correct_answers);
        INCORRECT = findViewById(R.id.incorrect_answers);
        ATTEMPTED = findViewById(R.id.attempted);
        UNATTEMPTED = findViewById(R.id.unAttempted);

        SetID = getIntent().getStringExtra("SetID");
        getPreviousTestResults();
        calculateScore();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridView = findViewById(R.id.gridView);
        final GridListAdapter adapter = new GridListAdapter(QUESTIONS, ANSWERS);
        adapter.setItemSelectedListener(new GridListAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                /*
                Intent ReviewIntent = new Intent(ScoreActivity.this, ReviewTestActivity.class);
                ReviewIntent.putExtra("position", position);
                ReviewIntent.putExtra("answer_type", adapter.getAnswerType());

                Bundle args = new Bundle();
                args.putSerializable("Questions", (Serializable) QUESTIONS);
                args.putStringArrayList("Answers", (ArrayList<String>) ANSWERS);
                ReviewIntent.putExtras(args);
                startActivity(ReviewIntent);
                 */
            }
        });
        gridView.setAdapter(adapter);
        getPreviousTestResults();
        if (!PREVIOUS_TEST_ID.contains(SetID)) {
            PREVIOUS_TEST_ID.add(SetID);
            PREVIOUS_TEST_MARKS.add(score.getText().toString());
            PREVIOUS_TEST_TOTAL.add(total.getText().toString());
            storePreviousTestIDs();
        }
    }

    private void calculateScore() {
        int correct, incorrect, attempted, unattempted;
        correct = incorrect = attempted = unattempted = 0;
        for (int i = 0; i < QUESTIONS.size(); i++) {
            if (ANSWERS.get(i).isEmpty()) {
                unattempted++;
            } else if (QUESTIONS.get(i).getCorrectAns().equals(ANSWERS.get(i))) {
                correct++;
                attempted++;
            } else {
                incorrect++;
                attempted++;
            }
        }
        score.setText(String.format("%d", correct));
        total.setText("" + QUESTIONS.size());

        TOTAL.setText("Total : " + QUESTIONS.size());
        CORRECT.setText("Correct : " + correct);
        INCORRECT.setText("Incorrect : " + incorrect);
        ATTEMPTED.setText("Attempted : " + attempted);
        UNATTEMPTED.setText("Unattempted : " + unattempted);
    }

    private void getPreviousTestResults() {
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        String json = preferences.getString(KEY_1, "");
        String json1 = preferences.getString(KEY_2, "");
        String json2 = preferences.getString(KEY_3, "");
        String json3 = preferences.getString(SetID.substring(4), "");
        String json4 = preferences.getString(SetID, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        Type type1 = new TypeToken<List<QuestionModel>>() {
        }.getType();

        PREVIOUS_TEST_ID = gson.fromJson(json, type);
        PREVIOUS_TEST_MARKS = gson.fromJson(json1, type);
        PREVIOUS_TEST_TOTAL = gson.fromJson(json2, type);
        QUESTIONS = gson.fromJson(json3, type1);
        ANSWERS = gson.fromJson(json4, type);

        if (PREVIOUS_TEST_ID == null) {
            PREVIOUS_TEST_ID = new ArrayList<>();
            PREVIOUS_TEST_MARKS = new ArrayList<>();
            PREVIOUS_TEST_TOTAL = new ArrayList<>();
        }
    }

    private void storePreviousTestIDs() {
        String json = gson.toJson(PREVIOUS_TEST_ID);
        String json1 = gson.toJson(PREVIOUS_TEST_MARKS);
        String json2 = gson.toJson(PREVIOUS_TEST_TOTAL);
        String json3 = gson.toJson(ANSWERS);
        editor.putString(KEY_1, json);
        editor.putString(KEY_2, json1);
        editor.putString(KEY_3, json2);
        editor.putString(SetID, json3);
        editor.commit();
    }
}
