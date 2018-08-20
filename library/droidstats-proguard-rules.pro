# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sharedHome/jj/android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Signature, SourceFile,LineNumberTable

## Retrofit
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-keep class android.support.design.** { *; }
-keep class org.w3c.dom.bootstrap.** { *; }
-keep class org.conscrypt.** { *; }
-keep class okio.** { *; }
-keep class android.databinding.** { *; }
-keep class java.lang.invoke.LambdaMetafactory { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class com.droidstats.sdk.data.models.** {
    public void set*(***);
    public *** get*();
}

-dontwarn com.fasterxml.jackson.databind.**
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn java.lang.invoke.**
-dontwarn com.droidstats.sdk.ui.**
-dontnote