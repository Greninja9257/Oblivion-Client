# Oblivion Client

Oblivion Client is a modular Minecraft utility client built on Fabric. It includes gameplay modules, HUD/GUI customization, account and proxy utilities, and an integrated Mineflayer bridge for multi-bot automation.

## Features

- Modular system across combat, movement, render, world, misc, and bot utilities
- Click GUI and HUD editor with persistent config files
- Bot bridge integration (`mineflayer-bridge`) with commands for swarm spawn, follow, mine, farm, guard, collect, chat, and task control
- Account and proxy management support
- JSON-based runtime configuration in the client data directory

## Project Structure

- `src/main/java/dev/oblivion/client` - client source code
- `src/main/resources` - Fabric metadata and mixin config
- `gradle/`, `build.gradle`, `settings.gradle` - build system
- `run/` - local runtime data (ignored in git)

## Build

```bash
./gradlew build
```

## Development Notes

- Requires Java and Gradle environment compatible with the project setup.
- Keep local runtime data (`run/`) out of version control.
