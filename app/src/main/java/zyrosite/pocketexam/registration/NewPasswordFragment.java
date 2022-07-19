package zyrosite.pocketexam.registration;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import zyrosite.pocketexam.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPasswordFragment extends Fragment {
    private PhoneAuthCredential phoneAuthCredential;
    private String phone;
    private FirebaseAuth mAuth;
    private EditText newPassword, confirmPassword;
    private Button resetPasswordBtn;
    private FirebaseUser user;
    private ProgressBar indicatorView;
    private TextInputLayout textInputLayout_old, textInputLayout_new, textInputLayout_confirm;
    public NewPasswordFragment() {
        // Required empty public constructor
    }

    public NewPasswordFragment(PhoneAuthCredential credential, String phone) {
        phoneAuthCredential = credential;
        this.phone = phone;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_password, container, false);
        mAuth = FirebaseAuth.getInstance();
        newPassword = view.findViewById(R.id.new_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        resetPasswordBtn = view.findViewById(R.id.reset_password_btn);
        indicatorView = view.findViewById(R.id.loadingIndicator);
        textInputLayout_old = view.findViewById(R.id.textInputLayout_old_password);
        textInputLayout_new = view.findViewById(R.id.textInputLayout_new_password);
        textInputLayout_confirm = view.findViewById(R.id.textInputLayout_confirm_password);
        indicatorView.setVisibility(View.INVISIBLE);
        textInputLayout_old.setVisibility(View.GONE);
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().length() < 8) {
                    textInputLayout_new.setError("password must be at least 8 character long.");
                    return;
                } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    textInputLayout_confirm.setError("Password Mismatch!");
                    textInputLayout_new.setError(null);
                    return;
                }else {
                    textInputLayout_new.setError(null);
                    textInputLayout_confirm.setError(null);
                }
                indicatorView.setVisibility(View.VISIBLE);
                resetPasswordBtn.setAlpha(0.5f);
                resetPasswordBtn.setEnabled(false);
                user = mAuth.getCurrentUser();
                if (user == null) {
                    mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = task.getResult().getUser();
                                user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Password updated successfully...", Toast.LENGTH_SHORT).show();
                                            setFragment("LOGIN");
                                        } else {
                                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        indicatorView.setVisibility(View.INVISIBLE);
                                        resetPasswordBtn.setAlpha(1);
                                        resetPasswordBtn.setEnabled(true);
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                indicatorView.setVisibility(View.INVISIBLE);
                                resetPasswordBtn.setAlpha(1);
                                resetPasswordBtn.setEnabled(true);
                            }
                        }
                    });
                } else {
                    user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password Updated successfully...", Toast.LENGTH_SHORT).show();
                                setFragment("LOGIN");
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            indicatorView.setVisibility(View.INVISIBLE);
                            resetPasswordBtn.setAlpha(1);
                            resetPasswordBtn.setEnabled(true);
                        }
                    });
                }
            }
        });
        return view;
    }

    private void setFragment(String TAG) {
        FragmentManager fm = getParentFragmentManager();
        //FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fm.popBackStack(TAG, 0);
        mAuth.signOut();
    }
}
