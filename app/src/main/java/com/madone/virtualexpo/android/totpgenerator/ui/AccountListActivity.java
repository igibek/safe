package com.madone.virtualexpo.android.totpgenerator.ui;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madone.virtualexpo.android.totpgenerator.R;
import com.madone.virtualexpo.android.totpgenerator.model.Account;

import java.util.List;

public class AccountListActivity extends AppCompatActivity {

    private class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {
        private final List<Account> mAccounts;

        public AccountRecyclerViewAdapter(List<Account> accounts) {
            mAccounts = accounts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.account_list_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Account account = mAccounts.get(position);
            holder.bindAccounts(account);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mAccounts.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final ImageView mImageLogo;
            public final TextView mTitleText;

            public Account mAccount;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mImageLogo = (ImageView) itemView.findViewById(R.id.logo);
                mTitleText = (TextView) itemView.findViewById(R.id.name);
            }

            public void bindAccounts(Account account) {
                mAccount = account;
                mImageLogo.setImageResource(R.mipmap.ic_image_logo);
            }
        }
    }
}
