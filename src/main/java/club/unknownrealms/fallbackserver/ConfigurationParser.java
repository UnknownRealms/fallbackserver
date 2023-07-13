package club.unknownrealms.fallbackserver;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurationParser {
    private static Configuration configuration;
    private static MessagesConfiguration messagesConfiguration;

    private static void createFileFromResource(File file, String resource) throws Exception {
        file.createNewFile();
        InputStream defaultFile = FallBackServer.getInstance().getResource(resource);
        FileWriter writer = new FileWriter(file);
        byte[] buffer = new byte[defaultFile.available()];
        defaultFile.read(buffer);
        writer.write(new String(buffer));
        writer.close();
        defaultFile.close();
    }

    public static void parseConfig() throws Exception {
        File configFile = FallBackServer.getInstance().getConfigFile();
        if (!configFile.exists()) {
            createFileFromResource(configFile, "config.yml");
        }
        FileReader reader = new FileReader(configFile);

        Yaml yaml = new Yaml();
        configuration = new Configuration(yaml.load(reader));
    }

    public static void parseMessages() throws Exception {
        File messagesFile = FallBackServer.getInstance().getMessagesFile();
        if (!messagesFile.exists()) {
            createFileFromResource(messagesFile, "messages.yml");
        }
        FileReader reader = new FileReader(messagesFile);

        Yaml yaml = new Yaml();
        messagesConfiguration = new MessagesConfiguration(yaml.load(reader));
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static MessagesConfiguration getMessagesConfiguration() {
        return messagesConfiguration;
    }

    public static class Configuration {
        private final ArrayList<String> fallbackServers;

        private final ArrayList<String> blacklistedMessages;

        private final ArrayList<String> blacklistedServers;

        public Configuration(Map<String, Object> yamlMap) {
            if (yamlMap.get("fallbackServers") instanceof List) {
                fallbackServers = (ArrayList<String>) yamlMap.get("fallbackServers");
            } else {
                throw new IllegalArgumentException("fallbackServers must be a list");
            }

            if (yamlMap.get("blacklistedMessages") instanceof List) {
                blacklistedMessages = (ArrayList<String>) yamlMap.get("blacklistedMessages");
            } else {
                throw new IllegalArgumentException("blacklistedMessages must be a list");
            }

            if (yamlMap.get("blacklistedServers") instanceof List) {
                blacklistedServers = (ArrayList<String>) yamlMap.get("blacklistedServers");
            } else {
                throw new IllegalArgumentException("blacklistedServers must be a list");
            }
        }

        public ArrayList<String> getFallbackServers() {
            return fallbackServers;
        }

        public ArrayList<String> getBlacklistedMessages() {
            return blacklistedMessages;
        }

        public ArrayList<String> getBlacklistedServers() {
            return blacklistedServers;
        }
    }

    public static class MessagesConfiguration {
        private final String kickMessage;
        private final String kickMessageJoin;

        public MessagesConfiguration(Map<String, Object> yamlMap) {
            if (yamlMap.get("kickMessage") instanceof String) {
                kickMessage = (String) yamlMap.get("kickMessage");
            } else {
                throw new IllegalArgumentException("kickMessage must be a string");
            }

            if (yamlMap.get("kickMessageJoin") instanceof String) {
                kickMessageJoin = (String) yamlMap.get("kickMessageJoin");
            } else {
                throw new IllegalArgumentException("kickMessageJoin must be a string");
            }
        }

        public String getKickMessage() {
            return kickMessage;
        }
        public String getKickMessageJoin() {
            return kickMessageJoin;
        }
    }
}
