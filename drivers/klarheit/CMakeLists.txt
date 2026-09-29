cmake_minimum_required(VERSION 3.16.3)
project("Klarheit")

set(VERSION_CODE "00000000" CACHE STRING "Build version code")
set(VERSION_NAME "0.0.0" CACHE STRING "Build version name")

add_compile_definitions(VERSION_CODE=${VERSION_CODE})
add_compile_definitions(VERSION_NAME="${VERSION_NAME}")

set(CMAKE_INTERPROCEDURAL_OPTIMIZATION ON)

add_compile_options(
    -O3 -DNDEBUG
    -ffunction-sections -fdata-sections
    -fno-exceptions -fno-rtti
    -fno-unwind-tables -fno-asynchronous-unwind-tables
    -fvisibility=hidden -fvisibility-inlines-hidden)

add_subdirectory(KlarheitDSP)

add_library(klarheit SHARED
    src/KlarheitEffect.cpp
    src/KlarheitContext.cpp
)

target_include_directories(klarheit PRIVATE
    src
    src/include
)

target_link_libraries(klarheit KlarheitDSP log)

target_compile_options(klarheit PRIVATE
    -Wall -Wno-unused-parameter -Wno-unused-variable -Wno-unused-function)

target_link_options(klarheit PRIVATE
    -Wl,--gc-sections
    -Wl,--strip-all
    -Wl,--exclude-libs,ALL
    -fvisibility=hidden)
