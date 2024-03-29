#--------------------------------- 一些proguard配置项 ------------------------------------
#这会产生日志
#Specifies to write out some more information during processing.
#If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
#-verbose

#在进行任何一种处理的日志，都将会输出到这里
#Specifies to write out the internal structure of the class files, after any processing.
#The structure is printed to the standard output or to the given file. For example, you may want to write out the contents of a given jar file,
#without processing it at all.
#-dump "YOUR FILE ABSOLUTEPATH"

#将匹配-keep混淆规则的类或成员日志输出到指定文件
#Specifies to exhaustively list classes and class members matched by the various -keep options.
#The list is printed to the standard output or to the given file. The list can be useful to verify if the intended class members are really found,
# especially if you're using wildcards. For example, you may want to list all the applications or all the applets that you are keeping.
#-printseeds "YOUR FILE ABSOLUTEPATH"

#将被移除的代码列表日志输出到指定文件
#Specifies to list dead code of the input class files. The list is printed to the standard output or to the given file.
#For example, you can list the unused code of an application. Only applicable when shrinking.
#-printusage "YOUR FILE ABSOLUTEPATH"

#将重命名日志输出到文件，这里可以找到旧名称->新名称的映射关系
#Specifies to print the mapping from old names to new names for classes and class members that have been renamed.
#The mapping is printed to the standard output or to the given file. For example, it is required for subsequent incremental obfuscation,
# or if you ever want to make sense again of obfuscated stack traces. Only applicable when obfuscating.
#-printmapping "YOUR FILE ABSOLUTEPATH"

#直接使用一个已经存在的mapping文件来作为混淆规则
#Specifies to reuse the given name mapping that was printed out in a previous obfuscation run of ProGuard.
#Classes and class members that are listed in the mapping file receive the names specified along with them.
#Classes and class members that are not mentioned receive new names.
#The mapping may refer to input classes as well as library classes.
#This option can be useful for incremental obfuscation, i.e. processing add-ons or small patches to an existing piece of code.
#In such cases, you should consider whether you also need the option -useuniqueclassmembernames. Only a single mapping file is allowed.
#Only applicable when obfuscating.
#-applymapping "YOUR FILE ABSOLUTEPATH"
#----------------------------------------------------------------------------

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

-assumenosideeffects class com.qxtx.idea.http.tools.HttpLog {
    public static void v(...);
    public static void d(...);
}

-keep class com.qxtx.idea.http** {*;}
-keep class okhttp3.** {*;}
-keep class okio.** {*;}