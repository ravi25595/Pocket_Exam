package zyrosite.pocketexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import zyrosite.pocketexam.registration.RegistrationActivity;
import zyrosite.pocketexam.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String Email, DisplayName, ID, PhotoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try{
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            } else {
                ID = firebaseAuth.getCurrentUser().getUid();
                firestore.collection("Users").document(ID).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    intent.putExtra("ID", ID);
                                    DocumentSnapshot document = task.getResult();
                                    intent.putExtra("Email", document.get("Email").toString());
                                    intent.putExtra("DisplayName", document.get("DisplayName").toString());
                                    intent.putExtra("Phone", document.get("PhoneNumber").toString());
                                    intent.putExtra("PhotoUrl", document.get("PhotoUrl").toString());
                                    List<String> EXAMS = (List<String>) document.get("EXAMS");
                                    intent.putStringArrayListExtra("EXAMS", (ArrayList<String>) EXAMS);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SplashActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }catch (Exception e){
            Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}