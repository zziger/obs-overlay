package me.zziger.obsoverlay.modules;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    static Kernel32 INSTANCE = Native.load("Kernel32", Kernel32.class);

    Pointer GetModuleHandleA(String moduleName);
    Pointer GetProcAddress(Pointer hModule, String procName);
}
