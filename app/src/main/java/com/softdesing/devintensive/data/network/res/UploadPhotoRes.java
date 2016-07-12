package com.softdesing.devintensive.data.network.res;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


    public class UploadPhotoRes {

        public class Data {

            @SerializedName("photo")
            @Expose
            public String photo;
            @SerializedName("updated")
            @Expose
            public String updated;

        }

        @SerializedName("success")
        @Expose
        public boolean success;
        @SerializedName("data")
        @Expose
        public Data data;

    }