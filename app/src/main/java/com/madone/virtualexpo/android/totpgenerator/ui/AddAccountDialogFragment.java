package com.madone.virtualexpo.android.totpgenerator.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.madone.virtualexpo.android.totpgenerator.R;
import com.madone.virtualexpo.android.totpgenerator.utils.AES;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public class AddAccountDialogFragment extends DialogFragment {

    EditText mEditTextAccountName;

    public static AddAccountDialogFragment newInstance() {
        AddAccountDialogFragment fragment = new AddAccountDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_account, null);
        mEditTextAccountName = (EditText) rootView.findViewById(R.id.edit_text_list_name);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEditTextAccountName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addAccount();
                }
                return true;
            }
        });

        builder.setTitle("Add new Account");
        builder.setMessage("Please enter the key for this account.");

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addAccount();
                    }
                });

        return builder.create();
    }

    private void addAccount() {

        String mToken = mEditTextAccountName.getText().toString();

        Log.e("AddAccount", "onClick:" + mToken);

        try {
            String iv;

            String secretKey = PinActivity.SHA1(PinActivity.mPinCode);
            secretKey = secretKey.substring(0, 16);

            iv = PinActivity.SHA1(AES.generateIv());
            iv = iv.substring(0, 16);

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putString("E_TOKEN", AES.encrypt(secretKey, iv, mToken))
                    .apply();

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putString("IV", iv)
                    .apply();

            PreferenceManager.getDefaultSharedPreferences(getActivity())
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

        Intent i = new Intent(getActivity(), MainActivity.class);
        i.putExtra("FROM_ACTIVITY", "Camera");
        startActivity(i);


    }
}