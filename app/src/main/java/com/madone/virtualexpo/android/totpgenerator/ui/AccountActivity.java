package com.madone.virtualexpo.android.totpgenerator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.madone.virtualexpo.android.totpgenerator.R;

public class AccountActivity extends AppCompatActivity {

    private FloatingActionButton mAddAccountButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        setTitle("Safe");
        
        mAddAccountButton = (FloatingActionButton) findViewById(R.id.fab);
        mAddAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityAddAccount.class));

                // DialogFragment fragment = AccountsDialogFragment.newInstance();
                // fragment.show(getSupportFragmentManager(), "account_fragment");
            }
        });

    }

    private void showDialogIfNeeded(Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Account");
        builder.setMessage(R.string.qr_code_desc);
    }
}