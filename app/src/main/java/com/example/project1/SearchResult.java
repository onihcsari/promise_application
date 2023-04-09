package com.example.project1;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {
    @SerializedName("documents")
    public List<Document> documents;

    public static class Document {
        @SerializedName("place_name")
        public String place_name;

        @SerializedName("x")
        public double longitude;

        @SerializedName("y")
        public double latitude;
    }
}