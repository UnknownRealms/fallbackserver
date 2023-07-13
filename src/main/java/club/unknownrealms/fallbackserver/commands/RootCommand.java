package club.unknownrealms.fallbackserver.commands;

import club.unknownrealms.fallbackserver.ConfigurationParser;
import club.unknownrealms.fallbackserver.FallBackServer;
import com.velocitypowered.api.command.*;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class RootCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args[0].equals("reload")) {
            source.sendMessage(Component.text("Reloading configuration...", GREEN));
            try {
                ConfigurationParser.parseConfig();
                source.sendMessage(Component.text("Reloaded configuration!", GREEN));
            } catch (Exception e) {
                source.sendMessage(Component.text("Failed to reload configuration!", RED));
                e.printStackTrace();
            }

            source.sendMessage(Component.text("Reloading messages...", GREEN));
            try {
                ConfigurationParser.parseMessages();
                source.sendMessage(Component.text("Reloaded messages!", GREEN));
            } catch (Exception e) {
                source.sendMessage(Component.text("Failed to reload messages!", RED));
                e.printStackTrace();
            }
        } else {
            source.sendMessage(Component.text("Usage: /fallbackserver reload", RED));
        }
    }

    public static CommandMeta getMeta(CommandManager manager) {
        return manager.metaBuilder("fallbackserver").plugin(FallBackServer.getInstance()).build();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        ArrayList<String> suggests = new ArrayList<>(1);
        suggests.add("reload");
        return suggests;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        ArrayList<String> suggests = new ArrayList<>(1);
        suggests.add("reload");

        future.complete(suggests);
        return future;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("fallbackserver.reload");
    }
}
