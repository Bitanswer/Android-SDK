package com.bitanswer.auth.android;

public class BitAnswerException extends RuntimeException {

   private String code;
   private String description;

   public BitAnswerException(String message) {
      super(message);
   }

   public BitAnswerException(String code, String description) {
      this.code = code;
      this.description = description;
   }

   public BitAnswerException(String message, Throwable cause) {
      super(message, cause);
   }

   public BitAnswerException(Throwable cause) {
      super(cause);
   }

   public String getCode() {
      return code;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public String toString() {
      return "BitAnswerException{" +
              "error_code='" + code + '\'' +
              ", error_description='" + description + '\'' +
              '}';
   }
}
