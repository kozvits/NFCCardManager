# Add project specific ProGuard rules here.
-keep class com.nfccardmanager.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}