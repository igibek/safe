package com.madone.virtualexpo.android.totpgenerator.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.madone.virtualexpo.android.totpgenerator.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class ScreenLockActivity extends AppCompatActivity {

    public static String PIN;
    private EditText mPinEditText;
    private TextView mErrorTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlock);
/*
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .clear()
                .apply();
*/
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        mErrorTextView = (TextView) findViewById(R.id.pin_error);

        boolean previouslyStarted = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean("SECOND_TIME", false);

        if (!previouslyStarted) {
            startActivity(new Intent(this, PinActivity.class));
        }

        mPinEditText = (EditText) findViewById(R.id.pin_edit_text);
        mPinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PIN = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final String attemptCounter = PreferenceManager.getDefaultSharedPreferences(this).
                getString("ATTEMPT_COUNTER", "0");
        int attemptInt = Integer.parseInt(attemptCounter);

        if (attemptInt > 0) {
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText("Attempts left: " + (10 - attemptInt));
        }

        final String pinCode = PreferenceManager.getDefaultSharedPreferences(this).
                getString("PIN_CODE", null);
        final String salt = PreferenceManager.getDefaultSharedPreferences(this).
                getString("SALT_CODE", null);

        if(isReadyToTry()) {
            mPinEditText.setFocusable(true);
        } else {
            mPinEditText.setFocusable(false);
            mErrorTextView.setText("Attempts left: " + (10 - attemptInt));
        }

        mPinEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPin(pinCode, salt);

                    return true;
                }

                return false;
            }
        });

        mPinEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isReadyToTry()) {
                    timeLeftFormat();
                } else {
                    Log.i("onclick()", "ready");
                    // mPinEditText.setFocusable(true);
                    mPinEditText.setFocusableInTouchMode(true);
                    mPinEditText.setFocusable(true);
                }
            }
        });

    }

    private void checkPin(String pinCode, String salt){

        if(isReadyToTry()) {
            mPinEditText.setFocusable(true);
        } else {
            mPinEditText.setFocusable(false);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mPinEditText.getWindowToken(), 0);
            return;
        }

        try {

            final String attemptCounter = PreferenceManager.getDefaultSharedPreferences(this).
                    getString("ATTEMPT_COUNTER", "0");
            Log.d("TEST_CLICKABLE", "COUNTER: " + attemptCounter);
            if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 0)
            {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 9");
                mPinEditText.setText("");

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "1")
                        .apply();

            }else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 1) {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 8");
                mPinEditText.setText("");

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "2")
                        .apply();
            }else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 2)
            {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 7");
                mPinEditText.setText("");

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "3")
                        .apply();
            }else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 3) //fourth wrong attemp
            {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 6");
                mPinEditText.setText("");

                Long nextAttemptTime = System.currentTimeMillis() / 1000L + 20;

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong("NEXT_ATTEMPT_TIME", nextAttemptTime)
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "4")
                        .apply();

                mPinEditText.setFocusable(false);

            } else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 4) //fifth wrong attemp
            {

                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 5");
                mPinEditText.setText("");

                Long nextAttemptTime = System.currentTimeMillis() / 1000L + 30;

//                    Toast.makeText(ScreenLockActivity.this, (
//                                    System.currentTimeMillis()/1000L - nextAttemptTime < 0) ?                         , Toast.LENGTH_SHORT).show();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong("NEXT_ATTEMPT_TIME", nextAttemptTime)
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "5")
                        .apply();

                mPinEditText.setFocusable(false);

            } else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 5) //fifth wrong attempt
            {
                mErrorTextView.setVisibility(View.VISIBLE);

                mErrorTextView.setText("Wrong Pin, Attempts left 4");
                mPinEditText.setText("");

                Long nextAttemptTime = System.currentTimeMillis() / 1000L + 40;

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong("NEXT_ATTEMPT_TIME", nextAttemptTime)
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "6")
                        .apply();

                mPinEditText.setFocusable(false);

            } else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 6) //fifth wrong attemp
            {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 3");
                mPinEditText.setText("");

                Long nextAttemptTime = System.currentTimeMillis() / 1000L + 50;

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong("NEXT_ATTEMPT_TIME", nextAttemptTime)
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "7")
                        .apply();

                mPinEditText.setFocusable(false);

            } else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 7) //fifth wrong attemp
            {

                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 2");
                mPinEditText.setText("");

                Long nextAttemptTime = System.currentTimeMillis() / 1000L + 60;

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong("NEXT_ATTEMPT_TIME", nextAttemptTime)
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "8")
                        .apply();

                mPinEditText.setFocusable(false);

            } else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 8) //fifth wrong attemp
            {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Wrong Pin, Attempts left 1");
                mPinEditText.setText("");

                Long nextAttemptTime = System.currentTimeMillis() / 1000L + 70;

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong("NEXT_ATTEMPT_TIME", nextAttemptTime)
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "9")
                        .apply();

                mPinEditText.setFocusable(false);
            } else if(!PinActivity.SHA1(PIN + salt).equals(pinCode) && Integer.parseInt(attemptCounter) == 9) //fifth wrong attemp
            {
                mErrorTextView.setVisibility(View.VISIBLE);
                mPinEditText.setText("");

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("E_TOKEN", "0")
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "0")
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("SALT_CODE", "0")
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("PIN_CODE", "0")
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("IV", "0")
                        .apply();
                mPinEditText.setFocusable(false);

                Intent i = new Intent(this, PinActivity.class);
                startActivity(i);
            } else {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("ATTEMPT_COUNTER", "0")
                        .apply();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("FROM_ACTIVITY", "Lock");

                startActivity(i);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private boolean isReadyToTry(){
        return PreferenceManager.getDefaultSharedPreferences(this).getLong("NEXT_ATTEMPT_TIME", 0) <=
                System.currentTimeMillis() / 1000L;
    }

    private void timeLeftFormat() {
        String time = "";

        long timeleft = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getLong("NEXT_ATTEMPT_TIME", 0) - System.currentTimeMillis()/1000L;
        Log.d("seconds", timeleft + "");
        if (timeleft < 60) {
            time = timeleft + " seconds left";
        } else if (timeleft >= 60 && timeleft < 3600 ) {
            long secondsLeft = timeleft % 60;
            time = timeleft / 60 + " minutes " + ((secondsLeft == 0) ? "" : (secondsLeft + " seconds ")) + "left";
        } else if (timeleft >= 3600) {
            long secondsLeft = (timeleft % 3600) % 60;
            long minutesLeft = (timeleft % 3600) / 60;
            time = timeleft / 3600 + " hours " +  ((minutesLeft == 0) ? "" : (minutesLeft + " minutes "))
                    +  ((secondsLeft == 0) ? "" : (secondsLeft + " seconds " )) + "left";
        }

//        Toast.makeText(ScreenLockActivity.this,
//                time,
//                Toast.LENGTH_SHORT).show();

        View parentLayout = findViewById(R.id.parent_layout);
        final Snackbar snackbar = Snackbar.make(parentLayout, time, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}