package com.example.adam.myusefulllocations.FavoritesFeature.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class PlaceContract {

    public static final String AUTHORITY = "com.example.adam.myusefulllocations";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_PLACE = "places";


        public static final class PlaceEntry implements BaseColumns{

            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

                public static final String TABLE_NAME = "places";
                public static final String COLUMNS_PLACE_ID = "placeID";

        }


}
