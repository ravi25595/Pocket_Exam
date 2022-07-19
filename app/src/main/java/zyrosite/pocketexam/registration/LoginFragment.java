package zyrosite.pocketexam.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import zyrosite.pocketexam.R;
import zyrosite.pocketexam.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextView forgotPassword;
    private Button loginBtn;
    private EditText pass;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String Email, DisplayName, ID, PhotoUrl, Phone;
    private List EXAMS;
    private TextInputLayout textInputLayout_pass;

    public LoginFragment(String Phone, String Email, String DisplayName, String ID, String PhotoUrl, List EXAMS) {
        this.Phone = Phone;
        this.Email = Email;
        this.DisplayName = DisplayName;
        this.ID = ID;
        this.PhotoUrl = PhotoUrl;
        this.EXAMS = EXAMS;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        forgotPassword = view.findViewById(R.id.tv_forgot_password);
        loginBtn = view.findViewById(R.id.signInBtn);
        progressBar = view.findViewById(R.id.progressBar);
        pass = view.findViewById(R.id.edit_pass);
        mAuth = FirebaseAuth.getInstance();
        textInputLayout_pass = view.findViewById(R.id.textInputLayout_pass);
        forgotPassword.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_forgot_password:
                setFragment(new OtpFragment(Phone, 1), "OTP");
                break;
            case R.id.signInBtn:
                String number = Email;
                progressBar.setVisibility(View.VISIBLE);
                textInputLayout_pass.setError(null);
                mAuth.signInWithEmailAndPassword(Email, pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    getActivity().finish();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("DisplayName", DisplayName);
                                    intent.putExtra("Email", Email);
                                    intent.putExtra("ID", ID);
                                    intent.putExtra("PhotoUrl", PhotoUrl);
                                    intent.putStringArrayListExtra("EXAMS", (ArrayList<String>) EXAMS);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                                    textInputLayout_pass.setError(task.getException().getMessage());
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                break;
        }
    }
    private void setFragment(Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.registration_frameLayout, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }
}
