package zyrosite.pocketexam.ui;

import static zyrosite.pocketexam.ScoreActivity.FILE_NAME;
import static zyrosite.pocketexam.ScoreActivity.KEY_1;
import static zyrosite.pocketexam.ScoreActivity.KEY_2;
import static zyrosite.pocketexam.ScoreActivity.KEY_3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import zyrosite.pocketexam.CategoryModel;
import zyrosite.pocketexam.R;
import zyrosite.pocketexam.SetModel;
import zyrosite.pocketexam.StudyMaterialModel;

public class HomeFragment extends Fragment {
    private Context mContext;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private LinearLayoutManager manager;
    private List<HomePageModel> homePageModelList;
    private HomePageAdapter adapter;
    private boolean isScrolling = false, isLastDocument = false;
    private int currentItems, totalItems, scrolledOutItems, LIMIT = 8;

    private DocumentSnapshot cursor;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<String> PREVIOUS_TEST_ID, PREVIOUS_TEST_MARKS, PREVIOUS_TEST_TOTAL;

    public HomeFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setTitle("Home");
        getActivity().getActionBar().setLogo(R.drawable.new_logo);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firestore = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        homePageModelList = new ArrayList<>();
        getCategoryList();
        getTestList();
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new HomePageAdapter(homePageModelList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrolledOutItems = manager.findFirstVisibleItemPosition();
                if (isScrolling && (!isLastDocument && totalItems == currentItems + scrolledOutItems)) {
                    isScrolling = false;
                    try {
                        fetchMoreData();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return view;
    }

    private void fetchMoreData() {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("HomePage").whereIn("categoryID", MainActivity.PREFERRED_EXAMS)
                .whereEqualTo("published", true).orderBy("date", Query.Direction.DESCENDING).startAfter(cursor)
                .limit(LIMIT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int type = document.get("type") == null ? 2 : (int) (long) document.get("type");
                                switch (type) {
                                    case 0:
                                        homePageModelList.add(new HomePageModel(
                                                0, document.toObject(StudyMaterialModel.class)
                                        ));
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        SetModel setModel = document.toObject(SetModel.class);
                                        if (PREVIOUS_TEST_ID.contains(setModel.getCategoryID())) {
                                            homePageModelList.add(new HomePageModel(
                                                    3, setModel
                                            ));
                                        } else {
                                            homePageModelList.add(new HomePageModel(
                                                    2, setModel
                                            ));
                                        }
                                }
                                cursor = document;
                            }
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onResume() {
        super.onResume();
        getPreviousTestResults();
        adapter.notifyDataSetChanged();
    }

    private void getCategoryList() {
        firestore.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> map = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        map.put(document.getId(), new CategoryModel(
                                document.get("name").toString(),
                                document.get("url").toString(),
                                document.getId()
                        ));
                    }
                    adapter.setCategoryMap(map);
                }
            }
        });
    }

    private void getTestList() {
        if (MainActivity.PREFERRED_EXAMS == null || MainActivity.PREFERRED_EXAMS.size() == 0) {
            Toast.makeText(getContext(), "No exams are selected. Please go to Exam Preferences from drawer menu", Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("HomePage").whereIn("categoryID", MainActivity.PREFERRED_EXAMS)
                .whereEqualTo("published", true).orderBy("date", Query.Direction.DESCENDING).limit(LIMIT).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int type = document.get("type") == null ? 2 : (int) (long) document.get("type");
                                switch (type) {
                                    case 0:
                                        homePageModelList.add(new HomePageModel(
                                                        0, document.toObject(StudyMaterialModel.class)
                                                )
                                        );
                                        StudyMaterialModel model = document.toObject(StudyMaterialModel.class);
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        SetModel setModel = document.toObject(SetModel.class);
                                        if (PREVIOUS_TEST_ID.contains(setModel.getCategoryID())) {
                                            homePageModelList.add(new HomePageModel(
                                                    3, setModel
                                            ));
                                        } else {
                                            homePageModelList.add(new HomePageModel(
                                                    2, setModel
                                            ));
                                        }
                                }
                                cursor = document;
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), homePageModelList.size() + "", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getPreviousTestResults() {
        preferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
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
        //Toast.makeText(getContext(), PREVIOUS_TEST_ID.size()+"T", Toast.LENGTH_SHORT).show();
        adapter.setPreviousResults(PREVIOUS_TEST_ID, PREVIOUS_TEST_MARKS, PREVIOUS_TEST_TOTAL);
    }
}