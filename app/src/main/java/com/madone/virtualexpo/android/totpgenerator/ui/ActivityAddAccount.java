package com.madone.virtualexpo.android.totpgenerator.ui;

import android.Manifest;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.madone.virtualexpo.android.totpgenerator.utils.AES;
import com.madone.virtualexpo.android.totpgenerator.camera.FullScannerFragmentActivity;
import com.madone.virtualexpo.android.totpgenerator.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ActivityAddAccount extends AppCompatActivity {

    private Button mAddButton;
    private TextView mNoQRCode;

    private ZXingScannerView mScannerView;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        mNoQRCode = (TextView) findViewById(R.id.no_qr);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Account");
        builder.setMessage("Please enter the key for this account.");

        final EditText editText = new EditText(this);
        builder.setView(editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mToken = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String iv;

                    String secretKey = PinActivity.SHA1(PinActivity.mPinCode);
                    secretKey = secretKey.substring(0, 16);

                    iv = PinActivity.SHA1(AES.generateIv());
                    iv = iv.substring(0, 16);

                    PreferenceManager.getDefaultSharedPreferences(ActivityAddAccount.this)
                            .edit()
                            .putString("E_TOKEN", AES.encrypt(secretKey, iv, mToken))
                            .apply();

                    PreferenceManager.getDefaultSharedPreferences(ActivityAddAccount.this)
                            .edit()
                            .putString("IV", iv)
                            .apply();

                    PreferenceManager.getDefaultSharedPreferences(ActivityAddAccount.this)
                            .edit()
                            .putBoolean("SECOND_TIME", true)
                            .apply();

                    Log.i("DECODING", AES.decrypt(secretKey, iv, AES.encrypt(secretKey, iv, mToken)));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(ActivityAddAccount.this, MainActivity.class);
                i.putExtra("FROM_ACTIVITY", "Camera");
                startActivity(i);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();

        mNoQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = AddAccountDialogFragment.newInstance();
                dialog.show(ActivityAddAccount.this.getFragmentManager(), "AddAccountDialogFragment");
            }
        });

        mAddButton = (Button) findViewById(R.id.button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(FullScannerFragmentActivity.class);
            }
        });
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
