package com.bitanswer.auth.android.provider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;

public class RedirectActivity extends Activity {
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Intent activity = new Intent(this, AuthenticationActivity.class);
      activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      Intent intent = getIntent();
      if (intent != null) {
         Uri uri = intent.getData();
         activity.setData(uri);
         Log.d(RedirectActivity.class.getSimpleName(), uri.toString());
      }
      startActivity(activity);
      finish();
   }
}
