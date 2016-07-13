package com.madone.virtualexpo.android.totpgenerator.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.madone.virtualexpo.android.totpgenerator.utils.AES;
import com.madone.virtualexpo.android.totpgenerator.utils.HOTPAlgorithm;
import com.madone.virtualexpo.android.totpgenerator.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";


    private long currentUnixTime = 0;
    private long secondsMain;
    int secondsPassed = 0;
    long secondsToIterate = 0;
    int countDownTimerCounter = 0;
    long previousSecondsToFinish = 0;
    int percentage;


    private TextView mCodeTextView;
    private TextView mCounterTextView;
    private ProgressBar mProgressBar;

    private String pass = "12345678901234567890";
    private String mGeneratedKey;
    private long t;
    byte[] code;
    private long seconds;
    private long millisInFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCodeTextView = (TextView) findViewById(R.id.codeTextView);
        mCounterTextView = (TextView) findViewById(R.id.counterTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.circular_progressbar);

        Long currentUnixTime = System.currentTimeMillis() / 1000L;
        secondsPassed = (int) (currentUnixTime - seconds);
        secondsToIterate =  30 - System.currentTimeMillis() / 1000L % 30;

        String secretKey = null;
/*
        boolean previouslyStarted = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean("SECOND_TIME", false);
*/
        Intent i = getIntent();
        String previousActivity = i.getStringExtra("FROM_ACTIVITY");

        if (previousActivity.equals("Camera")) {
            try {
                secretKey = PinActivity.SHA1(PinActivity.mPinCode);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (previousActivity.equals("Lock")) {
            try {
                secretKey = PinActivity.SHA1(ScreenLockActivity.PIN);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        secretKey = secretKey.substring(0, 16);

        code = AES.decrypt(secretKey,
                PreferenceManager.getDefaultSharedPreferences(this).getString("IV", "iv error"),
                PreferenceManager.getDefaultSharedPreferences(this).getString("E_TOKEN", "e_token error")).getBytes();
        t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

        try {
            mGeneratedKey = HOTPAlgorithm.generateOTP(code, t, 6, false, 0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        mCodeTextView.setText(mGeneratedKey+"");
        timer();
    }

    private void timer() {
        if (countDownTimerCounter == 0)
        {
            new CountDownTimer(secondsToIterate * 1000, 500) {

                public void onTick(long millisUntilFinished)
                {
                    secondsMain = millisUntilFinished / 1000;
                    percentage = (int) (100 * secondsMain) / 30;
                    mProgressBar.setProgress(percentage);
                    mCounterTextView.setText(secondsMain + "");
                    currentUnixTime = System.currentTimeMillis() / 1000L;

                    t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

                }

                public void onFinish() {
                    countDownTimerCounter = 1;
                    t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

                    try {
                        mGeneratedKey = HOTPAlgorithm.generateOTP(code, t, 6, false, 0);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }

                    mCodeTextView.setText(mGeneratedKey + "");

                    mProgressBar.setProgress(100);
                    timer();
                }
            }.start();
        }
        else{
            new CountDownTimer(30000, 500) {

                public void onTick(long millisUntilFinished) {
                    secondsMain = millisUntilFinished / 1000;
                    percentage = (int) (100 * secondsMain) / 30;

                    mProgressBar.setProgress(percentage);
                    mCounterTextView.setText(secondsMain + "");
                    currentUnixTime = System.currentTimeMillis() / 1000L;


                    t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

                }

                public void onFinish() {
                    t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

                    try {
                        mGeneratedKey = HOTPAlgorithm.generateOTP(code, t, 6, false, 0);

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }

                    mCodeTextView.setText(mGeneratedKey + "");

                    mProgressBar.setProgress(100);
                    timer();
                }
            }.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .clear()
                        .apply();

                Intent intent = new Intent(getApplicationContext(), PinActivity.class);

                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = getIntent();
        String previousActivity = i.getStringExtra("FROM_ACTIVITY");

        if (previousActivity.equals("Camera")) {
            Intent intent = new Intent(getApplicationContext(), ScreenLockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else if (previousActivity.equals("Lock")) {
            Intent intent = new Intent(getApplicationContext(), ScreenLockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }
}