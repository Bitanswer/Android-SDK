package com.bitanswer.auth.android.provider;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bitanswer.auth.android.BitAnswer;
import com.bitanswer.auth.android.BitAnswerException;
import com.bitanswer.auth.android.result.Credentials;
import com.bitanswer.auth.android.result.UserProfile;

public class WebAuthProvider {
   static final String CLIENT_ID           = "client_id";
   static final String CLIENT_SECRET       = "client_secret";
   static final String REDIRECT_URI        = "redirect_uri";
   static final String DISPLAY             = "display";
   static final String SCOPE               = "scope";
   static final String STATE               = "state";
   static final String GRANT_TYPE          = "grant_type";
   static final String RESPONSE_TYPE       = "response_type";
   static final String NONCE               = "nonce";
   static final String RESPONSE_MODE       = "response_mode";
   static final String PROMPT              = "prompt";
   static final String UI_LOCALES          = "ui_locales";
   static final String ID_TOKEN_HINT       = "id_token_hint";
   static final String LOGOUT_HINT         = "logout_hint";
   static final String LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";

   static final String CODE_VERIFIER         = "code_verifier";
   static final String CODE_CHALLENGE        = "code_challenge";
   static final String CODE_CHALLENGE_METHOD = "code_challenge_method";
   static final String PKCE_METHOD_PLAIN     = "plain";
   static final String PKCE_METHOD_S256      = "S256";

   static final String CODE = "code";

   static final String ERROR             = "error";
   static final String ERROR_DESCRIPTION = "error_description";

   static final String ERROR_VALUE_UNKNOWN_EXCEPTION = "unknown_exception";
   static final String ERROR_VALUE_ACCESS_DENIED     = "access_denied";
   static final String ERROR_VALUE_UNAUTHORIZED      = "unauthorized";
   static final String ERROR_VALUE_AUTH_CANCELED     = "authentication_canceled";

   private static final ThreadLocal<AuthBuilder> localAuthBuilder = new ThreadLocal<>();

   private static final Gson         gson       = new Gson().newBuilder().create();
   private static final OkHttpClient httpClient = new OkHttpClient.Builder().callTimeout(3, TimeUnit.MINUTES).build();
   private static final Executor     executor   = Executors.newFixedThreadPool(5);


   /**
    * You can use this method to login, when you use this,
    * you should use 'setScheme(scheme)' after this method, otherwise it may cause error.
    * <br>
    * This method will open a browser to login, and remember the session on the browser.
    * <br>
    * You would better remember the callback credentials.
    *
    * @param account The BitAnswer object.
    */
   public static AuthBuilder login(BitAnswer account) {
      return new AuthBuilder(account);
   }

   /**
    * You can use this method to logout, when you use this,
    * you should use 'setScheme(scheme)' and 'setIdTokenHint(id_token)' after this method.
    * <br>
    * This method will open the browser to clear session, and closed quickly.
    *
    * @param account The BitAnswer object.
    */
   public static LogoutBuilder logout(BitAnswer account) {
      return new LogoutBuilder(account);
   }

   /**
    * You should use 'setScheme(scheme)' and 'setAccessToken(access_token)' after this method.
    * <br>
    * This flow will return {@link UserProfile}.
    *
    * @param account The BitAnswer object.
    */
   public static UserInfoBuilder userInfo(BitAnswer account) {
      return new UserInfoBuilder(account);
   }

   protected static void resume(Intent result) {
      if (localAuthBuilder.get() == null) {
         return;
      }
      CallBack<Credentials> callBack = Objects.requireNonNull(localAuthBuilder.get()).callback;
      if (result == null || result.getData() == null) {
         callBack.onFailure(new BitAnswerException(ERROR_VALUE_AUTH_CANCELED,
                 "The user closed the browser app and the authentication was canceled."));
         return;
      }
      AuthBuilder authBuilder = localAuthBuilder.get();
      if (authBuilder == null) {
         callBack.onFailure(new BitAnswerException(ERROR_VALUE_UNKNOWN_EXCEPTION, "no auth."));
         return;
      }
      Map<String, String> values = AuthUtils.getValuesFromUri(result.getData());
      if (values.get(ERROR) != null) {
         callBack.onFailure(new BitAnswerException(values.get(ERROR), values.get(ERROR_DESCRIPTION)));
         return;
      }
      if (!Objects.equals(values.get(STATE), authBuilder.state)) {
         callBack.onFailure(new BitAnswerException(ERROR_VALUE_ACCESS_DENIED, "The received state is invalid. Try again."));
         return;
      }
      CodeGrantBuilder codeGrantBuilder = new CodeGrantBuilder(authBuilder.account, values.get(CODE));
      codeGrantBuilder.doGrant();
   }

   public static class AuthBuilder {
      private final BitAnswer account;

      private String redirectUri;
      private String state;
      private String scope;
      private String responseType;
      private String nonce;
      private String uiLocales;
      private String prompt;
      private String scheme;
      private String codeChallenge;
      private String codeChallengeMethod;
      private String codeVerifier;

      private CallBack<Credentials> callback = null;

      public AuthBuilder(BitAnswer account) {
         this.account = account;
         this.state = AuthUtils.randomString();
         this.nonce = AuthUtils.randomString();
         this.scope = "openid profile email";
         this.responseType = "code";
         this.uiLocales = "zh_CN en";
         this.prompt = "none";
         this.codeVerifier = AuthUtils.randomString(64);
         this.codeChallengeMethod = PKCE_METHOD_S256;
         this.codeChallenge = AuthUtils.generateS256CodeChallenge(this.codeVerifier);
      }

      public AuthBuilder setRedirectUri(String redirectUri) {
         if (!AuthUtils.isEmpty(redirectUri)) {
            this.redirectUri = redirectUri;
         }
         return this;
      }

      public AuthBuilder setState(String state) {
         if (!AuthUtils.isEmpty(state)) {
            this.state = state;
         }
         return this;
      }

      public AuthBuilder setScope(String scope) {
         if (!AuthUtils.isEmpty(scope)) {
            this.scope = scope;
         }
         return this;
      }

      public AuthBuilder setResponseType(String responseType) {
         if (!AuthUtils.isEmpty(responseType)) {
            this.responseType = responseType;
         }
         return this;
      }

      public AuthBuilder setNonce(String nonce) {
         if (!AuthUtils.isEmpty(nonce)) {
            this.nonce = nonce;
         }
         return this;
      }

      public AuthBuilder setUiLocales(String uiLocales) {
         if (!AuthUtils.isEmpty(uiLocales)) {
            this.uiLocales = uiLocales;
         }
         return this;
      }

      public AuthBuilder setPrompt(String prompt) {
         if (!AuthUtils.isEmpty(prompt)) {
            this.prompt = prompt;
         }
         return this;
      }

      public AuthBuilder setScheme(String scheme) {
         if (AuthUtils.isEmpty(scheme)) {
            throw new BitAnswerException("Scheme must set a valid value.");
         }
         this.scheme = scheme;
         return this;
      }

      public AuthBuilder setCodeChallengeMethod(String codeChallengeMethod) {
         if (!PKCE_METHOD_S256.equals(codeChallengeMethod) && !PKCE_METHOD_PLAIN.equals(codeChallengeMethod)) {
            throw new BitAnswerException("code_challenge_method must be 'S256' or 'plain'.");
         }
         this.codeChallengeMethod = codeChallengeMethod;
         return this;
      }

      /**
       * You should set a valid value, otherwise it will not work.
       * <br>
       * See more: https://www.rfc-editor.org/rfc/rfc7636#section-4
       */
      public AuthBuilder setCodeVerifier(String codeVerifier) {
         if (AuthUtils.isEmpty(codeVerifier)) {
            return this;
         }
         if (codeVerifier.length() < 43 || codeVerifier.length() > 128) {
            throw new BitAnswerException("code_verifier should with a minimum length of 43 characters and a maximum length of 128 characters.");
         }
         this.codeVerifier = codeVerifier;
         if (PKCE_METHOD_S256.equals(this.codeChallengeMethod)) {
            this.codeChallenge = AuthUtils.generateS256CodeChallenge(codeVerifier);
         }
         else if (PKCE_METHOD_PLAIN.equals(this.codeChallengeMethod)) {
            this.codeChallenge = codeVerifier;
         }
         return this;
      }

      public void start(Context context, CallBack<Credentials> callBack) {
         this.callback = callBack;
         if (AuthUtils.isEmpty(this.redirectUri)) {
            redirectUri = AuthUtils.getCallBackUri(scheme, context.getApplicationContext().getPackageName(), account.getHost());
         }

         Uri authUri = Uri.parse(account.getAuthUri()).buildUpon()
                 .appendQueryParameter(CLIENT_ID, this.account.getAppId())
                 .appendQueryParameter(REDIRECT_URI, this.redirectUri)
                 .appendQueryParameter(SCOPE, this.scope)
                 .appendQueryParameter(STATE, this.state)
                 .appendQueryParameter(RESPONSE_TYPE, this.responseType)
                 .appendQueryParameter(NONCE, this.nonce)
                 .appendQueryParameter(PROMPT, this.prompt)
                 .appendQueryParameter(UI_LOCALES, this.uiLocales)
                 .appendQueryParameter(CODE_CHALLENGE, this.codeChallenge)
                 .appendQueryParameter(CODE_CHALLENGE_METHOD, this.codeChallengeMethod)
                 .build();

         localAuthBuilder.set(this);
         AuthenticationActivity.authenticationBrowser(context, authUri);
      }
   }

   static class CodeGrantBuilder {
      @SerializedName(CLIENT_ID)
      private final String clientId;
      @SerializedName(CLIENT_SECRET)
      private final String clientSecret;
      private final String code;
      @SerializedName(GRANT_TYPE)
      private       String grantType;
      @SerializedName(CODE_VERIFIER)
      private       String codeVerifier;

      private final transient BitAnswer account;

      CodeGrantBuilder(@NonNull BitAnswer account, @NonNull String code) {
         this.account = account;
         this.clientId = account.getAppId();
         this.clientSecret = account.getAppSecret();
         this.code = code;
         this.grantType = "authorization_code";
      }

      void doGrant() {
         AuthBuilder authBuilder = Objects.requireNonNull(localAuthBuilder.get());
         CallBack<Credentials> callBack = authBuilder.callback;
         this.codeVerifier = authBuilder.codeVerifier;
         executor.execute(() -> {
            String url = Uri.parse(account.getHost()).buildUpon()
                    .appendEncodedPath("oidc")
                    .appendEncodedPath("token")
                    .build().toString();

            String result = post(this, url, callBack);
            if (result == null) {
               return;
            }
            Credentials credentials = gson.fromJson(result, Credentials.class);
            callBack.onSuccess(credentials);
         });
      }
   }

   public static class UserInfoBuilder {
      private transient final BitAnswer account;

      @SerializedName(Credentials.ACCESS_TOKEN)
      private String accessToken;

      public UserInfoBuilder(@NonNull BitAnswer account) {
         this.account = account;
      }

      public UserInfoBuilder setAccessToken(String accessToken) {
         this.accessToken = accessToken;
         return this;
      }

      public void start(CallBack<UserProfile> callBack) {
         executor.execute(() -> {
            String url = Uri.parse(account.getHost()).buildUpon()
                    .appendEncodedPath("oidc")
                    .appendEncodedPath("userinfo")
                    .build().toString();
            String result = post(this, url, callBack);
            if (result == null) {
               return;
            }
            UserProfile userProfile = gson.fromJson(result, UserProfile.class);
            callBack.onSuccess(userProfile);
         });
      }
   }

   public static class LogoutBuilder {
      private String idTokenHint;
      private String logoutHint;
      private String clientId;
      private String postLogoutRedirectUri;
      private String state;
      private String uiLocales;

      private BitAnswer account;
      private String    scheme;

      public LogoutBuilder(@NonNull BitAnswer account) {
         this.account = account;
         this.clientId = account.getAppId();
         this.state = AuthUtils.randomString();
         this.uiLocales = "zh_CN en";
      }

      public LogoutBuilder setScheme(String scheme) {
         this.scheme = scheme;
         return this;
      }

      public LogoutBuilder setIdTokenHint(String idTokenHint) {
         this.idTokenHint = idTokenHint;
         return this;
      }

      public LogoutBuilder setLogoutHint(String logoutHint) {
         this.logoutHint = logoutHint;
         return this;
      }

      public LogoutBuilder setPostLogoutRedirectUri(String postLogoutRedirectUri) {
         this.postLogoutRedirectUri = postLogoutRedirectUri;
         return this;
      }

      public LogoutBuilder setState(String state) {
         this.state = state;
         return this;
      }

      public LogoutBuilder setUiLocales(String uiLocales) {
         this.uiLocales = uiLocales;
         return this;
      }

      public void start(Context context, CallBack<Void> callBack) {
         if (AuthUtils.isEmpty(postLogoutRedirectUri)) {
            postLogoutRedirectUri = AuthUtils.getCallBackUri(scheme, context.getApplicationContext().getPackageName(), account.getHost());
         }
         Uri uri = Uri.parse(account.getHost()).buildUpon()
                 .appendEncodedPath("oidc")
                 .appendEncodedPath("end_session_endpoint")
                 .appendQueryParameter(ID_TOKEN_HINT, idTokenHint)
                 .appendQueryParameter(CLIENT_ID, clientId)
                 .appendQueryParameter(LOGOUT_REDIRECT_URI, postLogoutRedirectUri)
                 .appendQueryParameter(STATE, state)
                 .appendQueryParameter(UI_LOCALES, uiLocales)
                 .build();
         localAuthBuilder.remove();
         AuthenticationActivity.logoutBrowser(context, uri);
         callBack.onSuccess(null);
      }
   }

   private static String post(Object toJsonObj, String url, CallBack<?> callBack) {
      String requestJson = gson.toJson(toJsonObj);
      RequestBody requestBody = RequestBody.create(requestJson, MediaType.get("application/json"));
      Request request = new Request.Builder().url(url)
              .post(requestBody)
              .build();
      try {
         Response response = httpClient.newCall(request).execute();
         String res = response.body().string();

         Map<String, Object> map = gson.fromJson(res, Map.class);
         if (map.containsKey(ERROR) && map.containsKey(ERROR_DESCRIPTION)) {
            callBack.onFailure(new BitAnswerException(map.get(ERROR).toString(),
                    map.get(ERROR_DESCRIPTION).toString()));
            return null;
         }
         return res;
      } catch (IOException e) {
         callBack.onFailure(new BitAnswerException("http request failed.", e));
      }
      return null;
   }
}
