package com.example.csongor.newsapp.guardian_api;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.csongor.newsapp.guardian_api.ApiKey.API_KEY;

/**
 * If you have another API key for Guardian REST api modify it HERE!!
 */
@StringDef(API_KEY)
@Retention(RetentionPolicy.SOURCE)
public @interface ApiKey {
    String API_KEY = "&api-key=7784ddf2-5e12-4126-b8d1-878533f4dc01";
}
