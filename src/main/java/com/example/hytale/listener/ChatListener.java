package com.example.hytale.listener;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.logger.HytaleLogger;

/**
 * Demonstrates how to listen to keyed events using registerGlobal().
 *
 * IMPORTANT: PlayerChatEvent implements IAsyncEvent<String> (a keyed event).
 * You CANNOT use register(PlayerChatEvent.class, handler) - it won't compile!
 *
 * For keyed events, use one of:
 *   - registerGlobal(Class, Consumer) - receives ALL events regardless of key
 *   - register(Class, K key, Consumer) - receives only events matching the key
 *
 * Common mistake that fails at compile time:
 *   getEventRegistry().register(PlayerChatEvent.class, event -> { ... });
 *   // Error: incompatible upper bounds PlayerChatEvent, IBaseEvent<Void>
 */
public class ChatListener {

    private final HytaleLogger logger;

    public ChatListener(HytaleLogger logger) {
        this.logger = logger;
    }

    /**
     * Registers chat event handlers with the event registry.
     *
     * @param eventRegistry The plugin's event registry
     */
    public void register(EventRegistry eventRegistry) {
        // registerGlobal receives ALL chat events regardless of key
        eventRegistry.registerGlobal(PlayerChatEvent.class, this::onPlayerChat);
    }

    private void onPlayerChat(PlayerChatEvent event) {
        String username = event.getSender().getUsername();
        String content = event.getContent();

        logger.atInfo().log("[Chat] %s: %s", username, content);

        // Example: simple chat filter (you'd want something more robust in production)
        if (content.toLowerCase().contains("hello")) {
            event.getSender().sendMessage(
                com.hypixel.hytale.server.core.Message.raw("Hey there, " + username + "!")
            );
        }
    }
}
