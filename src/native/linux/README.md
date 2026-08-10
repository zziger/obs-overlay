# Linux MinHook shim (`libMinHook.so`)

MinHook-compatible hooking for Linux, built on [funchook](https://github.com/kubo/funchook).
It exposes the same 3-function API the Java side uses on Windows (`MH_Initialize`,
`MH_CreateHook`, `MH_EnableHook`), so `me.zziger.obsoverlay.modules.MinHook` works on
both platforms unchanged.

Everything except the `MH_*` symbols is hidden (`--version-script`), and funchook +
capstone are statically linked in, so the shipped `.so` only depends on libc.

## Why this replaces the OpenGL swap hook

The Windows implementation hooks `wglSwapBuffers` with MinHook. On Linux the
equivalent low-level swap functions are:

| backend | function |
|---|---|
| GLX    | `glXSwapBuffers`, `glXSwapBuffersMscOML` (GLVND dispatch in `libGL.so.1` / `libGLX.so.0`) |
| EGL    | `eglSwapBuffers` (`libEGL.so.1`) |

funchook correctly relocates RIP-relative instructions when moving the prologue
into a trampoline, which matters here because GLVND's stubs start with
`mov rax, [rip+disp32]`-style instructions that a naive inline hook would corrupt.

## Rebuilding

Requires `cmake` and network access (funchook + capstone are fetched via
`FetchContent` at the pinned commit from `CMakeLists.txt`):

```sh
cmake -S src/native/linux -B src/native/linux/build -DCMAKE_BUILD_TYPE=Release
cmake --build src/native/linux/build --target MinHookLinux -j
strip -s src/native/linux/build/libMinHook.so
cp src/native/linux/build/libMinHook.so src/main/resources/lib/libMinHook.linux-x86_64.so
```

The committed binary `src/main/resources/lib/libMinHook.linux-x86_64.so` is what
`OverlayHook.initLibrary()` copies to the game folder at runtime and loads as
`MinHook`.

## License

`minhook_linux.c` is licensed under the mod's MIT license. The statically linked
[funchook](https://github.com/kubo/funchook) is licensed
[GPLv2+ with linking exception](LICENSE.funchook); this exception permits
distribution alongside or within independently-licensed modules. funchook source
is available at the commit pinned in `CMakeLists.txt` (`FUNCHOOK_GIT_TAG`).