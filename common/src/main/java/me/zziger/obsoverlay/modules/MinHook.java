package me.zziger.obsoverlay.modules;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface MinHook extends StdCallLibrary {
    public static interface wglSwapBuffers extends StdCallCallback {
        boolean callback(Pointer hDc);
    }

    int MH_Initialize();
    int MH_CreateHook(Pointer method, wglSwapBuffers hook, PointerByReference origMethod);
    int MH_EnableHook(Pointer method);
}
