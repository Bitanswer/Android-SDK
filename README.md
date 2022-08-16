# Bitanswer Android SDK

您可以很容易的将 SDK 集成到您的安卓应用中，以使用比特安索身份云提供的身份认证服务。这个 SDK 提供了**登录**，**登出**和**获取用户基本信息**功能。

## 环境版本要求

Android API version 21 or later and Java 8+.

```groovy
android {
    defaultConfig {
        minSdk 21
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## 安装

### 权限

您需要在App的`AndroidManifest.xml`文件中添加权限:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 开始使用

### 一、注册应用

首先，您需要在 [比特安索](https://bit.bitanswer.cn/) 控制台注册一个移动应用，记住应用的ID和密钥，之后在您的应用中添加认证对象:

```java
String appId = getString(R.string.com_bitanswer_app_Id);
String appSecret = getString(R.string.com_bitanswer_app_secret);
String host = getString(R.string.com_bitanswer_host);
BitAnswer bitAnswer = new BitAnswer(appId, appSecret, host);
```

```xml
<resources>
  <string name="com_bitanswer_host">YOUR_HOST</string>
  <string name="com_bitanswer_app_Id">YOUR_APP_ID</string>
  <string name="com_bitanswer_app_secret">YOUR_APP_SECRET</string>
</resources>
```

*使用时您需要将资源替换为真实信息*

### 二、配置应用回调地址

在控制台应用属性中，添加回调地址:

```
{YOUR_SCHEME}://{YOUR_HOST}/android/{YOUR_APP_PACKAGE_NAME}/callback
such as:
myapp://demo.bitanswer.cn/android/com.bitanswer.androiddemo/callback
```

这里需要您自定义一个scheme。配置回调地址之后，您需要在App的`build.gradle`中配置:

```groovy
manifestPlaceholders = [bitHost: "YOUR_HOST", bitScheme: "YOUR_SCHEME"]
```

`bitHost`您可以直接使用资源中的host:`@string/com_bitanswer_host`。到此为止，准备事项就全部完成了，下面将介绍如何使用。

### 三、使用SDK完成登录

这是一个示例:

```java
WebAuthProvider.login(bitAnswer)
              .setScheme("myapp")
              .start(this, new CallBack<Credentials>() {
                 @Override
                 public void onSuccess(Credentials credentials) {
                    Message message = Message.obtain();
                    message.obj = credentials;
                    accessHandler.sendMessage(message);
                 }

                 @Override
                 public void onFailure(BitAnswerException e) {
                    Message message = Message.obtain();
                    message.obj = e.toString();
                    errorHandler.sendMessage(message);
                 }
              });
```

在这个接口中，要求必须调用`setScheme()`方法，除此之外，也可以设置`scope`，`redirect_uri`等参数，最后使用`start()`方法打开浏览器完成登录认证，这里的`this`是一个`Context`。*登录时为了安全起见默认会携带PKCE协议参数*。

完成身份认证之后会回调`onSuccess()`方法，这个过程是**异步**的，所以用例中使用`Handler`来处理回调，您也可以使用其他方式。返回的`Credentials`中包含了`access_token`和`id_token`等参数，您需要把他保存下来，用于接下来获取用户信息。

### 四、使用access_token获取用户信息

这是一个示例:

```java
WebAuthProvider.userInfo(bitAnswer)
              .setAccessToken(accessToken)
              .start(new CallBack<UserProfile>() {
                 @Override
                 public void onSuccess(UserProfile userProfile) {
                    Message message = Message.obtain();
                    message.obj = userProfile.toString();
                    userInfoHandler.sendMessage(message);
                 }

                 @Override
                 public void onFailure(BitAnswerException e) {
                    Message message = Message.obtain();
                    message.obj = e.toString();
                    errorHandler.sendMessage(message);
                 }
              });
```

这个接口的用法与登录接口相似，同样也是**异步**的，除了`access_token`之外不需要其他参数。这个过程不会打开浏览器或是其他页面。

### 五、登出

这是一个示例：

```java
WebAuthProvider.logout(bitAnswer)
              .setScheme("myapp")
              .setIdTokenHint(idToken)
              .start(this, new CallBack<Void>() {
                 @Override
                 public void onSuccess(Void unused) {
                    logoutHandler.sendMessage(Message.obtain());
                 }

                 @Override
                 public void onFailure(BitAnswerException e) {
                    Message message = Message.obtain();
                    message.obj = e.toString();
                    errorHandler.sendMessage(message);
                 }
              });
```

由于之前的登录是在浏览器中完成的，所以登出时需要在浏览器中登出，这个接口会打开浏览器并迅速关闭。在这个接口中`setScheme()`和`setIdTokenHint`是必须的，在完成登出后，建议您清除之前保存的`Credentials`。

### 六、错误处理

通过之前的示例可以发现，我们所有的错误都以`BitAnswerException`的形式返回，您只需要捕获这个异常，就可以知道认证服务或SDK想告诉您的信息。
