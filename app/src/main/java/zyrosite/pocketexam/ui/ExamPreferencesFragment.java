package zyrosite.pocketexam.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import zyrosite.pocketexam.CategoryModel;
import zyrosite.pocketexam.R;

public class ExamPreferencesFragment extends Fragment {
    private List<CategoryModel> categoryModels;
    private ExamListAdapter adapter;
    private ProgressBar progressBar;

    public ExamPreferencesFragment(Context mContext) {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Choose Exams");
        View view = inflater.inflate(R.layout.fragment_exam_preferences, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        categoryModels = new ArrayList<>();
        getData();
        adapter = new ExamListAdapter(categoryModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Categories").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot:task.getResult()) {
                                categoryModels.add(new CategoryModel(
                                        snapshot.get("name").toString(),
                                        snapshot.get("url").toString(),
                                        snapshot.getId()
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}