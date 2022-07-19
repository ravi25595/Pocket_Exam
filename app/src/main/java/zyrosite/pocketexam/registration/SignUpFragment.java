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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.FirebaseFirestore;
import zyrosite.pocketexam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {
    private PhoneAuthCredential phoneAuthCredential;
    private FrameLayout frameLayout;
    private TextView tv_login;
    private EditText userET, emailET, passET, confirmPassET;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Button createBtn;
    private ProgressBar progressBar;
    private String userID, phone;
    public SignUpFragment(PhoneAuthCredential phoneAuthCredential, String phone) {
        this.phoneAuthCredential = phoneAuthCredential;
        this.phone = phone;
    }

    public SignUpFragment(PhoneAuthCredential phoneAuthCredential) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        frameLayout = container.findViewById(R.id.registration_frameLayout);
        userET = view.findViewById(R.id.edit_userName);
        emailET = view.findViewById(R.id.edit_email);
        passET = view.findViewById(R.id.edit_pass);
        confirmPassET = view.findViewById(R.id.edit_confirm_pass);
        createBtn = view.findViewById(R.id.createBtn);
        tv_login = view.findViewById(R.id.tv_sign_in);
        progressBar = view.findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        createBtn.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        return view;
    }

    private void setFragment(Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_sign_in:
                //isLoginFragment = true;
                //setFragment(new LoginFragment(document.get("Email").toString()));
                break;
            case R.id.createBtn:
                createPassword();
                break;
        }
    }

    private void createPassword() {
        final String userName = userET.getText().toString();
        final String email = emailET.getText().toString();
        final String pass = passET.getText().toString();
        String confirmPass = confirmPassET.getText().toString();
        if (!checkInputs(userName, email, pass, confirmPass))
            return;
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    userID = user.getUid();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
                    user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("ID", userID);
                                map.put("DisplayName", userName);
                                map.put("Password", pass);
                                map.put("Email", email);
                                map.put("PhoneNumber", phone);
                                map.put("PhotoUrl", "");
                                //map.put("DEVICE_MODEL", android.os.Build.)
                                firestore.collection("Users").document(userID).set(map);
                                getActivity().finish();
                                /*
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("DisplayName", userName);
                                intent.putExtra("Email", email);
                                intent.putExtra("ID", userID);
                                intent.putExtra("PhotoUrl", "");
                                intent.putStringArrayListExtra("EXAMS", new ArrayList<String>());
                                startActivity(intent);
                                 */
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }

    private boolean checkInputs(String userName, String email, String pass, String confirmPass) {
        if (userName.isEmpty()) {
            userET.setError("please enter username");
            return false;
        }
        if (email.isEmpty()) {
            emailET.setError("required!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Invalid email!");
            return false;
        }
        if (pass.length() < 8) {
            passET.setError("password must be atleast 8 characters");
            return false;
        }
        if (!pass.equals(confirmPass)) {
            confirmPassET.setError("password mismatch");
            return false;
        }
        return true;
    }
}
