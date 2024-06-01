package com.mirea.kt.ribo.afishamoscow;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class Category {
    @SerializedName("name")
    private String categoryName;
    @SerializedName("slug")
    private String categorySlug;


    public Category(String categoryName, String categorySlug) {
        this.categoryName = categoryName;
        this.categorySlug = categorySlug;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}