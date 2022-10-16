-keepattributes *Annotation*

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

#代码中@Keep注解生效的原因，可以配置自定义注解
# Understand the @Keep support annotation.
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

#----------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
#代码混淆压缩比，0-7之间，默认为5，一般不需要修改
-optimizationpasses 5
#混小时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
#指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
#不做预校验，preverify是混淆的4个步骤之一，android不需要这步骤，去掉可以加快混淆速度
-dontpreverify
#指定混淆时采用的算法，后面的参数是一个过滤器，这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#保护代码中的Annotation不被混淆，#这在json实体映射是非常重要，比如fastjson
-keepattributes *Annotation*,InnerClasses
#避免混淆泛型，这在json实体映射是非常重要，比如fastjson
-keepattributes Signature
#抛出异常是保留代码行号
-keepattributes SourceFile,LineNumberTable
#如果用到了反射需要加入
-keepattributes EnclosingMethod
#--------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------

#AndroidX的混淆规则
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}

-keep public interface ** { *; }
-keep public abstract class ** { *; }
-keep public enum ** { *; }

#保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保留Serializable序列化的类不能被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#对于R（资源）下所有类机器方法，都不能被混淆
-keep class **.R$* {
 *;
}
#对于带有回调函数onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}
#对于带有回调函数onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Callback);
}
#----------------------------------------------------------------------------

-keep class **JNI* {*;}
-keepclasseswithmembers class * {
    *JNI*(...);
}

-keepclasseswithmembernames class * {
	*JRI*(...);
}

-keep class **$Properties

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

-keep public class java.nio* { *; }
-keep public class javax.**

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep class com.qxtx.idea.http.converter.fastjson.** { *; }