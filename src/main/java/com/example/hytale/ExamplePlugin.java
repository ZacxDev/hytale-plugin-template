package com.example.hytale;

import com.example.hytale.commands.GiveScrollCommand;
import com.example.hytale.commands.HelloCommand;
import com.example.hytale.listener.ChatListener;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

public class ExamplePlugin extends JavaPlugin {

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        getLogger().atInfo().log("ExampleMod loaded!");
    }

    @Override
    protected void setup() {
        // Register commands
        getCommandRegistry().registerCommand(new HelloCommand());
        getCommandRegistry().registerCommand(new GiveScrollCommand());

        // Register event listeners
        // Unkeyed event (IBaseEvent<Void>) - use register()
        getEventRegistry().register(PlayerConnectEvent.class, event -> {
            String name = event.getPlayerRef().getUsername();
            event.getPlayerRef().sendMessage(
                Message.raw("Welcome to the server, " + name + "!")
            );
        });

        // Keyed event (IAsyncEvent<String>) - use registerGlobal()
        // See ChatListener for detailed explanation of keyed vs unkeyed events
        new ChatListener(getLogger()).register(getEventRegistry());

        getLogger().atInfo().log("ExampleMod setup complete!");
    }

    @Override
    protected void start() {
        getLogger().atInfo().log("ExampleMod enabled!");
    }

    @Override
    protected void shutdown() {
        getLogger().atInfo().log("ExampleMod disabled!");
    }
}
