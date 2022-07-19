package zyrosite.pocketexam;

import androidx.appcompat.app.AppCompatActivity;
import com.joanzapata.pdfview.PDFView;
import android.os.Bundle;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        PDFView pdf = findViewById(R.id.pdf_view);
        String filename = getIntent().getStringExtra("path");
        File file = new File(filename);
        pdf.fromFile(file)
                .swipeVertical(true)
                .enableSwipe(true)
                .showMinimap(true)
                .load();
    }
}