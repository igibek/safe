package com.madone.virtualexpo.android.totpgenerator.camera;

import android.os.Bundle;

import com.madone.virtualexpo.android.totpgenerator.R;

public class FullScannerFragmentActivity extends BaseScannerActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_full_scanner_fragment);
        setupToolbar();
    }
}