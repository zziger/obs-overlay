package me.zziger.obsoverlay.modules;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * {@code stdcall} binding of the MinHook hooking API, loaded from the bundled
 * {@code MinHook.dll} on Windows.
 *
 * <p>On Linux the same native symbols are loaded via {@link MinHookLinux} with
 * the C calling convention instead.
 */
public interface MinHook extends StdCallLibrary {

    /** {@code BOOL wglSwapBuffers(HDC hdc)} on Windows. */
    interface wglSwapBuffers extends StdCallCallback {
        boolean callback(Pointer hDc);
    }

    int MH_Initialize();

    int MH_CreateHook(Pointer method, Callback hook, PointerByReference origMethod);

    int MH_EnableHook(Pointer method);
}