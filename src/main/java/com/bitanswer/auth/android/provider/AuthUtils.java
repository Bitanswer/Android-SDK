package com.bitanswer.auth.android.provider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bitanswer.auth.android.BitAnswerException;

public class AuthUtils {
   private static final String TAG = AuthUtils.class.getSimpleName();

   private final ThreadLocal<Random> random = new ThreadLocal<Random>() {
      @Override
      protected Random initialValue() {
         return new SecureRandom();
      }
   };

   static String getCallBackUri(@NonNull String scheme, @NonNull String packageName, @NonNull String host) {
      if (!URLUtil.isValidUrl(host)) {
         Log.e(TAG, "The Host is invalid and the Callback URI will not be set. You used: " + host);
         return null;
      }

      Uri uri = Uri.parse(host)
              .buildUpon()
              .scheme(scheme)
              .appendPath("android")
              .appendPath(packageName)
              .appendPath("callback")
              .build();

      Log.v(TAG, "The Callback URI is: " + uri);
      return uri.toString();
   }

   @NonNull
   static Map<String, String> getValuesFromUri(@Nullable Uri uri) {
      if (uri == null) {
         return Collections.emptyMap();
      }
      return asMap(uri.getQuery() != null ? uri.getQuery() : uri.getFragment());
   }

   private static Map<String, String> asMap(@Nullable String valueString) {
      if (valueString == null) {
         return new HashMap<>();
      }
      final String[] entries = valueString.length() > 0 ? valueString.split("&") : new String[]{};
      Map<String, String> values = new HashMap<>(entries.length);
      for (String entry : entries) {
         final String[] value = entry.split("=");
         if (value.length == 2) {
            values.put(value[0], value[1]);
         }
      }
      return values;
   }

   public static boolean isEmpty(String s) {
      return s == null || s.isEmpty() || s.trim().isEmpty();
   }

   public static String randomString() {
      return randomString(32);
   }

   public static String randomString(int length) {
      SecureRandom secureRandom = new SecureRandom();
      byte[] bytes = new byte[length];
      secureRandom.nextBytes(bytes);
      return Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
   }

   public static String generateS256CodeChallenge(String codeVerifier) throws BitAnswerException {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         md.update(codeVerifier.getBytes(StandardCharsets.ISO_8859_1));
         byte[] digestBytes = md.digest();
         return Base64.encodeToString(digestBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
      } catch (NoSuchAlgorithmException e) {
         throw new BitAnswerException("no such algorithm");
      }
   }
}
