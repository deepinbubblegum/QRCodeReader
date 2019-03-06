package com.deepin.qrcodereader;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {
    private Button scan_btn,gen_btn,coy_btn;
    private EditText txt;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = findViewById(R.id.scan_btn);
        txt = findViewById(R.id.editText);
        coy_btn = findViewById(R.id.coy_btn);
        gen_btn = findViewById(R.id.gen_btn);
        imageView = findViewById(R.id.qrView);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("SCAN");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        coy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipData = ClipData.newPlainText("text", txt.getText());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(), "คัดลอกเรียบร้อย", Toast.LENGTH_SHORT).show();
            }
        });

        gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                if (txt.getText().toString().trim().length() == 0){
                    Toast.makeText(getApplicationContext(),"ไม่พบข้อมูล!", Toast.LENGTH_SHORT).show();
                }else {
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(txt.getText().toString(), BarcodeFormat.QR_CODE,1024,1024);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        imageView.setImageBitmap(bitmap);
                    }catch (WriterException e){
                        Toast.makeText(getApplicationContext(),"Error " + e, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "ยกเลิกการ สแกน QR", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                txt.setText(result.getContents());
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
