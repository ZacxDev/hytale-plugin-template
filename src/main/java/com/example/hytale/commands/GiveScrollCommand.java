package com.example.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import java.util.concurrent.CompletableFuture;

/**
 * Command that tells the player how to spawn a Greeting Scroll item.
 * Usage: /givescroll
 *
 * The Greeting_Scroll item is defined in the bundled asset pack under
 * Common/Assets/Items/Greeting_Scroll.json. Direct inventory manipulation
 * requires ECS component access not exposed through the command API,
 * so this command directs players to use the built-in /spawnitem command.
 */
public class GiveScrollCommand extends AbstractCommand {

    private static final String ITEM_ID = "Greeting_Scroll";

    public GiveScrollCommand() {
        super("givescroll", "Spawns a Greeting Scroll item");
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected CompletableFuture<Void> execute(CommandContext ctx) {
        ctx.sendMessage(Message.raw(
            "Use: /spawnitem " + ITEM_ID + " to get a Greeting Scroll!"
        ));
        return CompletableFuture.completedFuture(null);
    }
}
