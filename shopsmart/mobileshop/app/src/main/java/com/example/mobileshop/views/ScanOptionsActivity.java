package com.example.mobileshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileshop.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScanOptionsActivity extends AppCompatActivity {

    private Button btnScanBarcode, btnScanQr;
    private ActivityResultLauncher<ScanOptions> scanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_options);

        // Initialize UI elements
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        btnScanQr = findViewById(R.id.btnScanQr);

        // Initialize the scan launcher using the new ScanContract
        scanLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                // Scan was cancelled
                Toast.makeText(ScanOptionsActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // A successful scan; pass the scanned code back to the calling activity/fragment
                String scannedCode = result.getContents();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("scannedCode", scannedCode);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Launch scan for 1D barcodes
        btnScanBarcode.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            // Specify 1D barcode formats using the provided constant:
            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
            options.setPrompt("Place the barcode inside the scan area");
            options.setCameraId(0);  // Typically the rear-facing camera
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            scanLauncher.launch(options);
        });

        // Launch scan for QR codes
        btnScanQr.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            // Specify QR code format
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Place the QR code inside the scan area");
            options.setCameraId(0);  // Typically the rear-facing camera
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            scanLauncher.launch(options);
        });
    }
}
