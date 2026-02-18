package dev.oblivion.client.addon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.Module;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads addons from disk. Handles both Java source (compiled at runtime) and JSON config addons.
 */
public class AddonLoader {
    private final AddonCompiler compiler = new AddonCompiler();
    private final Map<String, URLClassLoader> classLoaders = new HashMap<>();

    /**
     * Loads a single addon from its directory.
     * The directory must contain an addon.json metadata file.
     */
    public Addon loadAddon(Path addonDir) throws AddonException {
        Path metaFile = addonDir.resolve("addon.json");
        if (!Files.exists(metaFile)) {
            throw new AddonException("Missing addon.json in " + addonDir);
        }

        JsonObject meta;
        try {
            meta = JsonParser.parseString(Files.readString(metaFile, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new AddonException("Failed to read addon.json: " + e.getMessage());
        }

        Addon addon = Addon.fromJson(meta);

        if (addon.getType() == Addon.Type.JAVA_SOURCE) {
            loadJavaAddon(addon, addonDir, meta);
        } else {
            loadJsonAddon(addon, addonDir);
        }

        addon.setStatus(Addon.Status.INSTALLED);
        return addon;
    }

    private void loadJavaAddon(Addon addon, Path addonDir, JsonObject meta) throws AddonException {
        if (!meta.has("mainClass")) {
            throw new AddonException("Java addon missing 'mainClass' in addon.json");
        }

        URLClassLoader cl = compiler.compile(addonDir);
        if (cl == null) {
            throw new AddonException("No .java source files found in addon directory");
        }
        classLoaders.put(addon.getId(), cl);

        String mainClass = meta.get("mainClass").getAsString();
        try {
            Class<?> clazz = cl.loadClass(mainClass);
            if (!Module.class.isAssignableFrom(clazz)) {
                throw new AddonException("mainClass '" + mainClass + "' must extend Module");
            }
            Module module = (Module) clazz.getDeclaredConstructor().newInstance();
            addon.setLoadedModule(module);
        } catch (AddonException e) {
            throw e;
        } catch (Exception e) {
            throw new AddonException("Failed to instantiate addon class '" + mainClass + "': " + e.getMessage());
        }
    }

    private void loadJsonAddon(Addon addon, Path addonDir) throws AddonException {
        Path configFile = addonDir.resolve("module.json");
        if (!Files.exists(configFile)) {
            throw new AddonException("JSON addon missing module.json");
        }

        JsonObject config;
        try {
            config = JsonParser.parseString(Files.readString(configFile, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new AddonException("Failed to read module.json: " + e.getMessage());
        }

        AddonModule module = new AddonModule(addon, config);
        addon.setLoadedModule(module);
    }

    public void unloadAddon(String addonId) {
        URLClassLoader cl = classLoaders.remove(addonId);
        if (cl != null) {
            try {
                cl.close();
            } catch (IOException e) {
                OblivionClient.LOGGER.warn("Failed to close classloader for addon: " + addonId, e);
            }
        }
    }
}
