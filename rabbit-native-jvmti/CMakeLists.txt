# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -frtti -Wall -Wno-unused-parameter -Wno-unused-variable -Wno-shift-count-overflow -Wno-error=non-virtual-dtor -Wno-sign-compare -Wno-switch -Wno-missing-braces")

project (rabbit-jvmti)

message("src dir -> ${PROJECT_SOURCE_DIR}")

# 设置库文件的输出路径（LIBRARY_OUTPUT_PATH是全局变量）
set(LIBRARY_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/libs)

# 查找当前目录所有源文件，并将名称保存到 DIR_SRCS 变量
# 不能查找子目录
#aux_source_directory(${PROJECT_SOURCE_DIR} DIR_SRCS)

#file(GLOB DIR_SRCS *.c *.cpp)

add_library( # Sets the name of the library.
        rabbit-jvmti

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/jvmti_handler.cpp
        src/main/cpp/jvmti_thread.cpp
        src/main/cpp/jvmti_init.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        rabbit-jvmti

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})