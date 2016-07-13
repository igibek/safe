package com.madone.virtualexpo.android.totpgenerator.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.madone.virtualexpo.android.totpgenerator.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class PinActivity extends AppCompatActivity {
    private EditText mPinEditText;
    private EditText mConfirmPinEditText;
    private TextView mErrorText;
    public static String mPinCode;
    private static String mPinString;
    private Button mContinueButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
/*
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .clear()
                .apply();
*/
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        setTitle("Choose you PIN");
        final Random r = new SecureRandom();
        final byte[] salt = new byte[32];
        r.nextBytes(salt);

        mErrorText = (TextView) findViewById(R.id.pin_does_not_match);
        mPinEditText = (EditText) findViewById(R.id.editText_pin);

        mPinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPinString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmPinEditText = (EditText) findViewById(R.id.confirm_pin);
        mConfirmPinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPinCode = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mContinueButton = (Button) findViewById(R.id.button_continue_pin);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(mPinString.equals(mPinCode))) {
                    mPinEditText.setText("");
                    mConfirmPinEditText.setText("");
                    mErrorText.setVisibility(View.VISIBLE);
                } else {
                    try {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .edit()
                                .putString("SALT_CODE", salt.toString())
                                .apply();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .edit()
                                .putString("PIN_CODE", SHA1(mPinCode + salt.toString()))
                                .apply();

                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    /*
                    Log.i("PinActivity", PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext()).
                            getString("SALT_CODE", "Salt Error"));
                    Log.i("PinActivity", PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext()).
                            getString("PIN_CODE", "PinCode Error"));
                            */
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mCancelButton = (Button) findViewById(R.id.button_cancel_pin);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPinEditText.getWindowToken(), 0);
            }
        });


    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        sha1hash = Arrays.copyOf(sha1hash, 16);
        return convertToHex(sha1hash);
    }
}
