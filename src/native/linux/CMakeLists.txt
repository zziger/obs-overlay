# Builds src/native/linux => libMinHook.so, the Linux MinHook-compatible hook
# library bundled in the mod jar.
#
# Usage:
#   cmake -S src/native/linux -B build/linux -DCMAKE_BUILD_TYPE=Release
#   cmake --build build/linux --target MinHookLinux
#
# Produces: build/linux/libMinHook.so
#
# Dependencies are fetched over the network at configure time:
#   - funchook (GPLv2+ with linking exception) at FUNCHOOK_GIT_TAG
#   - capstone (BSD) as funchook's disassembler
cmake_minimum_required(VERSION 3.18)
project(overlay-minhook-linux LANGUAGES C)

set(CMAKE_POSITION_INDEPENDENT_CODE ON)
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

# Pin the exact funchook source this build is based on.
set(FUNCHOOK_GIT_REPO "https://github.com/kubo/funchook.git" CACHE STRING "")
set(FUNCHOOK_GIT_TAG "b4991704add411ecbc492dae020f375124d51f45" CACHE STRING "")

set(FUNCHOOK_DISASM "capstone" CACHE STRING "disassembler engine used by funchook")

include(FetchContent)

FetchContent_Declare(
    funchook
    GIT_REPOSITORY ${FUNCHOOK_GIT_REPO}
    GIT_TAG        ${FUNCHOOK_GIT_TAG}
)
set(FUNCHOOK_BUILD_SHARED OFF CACHE BOOL "" FORCE)
set(FUNCHOOK_BUILD_STATIC ON  CACHE BOOL "" FORCE)
set(FUNCHOOK_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(BUILD_TESTING OFF CACHE BOOL "" FORCE)
set(CAPSTONE_BUILD_TESTS OFF CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(funchook)

# funchook's own CMakeLists already FetchContent's the capstone engine when
# FUNCHOOK_DISASM=capstone and exposes the targets it needs.
add_library(MinHookLinux SHARED minhook_linux.c)
set_target_properties(MinHookLinux PROPERTIES
    OUTPUT_NAME "MinHook"
    PREFIX "lib"
)
target_include_directories(MinHookLinux PRIVATE ${funchook_SOURCE_DIR}/include)
target_link_libraries(MinHookLinux PRIVATE funchook-static)

# Hide everything except the MinHook API the Java side binds to.
set(MHL_VERSION_SCRIPT "${CMAKE_CURRENT_SOURCE_DIR}/minhook.map")
set_target_properties(MinHookLinux PROPERTIES
    LINK_DEPENDS "${MHL_VERSION_SCRIPT}"
)
target_link_options(MinHookLinux PRIVATE "LINKER:--version-script=${MHL_VERSION_SCRIPT}")