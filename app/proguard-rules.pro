# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Main method
-keepclasseswithmembers public class me.zhanghai.android.beeshell.** {
    public static void main(java.lang.String[]);
}

# Android Support Library
-keep class android.support.** { *; }
-keep enum android.support.** { *; }
-keep interface android.support.** { *; }

# AndroidX
-keep class androidx.** { *; }
-keep enum androidx.** { *; }
-keep interface androidx.** { *; }

# BeanShell
-keep class bsh.** { *; }
-keep enum bsh.** { *; }
-keep interface bsh.** { *; }

# JLine
-keepnames class org.jline.** { *; }
-keepnames enum org.jline.** { *; }
-keepnames interface org.jline.** { *; }

# Material Components for Android
-keep class com.google.android.material.** { *; }
-keep enum com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }
