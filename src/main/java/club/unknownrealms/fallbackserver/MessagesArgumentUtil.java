package club.unknownrealms.fallbackserver;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessagesArgumentUtil {
    public static Component parseKickMessage(String kickMessage, String server, String fallback, String reason) {
        kickMessage = kickMessage.replace("%server%", server).replace("%fallback%", fallback).replace("%reason%", reason);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessage);
    }
}
