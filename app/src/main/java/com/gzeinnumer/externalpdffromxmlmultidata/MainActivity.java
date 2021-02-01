package com.gzeinnumer.externalpdffromxmlmultidata;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gzeinnumer.externalpdffromxmlmultidata.pdfMulti.PDFMultiCreator;
import com.gzeinnumer.externalpdffromxmlmultidata.pdfMulti.RVAdapterForPDF;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PDFMultiCreator.PDFCallBack {

    private RVAdapterForPDF adapter;
    private RecyclerView rv;
    private final List<RVAdapterForPDF.MyModelPDF> list = new ArrayList<>();
    private TextView btnPdfPath;
    private Button btnSharePdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        btnSharePdfFile = findViewById(R.id.btn_share_pdf);
        btnPdfPath = findViewById(R.id.btn_pdf_path);

        adapter = new RVAdapterForPDF();
        for(int i=0; i<30; i++){
            list.add(new RVAdapterForPDF.MyModelPDF(i, "Item "+i, "Code", ""));
        }
        adapter.setList(list);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.hasFixedSize();

        findViewById(R.id.btn_create_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFMultiCreator.generatePdfReport(list, getApplicationContext(), MainActivity.this);
            }
        });
    }

    @Override
    public void callBackPath(final String path) {
        if (path != null) {
            btnPdfPath.setText("PDF path : " + path);
            btnSharePdfFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePdf(path);
                }
            });
        }
    }

    private void sharePdf(String fileName) {
        //kode ini penting ungutuk memaksa agar aplikasi luar bsa mengakses data yang kita butuh kan
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ArrayList<Uri> uris = new ArrayList<Uri>();
        Uri u = Uri.fromFile(new File(fileName));
        uris.add(u);

        final Intent sendToIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sendToIntent.setType("application/pdf");
        sendToIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        sendToIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        sendToIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        sendToIntent.putExtra(Intent.EXTRA_TEXT, "message");
        sendToIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        try {
            startActivity(Intent.createChooser(sendToIntent, "Send mail..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }
}