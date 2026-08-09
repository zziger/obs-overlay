package me.zziger.obsoverlay.modules;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Linux variant of {@link MinHook} — same native symbols, but <em>C</em>
 * calling convention instead of <code>stdcall</code>.
 *
 * <p>JNA 5.15's native binder rejects the stdcall flag on Linux x86_64
 * ("Unrecognized calling convention"), so the Linux path must use a plain
 * {@link Library} interface. The backing library is the funchook-based
 * {@code libMinHook.so} bundled in the jar (see {@code src/native/linux}).
 */
public interface MinHookLinux extends Library {

    /** Both GLX and EGL swap functions take two pointers. */
    interface SwapBuffers extends Callback {
        int callback(Pointer first, Pointer second);
    }

    int MH_Initialize();

    int MH_CreateHook(Pointer method, Callback hook, PointerByReference origMethod);

    int MH_EnableHook(Pointer method);
}