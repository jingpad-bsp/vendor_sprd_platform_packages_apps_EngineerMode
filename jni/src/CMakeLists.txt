cmake_minimum_required(VERSION 3.4.1)
# set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Werror -Wunused -Wunreachable-code")

add_library(jni_engineermode
            SHARED
    jniutils.cpp)

# Include libraries needed for hello-jni lib
target_link_libraries(jni_engineermode
                    android
                    log)

