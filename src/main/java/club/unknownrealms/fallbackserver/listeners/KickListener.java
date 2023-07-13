package club.unknownrealms.fallbackserver.listeners;

import club.unknownrealms.fallbackserver.ConfigurationParser;
import club.unknownrealms.fallbackserver.FallBackServer;
import club.unknownrealms.fallbackserver.MessagesArgumentUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.slf4j.Logger;

public class KickListener {
    private static final Logger logger = FallBackServer.getInstance().getLogger();

    @Subscribe(order = PostOrder.FIRST)
    public void onKick(KickedFromServerEvent event) {
        String reason = "No reason provided.";
        if (event.getServerKickReason().isPresent()) {
            JsonObject json = new Gson().fromJson(GsonComponentSerializer.gson().serialize(event.getServerKickReason().get()), JsonObject.class);
            if (json.has("text")) {
                reason = json.get("text").getAsString();
            } else if (json.has("translate")) {
                reason = json.get("translate").getAsString();
            }
        }

        boolean cont = true;
        for (String regexp : ConfigurationParser.getConfiguration().getBlacklistedMessages()) {
            if (reason.matches(regexp)) {
                cont = false;
            }
        }

        logger.info(String.valueOf(cont));

        for (String server : ConfigurationParser.getConfiguration().getBlacklistedServers()) {
            if (server.equals(event.getServer().getServerInfo().getName())) {
                cont = false;
            }
        }

        logger.info(String.valueOf(cont));

        if (!cont) {
            return;
        }

        for (String fallback : ConfigurationParser.getConfiguration().getFallbackServers()) {
            if (FallBackServer.getInstance().getServer().getServer(fallback).isEmpty() ) continue;
            if (event.getServer().getServerInfo().getName().equals(fallback)) continue;

            if (event.kickedDuringServerConnect()) {
                event.setResult(KickedFromServerEvent.RedirectPlayer.create(
                        FallBackServer.getInstance().getServer().getServer(fallback).get(),
                        MessagesArgumentUtil.parseKickMessage(ConfigurationParser.getMessagesConfiguration().getKickMessageJoin(), event.getServer().getServerInfo().getName(), fallback, reason)));
            } else {
                event.setResult(KickedFromServerEvent.RedirectPlayer.create(
                        FallBackServer.getInstance().getServer().getServer(fallback).get(),
                        MessagesArgumentUtil.parseKickMessage(ConfigurationParser.getMessagesConfiguration().getKickMessage(), event.getServer().getServerInfo().getName(), fallback, reason)));
            }
        }
    }
}
