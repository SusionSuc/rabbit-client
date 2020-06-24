object BuildInfo {
    const val versionCode = 1000000
    const val versionName = "1.0.0"
    const val applicationId = "com.susion.rabbit.demo"
    const val minSdkVersion = 19
    const val compileSdkVersion = 28
    const val targetSdkVersion = 28
    const val buildToolsVersion = "29.0.3"
}

object LibDepen {
    const val kotlinStdlibJdk7Version = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.21"
    const val rxbinding3 = "com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha2"
    const val gson = "com.google.code.gson:gson:2.8.2"
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.0"
    const val rxandroid2 = "io.reactivex.rxjava2:rxandroid:2.1.0"
    const val retrofit2 = "com.squareup.retrofit2:retrofit:2.4.0"
    const val retrofit2RxJavaAdapter = "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0"
    const val retrofit2ConvertJson = "com.squareup.retrofit2:converter-gson:2.4.0"
    const val glide = "com.github.bumptech.glide:glide:4.10.0"
    const val greenDao = "org.greenrobot:greendao:3.2.2"
    const val autoService = "com.google.auto.service:auto-service:1.0-rc4"
    const val mpAndroidChart = "com.github.PhilJay:MPAndroidChart:v3.1.0"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.2"
    const val lifeClean = "com.susion:life-clean:1.0.6"
    const val asm = "org.ow2.asm:asm:7.1"
    const val asmAnalysis = "org.ow2.asm:asm-analysis:7.1"
    const val asmCommons = "org.ow2.asm:asm-commons:7.1"
    const val asmTree = "org.ow2.asm:asm-tree::7.1"
    const val asmUtils = "org.ow2.asm:asm-util:7.1"
    const val buildGradle = "com.android.tools.build:gradle:3.5.0"
}

object AndroidXDepen {
    private const val androidXVersion = "1.1.0"
    private const val archLifeVersion = "2.0.0"
    const val core = "androidx.core:core:${androidXVersion}"
    const val coreUtils = "androidx.legacy:legacy-support-core-utils:${androidXVersion}"
    const val coreUi = "androidx.legacy:legacy-support-core-ui:${androidXVersion}"
    const val media = "androidx.media:media:${androidXVersion}"
    const val fragment = "androidx.fragment:fragment:${androidXVersion}"
    const val annotations = "androidx.annotation:annotation:${androidXVersion}"
    const val appcompat = "androidx.appcompat:appcompat:${androidXVersion}"
    const val cardView = "androidx.cardview:cardview:${androidXVersion}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${androidXVersion}"
    const val multidex = "androidx.multidex:multidex:2.0.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
    const val lifeRuntime = "androidx.lifecycle:lifecycle-runtime:${archLifeVersion}"
    const val lifeExtensions = "androidx.lifecycle:lifecycle-extensions:${archLifeVersion}"
    const val lifeViewModel = "androidx.lifecycle:lifecycle-viewmodel:${archLifeVersion}"
    const val lifeLiveData = "androidx.lifecycle:lifecycle-livedata:${archLifeVersion}"
}