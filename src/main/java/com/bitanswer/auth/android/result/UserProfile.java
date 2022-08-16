package com.bitanswer.auth.android.result;

import com.google.gson.annotations.SerializedName;

/**
 * The field name is described in the OpenID connect protocol.
 * <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims"/>
 */
public class UserProfile {
   private static final String GIVEN_NAME            = "given_name";
   private static final String FAMILY_NAME           = "family_name";
   private static final String MIDDLE_NAME           = "middle_name";
   private static final String PREFERRED_USERNAME    = "preferred_username";
   private static final String EMAIL_VERIFIED        = "email_verified";
   private static final String PHONE_NUMBER          = "phone_number";
   private static final String PHONE_NUMBER_VERIFIED = "phone_number_verified";
   private static final String UPDATED_AT            = "updated_at";

   private String  sub;
   private String  name;
   @SerializedName(value = GIVEN_NAME)
   private String  givenName;
   @SerializedName(value = FAMILY_NAME)
   private String  familyName;
   @SerializedName(value = MIDDLE_NAME)
   private String  middleName;
   private String  nickname;
   @SerializedName(value = PREFERRED_USERNAME)
   private String  preferredUsername;
   private String  profile;
   private String  picture;
   private String  website;
   private String  email;
   @SerializedName(value = EMAIL_VERIFIED)
   private Boolean emailVerified;
   private String  gender;
   private String  birthdate;
   private String  zoneinfo;
   private String  locale;
   @SerializedName(value = PHONE_NUMBER)
   private String  phoneNumber;
   @SerializedName(value = PHONE_NUMBER_VERIFIED)
   private Boolean phoneNumberVerified;
   @SerializedName(value = UPDATED_AT)
   private Long    updateAt;

   public UserProfile() {
   }

   public String getSub() {
      return sub;
   }

   public void setSub(String sub) {
      this.sub = sub;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getGivenName() {
      return givenName;
   }

   public void setGivenName(String givenName) {
      this.givenName = givenName;
   }

   public String getFamilyName() {
      return familyName;
   }

   public void setFamilyName(String familyName) {
      this.familyName = familyName;
   }

   public String getMiddleName() {
      return middleName;
   }

   public void setMiddleName(String middleName) {
      this.middleName = middleName;
   }

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   public String getPreferredUsername() {
      return preferredUsername;
   }

   public void setPreferredUsername(String preferredUsername) {
      this.preferredUsername = preferredUsername;
   }

   public String getProfile() {
      return profile;
   }

   public void setProfile(String profile) {
      this.profile = profile;
   }

   public String getPicture() {
      return picture;
   }

   public void setPicture(String picture) {
      this.picture = picture;
   }

   public String getWebsite() {
      return website;
   }

   public void setWebsite(String website) {
      this.website = website;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Boolean getEmailVerified() {
      return emailVerified;
   }

   public void setEmailVerified(Boolean emailVerified) {
      this.emailVerified = emailVerified;
   }

   public String getGender() {
      return gender;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public String getBirthdate() {
      return birthdate;
   }

   public void setBirthdate(String birthdate) {
      this.birthdate = birthdate;
   }

   public String getZoneinfo() {
      return zoneinfo;
   }

   public void setZoneinfo(String zoneinfo) {
      this.zoneinfo = zoneinfo;
   }

   public String getLocale() {
      return locale;
   }

   public void setLocale(String locale) {
      this.locale = locale;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public Boolean getPhoneNumberVerified() {
      return phoneNumberVerified;
   }

   public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
      this.phoneNumberVerified = phoneNumberVerified;
   }

   public Long getUpdateAt() {
      return updateAt;
   }

   public void setUpdateAt(Long updateAt) {
      this.updateAt = updateAt;
   }

   @Override
   public String toString() {
      return "UserProfile{" +
              "id='" + sub + '\'' +
              ", name='" + name + '\'' +
              ", nickname='" + nickname + '\'' +
              ", picture='" + picture + '\'' +
              ", website='" + website + '\'' +
              ", email='" + email + '\'' +
              ", gender='" + gender + '\'' +
              ", phoneNumber='" + phoneNumber + '\'' +
              '}';
   }
}
