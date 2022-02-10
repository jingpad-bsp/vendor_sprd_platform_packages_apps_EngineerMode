/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "engjni"
//#include "utils/Log.h"
#include <android/log.h>
#include <stdint.h>
#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <malloc.h>


#define ROOT_MAGIC 0x524F4F54 //"ROOT"
#define ROOT_OFFSET 512
#define MAX_COMMAND_BYTES               (8 * 1024)

#define TAG  "jni_engineermode"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)


//#include "atci.h"
typedef struct {
    uint32_t magic;
    uint32_t root_flag;
} root_stat_t;

/*
int sendATCmd(int phoneId, const char *atCmd, char *resp, size_t respLen);
static jstring EngineerMode_sendATCmd(JNIEnv *env, jobject thiz, jint phoneId, jstring cmd) {
    char result[MAX_COMMAND_BYTES] = {0};
    const char *atCmd = env->GetStringUTFChars(cmd, 0);
    LOGD("send AT cmd: %s", atCmd);
    int resultValue = sendATCmd(phoneId, atCmd, result, MAX_COMMAND_BYTES);
    env->ReleaseStringUTFChars(cmd, atCmd);
    if (resultValue != 0) {
        return env->NewStringUTF("ERROR");
    }
    LOGI("the return value is: %s", result);
    return env->NewStringUTF(result);
}
*/

static jint check_sdcard_mounted_default(JNIEnv *env, jobject thiz) {
    char str[4096];
    char *token, *last;
    const char *mountPath = "/mnt/media_rw/";
    FILE *fp = fopen("/proc/mounts", "r");
    if (fp != NULL) {
        while (fgets(str, sizeof(str), fp)) {
            token = strtok_r(str, " ", &last);
            if (token != NULL) {
                token = strtok_r(NULL, " ", &last);
                if (strstr(token, mountPath)) {
                    fclose(fp);
                    return 1;
                }
            }
        }
        fclose(fp);
    }
    return -1;
}


static jstring get_sd_file_path(JNIEnv *env, jobject thiz) {
    char str[4096];
    char result[254] = {0};
    char *token, *last;
    const char *mountPath = "/mnt/media_rw/";
    FILE *fp = fopen("/proc/mounts", "r");
    if (fp != NULL) {
        while (fgets(str, sizeof(str), fp)) {
            token = strtok_r(str, " ", &last);
            if (token != NULL) {
                token = strtok_r(NULL, " ", &last);
                if (strstr(token, mountPath)) {
                    snprintf(result, sizeof("/storage/") + strlen(mountPath),
                             "/storage/%s", token + strlen(mountPath));
                    LOGD("the path of sd is: %s", result);
                    break;
                }
            }
        }
        fclose(fp);
    } else {
        return env->NewStringUTF("/storage/sdcard0");
    }
    return env->NewStringUTF(result);
}

/*
static jint get_rootflag(JNIEnv *env, jobject thiz) {

    char block_device[100];
    //strcpy(block_device, "/dev/block/platform/sdio_emmc/by-name/miscdata");
    strcpy(block_device, "/sys/root_recorder/rootrecorder");

    root_stat_t stat;
    FILE *device;
    int retval = 0;

    device = fopen(block_device, "r");
    if (!device) {
        goto out;
    }

    if (fread(&stat, sizeof(root_stat_t), 1, device) <= 0) {
        LOGE("[root_recorder] Couldn't read magic number!\n");
        goto out;
    }

    LOGD("[root_recorder] magic=%d\n", stat.magic);
    LOGD("[root_recorder] rootflag=%d\n", stat.root_flag);

    if (stat.magic == ROOT_MAGIC) {
        LOGE("[root_recorder] sprd magic verify pass.\n");
        retval = stat.root_flag;
    } else {
        LOGE("[root_recorder] sprd magic verify failed.\n");
    }

    out:
    if (device)
        fclose(device);
    return retval;

}
*/


static const char *hardWareClassPathName =
    "com/unisoc/engineermode/core/utils/EngineerModeNative";

static JNINativeMethod getMethods[] = {
//    {"native_sendATCmd",                    "(ILjava/lang/String;)Ljava/lang/String;", (void *) EngineerMode_sendATCmd},
//    {"native_get_rootflag",                 "()I",                                     (void *) get_rootflag},
    {"native_check_sdcard_mounted_default", "()I",                                     (void *) check_sdcard_mounted_default},
    {"native_get_sd_file_path",             "()Ljava/lang/String;",                    (void *) get_sd_file_path},
};

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    //use JNI1.6
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Error: GetEnv failed in JNI_OnLoad");
        return -1;
    }
    if (!registerNativeMethods(env, hardWareClassPathName, getMethods,
                               sizeof(getMethods) / sizeof(getMethods[0]))) {
        LOGE("Error: could not register native methods  ");
        return -1;
    }
    return JNI_VERSION_1_6;
}

