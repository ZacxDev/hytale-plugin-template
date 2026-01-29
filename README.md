# Hytale Plugin Template

A starter template for building Hytale server plugins. Demonstrates commands, events, and custom items with bundled assets.

[![Build](https://github.com/ZacxDev/hytale-plugin-template/actions/workflows/build.yml/badge.svg)](https://github.com/ZacxDev/hytale-plugin-template/actions/workflows/build.yml)

## Features

- `/hello [name]` - Sends a greeting message (public command, no permission required)
- `/givescroll` - Spawns the custom Greeting Scroll item
- **Greeting Scroll** - Custom item with icon, texture, and model (bundled asset pack)
- **Player join welcome** - Unkeyed event example (`PlayerConnectEvent`)
- **Chat listener** - Keyed event example (`PlayerChatEvent` with `registerGlobal()`)

## Common Gotchas

These are the most common mistakes when developing Hytale plugins:

### Event Registration: Keyed vs Unkeyed

Events in Hytale are either **keyed** or **unkeyed**. Using the wrong registration method causes **compile-time errors**.

| Event Type | Interface | Registration Method |
|------------|-----------|---------------------|
| Unkeyed | `IBaseEvent<Void>` | `register(Class, Consumer)` |
| Keyed | `IBaseEvent<K>` / `IAsyncEvent<K>` | `registerGlobal(Class, Consumer)` |

```java
// CORRECT: PlayerConnectEvent is unkeyed (IBaseEvent<Void>)
getEventRegistry().register(PlayerConnectEvent.class, event -> { ... });

// CORRECT: PlayerChatEvent is keyed (IAsyncEvent<String>)
getEventRegistry().registerGlobal(PlayerChatEvent.class, event -> { ... });

// WRONG: This won't compile!
// getEventRegistry().register(PlayerChatEvent.class, event -> { ... });
// Error: incompatible upper bounds PlayerChatEvent, IBaseEvent<Void>
```

See `ChatListener.java` for a complete keyed event example.

### PNG Textures Must Be 8-bit RGBA

All PNG textures **must be 8-bit/channel RGBA**. 16-bit PNGs will crash the client with "Failed to load CustomUI documents".

```bash
# Validate all textures in your project
./scripts/validate-textures.sh

# Convert a 16-bit PNG to 8-bit
magick input.png -depth 8 -type TrueColorAlpha PNG32:output.png

# Verify format
file texture.png  # Should show "8-bit/color RGBA"
```

### Logging Uses Flogger, Not SLF4J

Hytale uses Google Flogger. The standard `logger.info()` methods don't exist.

```java
// CORRECT
getLogger().atInfo().log("Player %s joined", username);
getLogger().atWarning().log("Something went wrong");

// WRONG - these methods don't exist
// getLogger().info("message");
// getLogger().warn("message");
```

### Item Definitions Go in Server/, Not Common/

Asset packs have two roots with distinct purposes:

| Directory | Purpose | Example |
|-----------|---------|---------|
| `Server/` | Data definitions | `Server/Item/Items/My_Item.json` |
| `Common/` | Visual assets | `Common/Icons/ItemsGenerated/My_Item.png` |

**Wrong**: `Common/Assets/Items/My_Item.json`
**Right**: `Server/Item/Items/My_Item.json`

### Command Permissions

Commands auto-generate a permission node by default. Players without that permission see "you do not have permission." To make a command public:

```java
@Override
protected boolean canGeneratePermission() {
    return false;  // Any player can use this command
}
```

## Quick Start

### Use This Template

1. Click **"Use this template"** on GitHub (or clone directly)
2. Rename your plugin (see [Customization](#customization) below)
3. Build and install

### Prerequisites

- [Nix](https://nixos.org/download.html) with flakes enabled, **OR**
- Java 25 + Gradle manually installed

### Build

```bash
# Enter dev shell (provides Java 25 + Gradle)
nix develop

# Build the plugin JAR
gradle shadowJar
```

Output: `build/libs/ExampleMod-1.0.0.jar`

### Install

Copy the JAR to your Hytale server's `mods/` directory:

```bash
cp build/libs/ExampleMod-1.0.0.jar /path/to/hytale-server/mods/
```

Restart the server. You should see:
```
ExampleMod loaded!
ExampleMod setup complete!
ExampleMod enabled!
```

## Customization

When creating your own plugin from this template, update these files:

### 1. Rename Package & Classes

```bash
# Rename package directory
mv src/main/java/com/example/hytale src/main/java/com/yourname/yourplugin

# Update package declarations in all .java files
find src -name "*.java" -exec sed -i 's/com\.example\.hytale/com.yourname.yourplugin/g' {} +
```

### 2. Update `manifest.json`

```json
{
  "Group": "com.yourname.yourplugin",
  "Name": "YourPlugin",
  "Version": "1.0.0",
  "Description": "Your plugin description",
  "Main": "com.yourname.yourplugin.YourPlugin",
  ...
}
```

### 3. Update `build.gradle`

```groovy
group = 'com.yourname.yourplugin'
version = '1.0.0'

tasks.named('shadowJar') {
    archiveBaseName.set('YourPlugin')
}
```

### 4. Update `settings.gradle`

```groovy
rootProject.name = 'your-plugin-name'
```

## Project Structure

```
hytale-plugin-template/
├── src/main/
│   ├── java/com/example/hytale/
│   │   ├── ExamplePlugin.java          # Main plugin entry point
│   │   ├── commands/
│   │   │   ├── HelloCommand.java       # /hello command (public)
│   │   │   └── GiveScrollCommand.java  # /givescroll command
│   │   └── listener/
│   │       └── ChatListener.java       # Keyed event example
│   └── resources/
│       ├── manifest.json               # Plugin manifest
│       ├── Server/Item/Items/          # Item definitions (data)
│       │   └── Greeting_Scroll.json
│       └── Common/                     # Visual assets
│           ├── Icons/ItemsGenerated/
│           │   └── Greeting_Scroll.png
│           └── Items/Greeting_Scroll/
│               ├── Greeting_Scroll.blockymodel
│               └── Greeting_Scroll_Texture.png
├── scripts/
│   └── validate-textures.sh            # PNG format validator
├── flake.nix                           # Nix dev environment
├── build.gradle                        # Gradle build config
└── settings.gradle
```

## Running a Local Server

The Hytale dedicated server requires game ownership and OAuth2 authentication.

1. Download `hytale-downloader-linux-amd64` from [Hytale Support](https://support.hytale.com/hc/en-us/articles/45326769420827-Hytale-Server-Manual)
2. Run it - follow OAuth2 device auth via `https://accounts.hytale.com/device`
3. Extract the downloaded archive
4. Launch:
   ```bash
   java -Xms4G -Xmx8G -jar Server/HytaleServer.jar --assets ./Assets.zip --bind 0.0.0.0:5520
   ```
5. On first run, authenticate: `/auth login device`
6. Persist auth: `/auth persistence Encrypted`

The server uses **UDP port 5520** (QUIC protocol).

## API Reference

### Plugin Lifecycle

```java
public class MyPlugin extends JavaPlugin {
    public MyPlugin(JavaPluginInit init) {
        super(init);
        // Constructor - plugin loaded
    }

    @Override
    protected void setup() {
        // Register commands and events
    }

    @Override
    protected void start() {
        // Plugin fully enabled
    }

    @Override
    protected void shutdown() {
        // Cleanup
    }
}
```

### Commands

```java
public class MyCommand extends AbstractCommand {
    public MyCommand() {
        super("mycommand", "Description");
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Make command public (no permission required)
    }

    @Override
    protected CompletableFuture<Void> execute(CommandContext ctx) {
        ctx.sendMessage(Message.raw("Hello!"));
        return CompletableFuture.completedFuture(null);
    }
}
```

### Events

```java
// Unkeyed events (PlayerConnectEvent, etc.)
getEventRegistry().register(PlayerConnectEvent.class, event -> {
    event.getPlayerRef().sendMessage(Message.raw("Welcome!"));
});

// Keyed events (PlayerChatEvent, etc.) - use registerGlobal
getEventRegistry().registerGlobal(PlayerChatEvent.class, event -> {
    // Handle chat
});
```

### Logging

Hytale uses Google Flogger:

```java
getLogger().atInfo().log("Message here");
getLogger().atWarning().log("Warning: %s", details);
```

## Asset Pack Notes

When `"IncludesAssetPack": true` in manifest.json:

- `Server/` - Data definitions (item JSON, block JSON)
- `Common/` - Visual assets (icons, textures, models)

**Important**: Item definitions go in `Server/Item/Items/`, not `Common/Assets/Items/`.

### PNG Requirements

All PNG textures must be **8-bit/channel RGBA**. 16-bit PNGs crash the client.

```bash
# Convert to correct format
magick input.png -depth 8 -type TrueColorAlpha PNG32:output.png

# Verify
file texture.png  # Should show "8-bit/color RGBA"
```

## Hytale SDK Version

This template uses Hytale Server SDK version `2026.01.28-87d03be09`.

To update, change the version in `build.gradle`:

```groovy
dependencies {
    compileOnly("com.hypixel.hytale:Server:YYYY.MM.DD-hash")
}
```

## Resources

- [Hytale Modding Community](https://hytalemodding.dev)
- [Hytale Server Manual](https://support.hytale.com/hc/en-us/articles/45326769420827-Hytale-Server-Manual)
- [Hytale DevLib](https://htdevlib.netlify.app)

## License

MIT License - see [LICENSE](LICENSE)
