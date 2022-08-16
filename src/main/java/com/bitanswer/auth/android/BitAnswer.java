package com.bitanswer.auth.android;

import java.util.Locale;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.bitanswer.auth.android.provider.AuthUtils;

public class BitAnswer {
   private final String appId;
   private final String appSecret;

   private String host;

   /**
    * @param appId     The ID of app.
    * @param appSecret The secret of app.
    * @param host      The host should not start with 'http://', please use 'https'.
    *                  And it will append 'https://' if you not set.
    */
   public BitAnswer(@NonNull String appId, @NonNull String appSecret, @NonNull String host) {
      this.appId = appId;
      this.appSecret = appSecret;
      this.host = host;
      convertHost();
   }

   private void convertHost() {
      if (AuthUtils.isEmpty(this.host)) {
         throw new BitAnswerException("host should not empty");
      }
      String normalizedUri = this.host.toLowerCase(Locale.ROOT);
      if (normalizedUri.startsWith("http://")) {
         throw new BitAnswerException("host need https, but: " + normalizedUri);
      }
      if (!normalizedUri.startsWith("http")) {
         this.host = "https://" + normalizedUri;
      }
   }

   public String getAppId() {
      return appId;
   }

   public String getAppSecret() {
      return appSecret;
   }

   /**
    * @return The host converted
    */
   public String getHost() {
      return this.host;
   }

   /**
    * @return The authorize-endpoint uri, which will be look like "https://{converted host}/oidc/auth"
    */
   public String getAuthUri() {
      return Uri.parse(this.host).buildUpon()
              .appendEncodedPath("oidc")
              .appendEncodedPath("auth")
              .build()
              .toString();
   }
}
