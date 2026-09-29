#pragma once

#ifdef ANDROID_TOOLCHAIN
#include <android/errno.h>
#else
#include <cerrno>
#endif

#if defined(__arm__)
#define KLARHEIT_ARCHITECTURE "ARM"
#elif defined(__aarch64__)
#define KLARHEIT_ARCHITECTURE "ARM64"
#elif defined(__i386__)
#define KLARHEIT_ARCHITECTURE "x86"
#elif defined(__x86_64__) || defined(_M_X64)
#define KLARHEIT_ARCHITECTURE "x86_64"
#else
#error "Unknown architecture"
// Note from the developer:
// There's no architecture dependent code in Klarheit, this is just for debugging purposes.
// Feel free to add your architecture if it's not listed here.
#endif

#define KLARHEIT_NAME "KlarheitDSP"
#define KLARHEIT_AUTHORS "Klarheit"
#define KLARHEIT_DEFAULT_SAMPLING_RATE 44100