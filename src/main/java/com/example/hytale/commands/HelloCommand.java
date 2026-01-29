package com.example.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import java.util.concurrent.CompletableFuture;

public class HelloCommand extends AbstractCommand {

    private final OptionalArg<String> nameArg;

    public HelloCommand() {
        super("hello", "Sends a greeting message");
        nameArg = withOptionalArg("name", "Name to greet", ArgTypes.STRING);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected CompletableFuture<Void> execute(CommandContext ctx) {
        String name = ctx.get(nameArg);
        if (name != null) {
            ctx.sendMessage(Message.raw("Hello, " + name + "!"));
        } else {
            ctx.sendMessage(Message.raw("Hello, world!"));
        }
        return CompletableFuture.completedFuture(null);
    }
}
