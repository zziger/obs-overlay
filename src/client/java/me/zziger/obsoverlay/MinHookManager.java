package me.zziger.obsoverlay;

import com.sun.jna.Native;
import me.zziger.obsoverlay.modules.MinHook;

public class MinHookManager {
    static private MinHook instance;
    static MinHook GetInstance() {
        if (instance == null) return instance = Native.load("MinHook", MinHook.class);
        return instance;
    }
}
