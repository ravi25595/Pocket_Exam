package zyrosite.pocketexam.registration;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import zyrosite.pocketexam.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpFragment extends DialogFragment {
    private int DESTINATION;
    private String phone;
    private EditText otpET[];
    private LinearLayout otpContainer;
    private TextView tv_phone;
    private Button resendBtn, verifyBtn;
    private ProgressBar progressBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Timer timer;
    private int count = 60;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private FirebaseAuth firebaseAuth;

    public OtpFragment(String phone, int DESTINATION) {
        this.phone = phone;
        this.DESTINATION = DESTINATION;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        init(view);
        tv_phone.setText("OTP has been sent to +91 " + phone);
        sendOTP();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (count == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendBtn.setEnabled(true);
                            resendBtn.setAlpha(1);
                            resendBtn.setText("RESEND OTP");
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendBtn.setText("Resend in " + count);
                        }
                    });
                    count--;
                }
            }
        }, 0, 1000);
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
                resendBtn.setEnabled(false);
                resendBtn.setAlpha(0.5f);
                count = 60;
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "";
                for (int i = 0; i < 6; i++) {
                    code += otpET[i].getText().toString();
                }
                if (code.length() != 6) {
                    otpET[5].setError("Invalid OTP");
                    return;
                }
                Toast.makeText(getContext(), code, Toast.LENGTH_LONG).show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithCredential(credential);
            }
        });
        return view;
    }

    private void signInWithCredential(final PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            switch (DESTINATION) {
                                case 0:
                                    FirebaseUser user = task.getResult().getUser();
                                    user.delete();
                                    setFragment(new SignUpFragment(credential), "SignUp");
                                    break;
                                case 1:
                                    setFragment(new NewPasswordFragment(credential, phone), "NewPass");
                                    break;
                            }
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void resendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                mCallbacks,
                mToken
        );
    }

    private void sendOTP() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                Toast.makeText(getContext(), "sms code " + code, Toast.LENGTH_LONG).show();
                /*if (code != null){
                    for (int i=0; i<6; i++){
                        otpET[i].setText(code.charAt(i));
                    }
                }*/
                switch (DESTINATION){
                    case 0:
                        setFragment(new SignUpFragment(phoneAuthCredential, phone), "SignUp");
                        break;
                    case 1:
                        setFragment(new NewPasswordFragment(phoneAuthCredential, phone), "NewPass");
                        break;
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException)
                    otpET[5].setError("Invalid OTP");
                else
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
                mToken = token;
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                mCallbacks
        );
    }

    private void init(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        verifyBtn = view.findViewById(R.id.verifyBtn);
        resendBtn = view.findViewById(R.id.resendBtn);
        tv_phone = view.findViewById(R.id.tv_phone);
        otpContainer = view.findViewById(R.id.otpETContainer);
        otpET = new EditText[6];
        for (int i = 0; i < 6; i++) {
            otpET[i] = (EditText) otpContainer.getChildAt(i);
            otpET[i].addTextChangedListener(new GenericTextWatcher(otpET[i]));
        }
    }

    private void setFragment(Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.registration_frameLayout, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private class GenericTextWatcher implements TextWatcher {
        private EditText view;

        public GenericTextWatcher(EditText view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            switch (view.getId()) {
                case R.id.otpET0:
                    if (text.length() == 1)
                        otpET[1].requestFocus();
                    break;
                case R.id.otpET1:
                    if (text.length() == 1)
                        otpET[2].requestFocus();
                    else if (text.length() == 0)
                        otpET[0].requestFocus();
                    break;
                case R.id.otpET2:
                    if (text.length() == 1)
                        otpET[3].requestFocus();
                    else if (text.length() == 0)
                        otpET[1].requestFocus();
                    break;
                case R.id.otpET3:
                    if (text.length() == 1)
                        otpET[4].requestFocus();
                    else if (text.length() == 0)
                        otpET[2].requestFocus();
                    break;
                case R.id.otpET4:
                    if (text.length() == 1)
                        otpET[5].requestFocus();
                    else if (text.length() == 0)
                        otpET[3].requestFocus();
                    break;
                case R.id.otpET5:
                    if (text.length() == 0)
                        otpET[4].requestFocus();
                    break;
            }
        }
    }
}
