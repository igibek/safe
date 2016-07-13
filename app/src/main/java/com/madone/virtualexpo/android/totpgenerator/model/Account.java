package com.madone.virtualexpo.android.totpgenerator.model;

import android.graphics.drawable.Drawable;

import java.util.UUID;

public class Account {
    private String mTitle;
    private Drawable mLogo;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Drawable getLogo() {
        return mLogo;
    }

    public void setLogo(Drawable logo) {
        mLogo = logo;
    }
}
