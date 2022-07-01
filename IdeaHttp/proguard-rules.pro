-assumenosideeffects class android.util.Log {
    public static int v(...);
#    public static int i(...);
#    public static int w(...);
    public static int d(...);
#    public static int e(...);
}

-assumenosideeffects class com.qxtx.idea.http.tools.HttpLog {
    public static void v(...);
    public static void d(...);
}

-keep class com.qxtx.idea.http** {*;}
-keep class okhttp3.** {*;}
-keep class okio.** {*;}