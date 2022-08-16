package com.bitanswer.auth.android.result;

import com.google.gson.annotations.SerializedName;

public class Credentials {

   public static final String ACCESS_TOKEN       = "access_token";
   public static final String TOKEN_TYPE         = "token_type";
   public static final String EXPIRES_IN         = "expires_in";
   public static final String ID_TOKEN           = "id_token";
   public static final String REFRESH_TOKEN      = "refresh_token";
   public static final String REFRESH_EXPIRES_IN = "refresh_expires_in";

   @SerializedName(ACCESS_TOKEN)
   private String accessToken;
   @SerializedName(TOKEN_TYPE)
   private String tokenType;
   @SerializedName(EXPIRES_IN)
   private Long   expiresIn;
   @SerializedName(ID_TOKEN)
   private String idToken;
   @SerializedName(REFRESH_TOKEN)
   private String refreshToken;
   @SerializedName(REFRESH_EXPIRES_IN)
   private Long   refreshExpiresIn;
   private String state;

   public Credentials() {
   }

   public String getAccessToken() {
      return accessToken;
   }

   public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   public String getTokenType() {
      return tokenType;
   }

   public void setTokenType(String tokenType) {
      this.tokenType = tokenType;
   }

   public Long getExpiresIn() {
      return expiresIn;
   }

   public void setExpiresIn(Long expiresIn) {
      this.expiresIn = expiresIn;
   }

   public String getIdToken() {
      return idToken;
   }

   public void setIdToken(String idToken) {
      this.idToken = idToken;
   }

   public String getRefreshToken() {
      return refreshToken;
   }

   public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
   }

   public Long getRefreshExpiresIn() {
      return refreshExpiresIn;
   }

   public void setRefreshExpiresIn(Long refreshExpiresIn) {
      this.refreshExpiresIn = refreshExpiresIn;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }
}
