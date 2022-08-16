package com.bitanswer.auth.android.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AuthenticationActivity extends Activity {

   private boolean intentLaunched = false;

   static final String EXTRA_INTENT_LAUNCHED = "com.bitanswer.android.EXTRA_INTENT_LAUNCHED";
   static final String EXTRA_AUTHORIZE_URI   = "com.bitanswer.android.EXTRA_AUTHORIZE_URI";

   static void authenticationBrowser(Context context, Uri authUri) {
      Intent intent = new Intent(context, AuthenticationActivity.class);
      intent.putExtra(EXTRA_AUTHORIZE_URI, authUri);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(intent);
   }

   static void logoutBrowser(Context context, Uri logoutUri) {
      Intent intent = new Intent(context, AuthenticationActivity.class);
      intent.putExtra(EXTRA_AUTHORIZE_URI, logoutUri);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(intent);
   }

   @Override
   protected void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
      setIntent(intent);
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putBoolean(EXTRA_INTENT_LAUNCHED, intentLaunched);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      Intent resultData = resultCode == RESULT_CANCELED ? new Intent() : data;
      deliverAuthenticationResult(resultData);
      finish();
   }

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (savedInstanceState != null) {
         intentLaunched = savedInstanceState.getBoolean(EXTRA_INTENT_LAUNCHED, false);
      }
   }

   @Override
   protected void onResume() {
      super.onResume();
      Intent authIntent = getIntent();
      if (!intentLaunched) {
         if (authIntent.getExtras() == null) {
            finish();
            return;
         }
         intentLaunched = true;
         Uri authUri = authIntent.getExtras().getParcelable(EXTRA_AUTHORIZE_URI);
         Intent intent = new Intent(Intent.ACTION_VIEW);
         intent.setData(authUri);
         this.startActivity(intent);
         return;
      }
      if (authIntent.getData() == null) {
         setResult(RESULT_CANCELED);
      }
      deliverAuthenticationResult(authIntent);
      finish();
   }

   public void deliverAuthenticationResult(Intent result) {
      WebAuthProvider.resume(result);
   }
}
