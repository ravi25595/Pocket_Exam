package zyrosite.pocketexam.registration;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import zyrosite.pocketexam.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneNumberFragment extends Fragment {
    private FrameLayout frameLayout;
    private EditText phone;
    private Button nextBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    public PhoneNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number, container, false);
        frameLayout = container.findViewById(R.id.registration_frameLayout);
        nextBtn = view.findViewById(R.id.next_btn);
        phone = view.findViewById(R.id.edit_email);
        progressBar = view.findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if (s.toString().length() == 10){
                    phone.setError(null);
                } else {
                    phone.setError("Please enter valid mobile number");
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    nextBtn.setVisibility(View.VISIBLE);
                } else {
                    nextBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                nextBtn.setAlpha(0.5f);
                nextBtn.setEnabled(false);
                firestore.collection("Users").whereEqualTo("PhoneNumber", phone.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        setFragment(new OtpFragment(phone.getText().toString(), 0), "OTP");
                                    } else {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            setFragment(new LoginFragment(
                                                    phone.getText().toString(),
                                                    document.get("Email").toString(),
                                                    document.get("DisplayName").toString(),
                                                    document.get("ID").toString(),
                                                    document.get("PhotoUrl").toString(),
                                                    (List<String>) document.get("EXAMS")
                                            ), "LOGIN");
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    task.getException().printStackTrace();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                nextBtn.setAlpha(1);
                                nextBtn.setEnabled(true);
                            }
                        });
            }
        });
        return view;
    }

    private void setFragment(Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }
}
