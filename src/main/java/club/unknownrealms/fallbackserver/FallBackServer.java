package club.unknownrealms.fallbackserver;

import club.unknownrealms.fallbackserver.commands.RootCommand;
import club.unknownrealms.fallbackserver.listeners.KickListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

@Plugin(
        id = "fallbackserver",
        name = "FallBackServer",
        version = BuildConstants.VERSION
)
public class FallBackServer {
    private static FallBackServer instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private File configFile;
    private File messagesFile;

    @Inject
    public FallBackServer(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws Exception {
        dataDirectory.toFile().mkdirs();

        configFile = new File(dataDirectory + "/config.yml");
        messagesFile = new File(dataDirectory + "/messages.yml");

        try {
            ConfigurationParser.parseConfig();
            ConfigurationParser.parseMessages();
        } catch (Exception e) {
            logger.error("Error parsing configuration files: " + e.getMessage());
            throw e;
        }

        server.getEventManager().register(this, new KickListener());
        server.getCommandManager().register(RootCommand.getMeta(server.getCommandManager()), new RootCommand());

        logger.info("FallBackServer has been enabled!");
    }

    public static FallBackServer getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return instance.server;
    }

    public Logger getLogger() {
        return instance.logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getMessagesFile() {
        return messagesFile;
    }

    public InputStream getResource(String name) {
        return getClass().getResourceAsStream("/" + name);
    }
}
