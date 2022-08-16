package com.bitanswer.auth.android.provider;

import com.bitanswer.auth.android.BitAnswerException;

public interface CallBack<T> {

   void onSuccess(T t);

   void onFailure(BitAnswerException e);
}
