#pragma once

#if defined(__ANDROID__)
#include <android/log.h>
#define TAG "Klarheit"
#if defined(LOG_NDEBUG) && LOG_NDEBUG == 0
#define KLARHEIT_LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#else
#define KLARHEIT_LOGD(...) ((void) 0)
#endif
#define KLARHEIT_LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define KLARHEIT_LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#else
#include <cstdio>
#define KLARHEIT_LOGD(fmt, ...) fprintf(stderr, "[Klarheit][DEBUG] " fmt "\n", ##__VA_ARGS__)
#define KLARHEIT_LOGI(fmt, ...) fprintf(stderr, "[Klarheit][INFO] " fmt "\n", ##__VA_ARGS__)
#define KLARHEIT_LOGE(fmt, ...) fprintf(stderr, "[Klarheit][ERROR] " fmt "\n", ##__VA_ARGS__)
#endif
