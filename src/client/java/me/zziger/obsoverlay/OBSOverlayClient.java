package me.zziger.obsoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OBSOverlayClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("obs-overlay");
	public static final String MOD_ID = "obs-overlay";

	public static boolean copyLibrary() {
		InputStream libFile = OBSOverlayClient.class.getResourceAsStream("/lib/MinHook.x64.dll");
		if (libFile == null) {
			LOGGER.error("Failed to get MinHook.x64.dll dependency");
			return false;
		}

		File nativeDir = new File(FabricLoader.getInstance().getGameDir().toAbsolutePath().toString().concat("/native"));
		File copyLibFile = new File(FabricLoader.getInstance().getGameDir().toAbsolutePath().toString().concat("/native/MinHook.dll"));
		nativeDir.mkdir();

		try {
			FileOutputStream fos = new FileOutputStream(copyLibFile);
			copyLibFile.createNewFile();
			IOUtils.copy(libFile, fos);
			fos.close();
		} catch (IOException e) {
			LOGGER.error("Failed to copy dependency DLL");
			return false;
		}

		System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
		LOGGER.info("Copied dependency DLL successfully");
		return true;
	}

	private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		throw (E) e;
	}

	@Override
	public void onInitializeClient() {
		if (!copyLibrary()) {
			sneakyThrow(new Exception("Failed to load OBS Overlay mod"));
		}

		OBSOverlayConfig.init();
	}
}