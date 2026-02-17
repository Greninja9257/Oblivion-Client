package dev.oblivion.client;

import dev.oblivion.client.account.AccountManager;
import dev.oblivion.client.bot.BotManager;
import dev.oblivion.client.command.CommandManager;
import dev.oblivion.client.config.ConfigManager;
import dev.oblivion.client.event.EventBus;
import dev.oblivion.client.gui.hud.HudManager;
import dev.oblivion.client.gui.notification.NotificationManager;
import dev.oblivion.client.module.ModuleManager;
import dev.oblivion.client.plugin.PluginManager;
import dev.oblivion.client.proxy.ProxyManager;
import dev.oblivion.client.social.FriendManager;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OblivionClient {
    public static final String NAME = "Oblivion";
    public static final String VERSION = "2.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static final OblivionClient INSTANCE = new OblivionClient();

    public final EventBus eventBus = new EventBus();
    public final ModuleManager moduleManager = new ModuleManager();
    public final CommandManager commandManager = new CommandManager();
    public final ConfigManager configManager = new ConfigManager();
    public final FriendManager friendManager = new FriendManager();
    public final AccountManager accountManager = new AccountManager();
    public final ProxyManager proxyManager = new ProxyManager();
    public final BotManager botManager = new BotManager();
    public final HudManager hudManager = new HudManager();
    public final PluginManager pluginManager = new PluginManager();
    public final NotificationManager notificationManager = new NotificationManager();

    private volatile boolean initialized = false;

    public static OblivionClient get() {
        return INSTANCE;
    }

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    public synchronized void initialize() {
        if (initialized) return;
        initialized = true;

        LOGGER.info("Initializing {} v{}", NAME, VERSION);

        try { moduleManager.init(); } catch (Exception e) { LOGGER.error("Failed to init modules", e); }
        try { commandManager.init(); } catch (Exception e) { LOGGER.error("Failed to init commands", e); }
        try { hudManager.init(); } catch (Exception e) { LOGGER.error("Failed to init HUD", e); }
        try { friendManager.init(); } catch (Exception e) { LOGGER.error("Failed to init friends", e); }
        try { accountManager.init(); } catch (Exception e) { LOGGER.error("Failed to init accounts", e); }
        try { proxyManager.init(); } catch (Exception e) { LOGGER.error("Failed to init proxies", e); }
        try { botManager.init(); } catch (Exception e) { LOGGER.error("Failed to init bot manager", e); }
        try { pluginManager.init(); } catch (Exception e) { LOGGER.error("Failed to init plugins", e); }
        try { configManager.load(); } catch (Exception e) { LOGGER.error("Failed to load config", e); }

        LOGGER.info("{} v{} initialized successfully!", NAME, VERSION);
    }

    public synchronized void shutdown() {
        LOGGER.info("Shutting down {}...", NAME);
        try { configManager.save(); } catch (Exception e) { LOGGER.error("Failed to save config", e); }
        try { botManager.shutdown(); } catch (Exception e) { LOGGER.error("Failed to shutdown bot manager", e); }
        try { pluginManager.shutdown(); } catch (Exception e) { LOGGER.error("Failed to shutdown plugins", e); }
        initialized = false;
    }

    public EventBus getEventBus() { return eventBus; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public CommandManager getCommandManager() { return commandManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public FriendManager getFriendManager() { return friendManager; }
    public AccountManager getAccountManager() { return accountManager; }
    public ProxyManager getProxyManager() { return proxyManager; }
    public BotManager getBotManager() { return botManager; }
    public HudManager getHudManager() { return hudManager; }
    public PluginManager getPluginManager() { return pluginManager; }
    public NotificationManager getNotificationManager() { return notificationManager; }
}
