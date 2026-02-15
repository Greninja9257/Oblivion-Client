# Oblivion Client

Oblivion Client is a Fabric-based Minecraft utility client with a modular hack framework, configurable HUD/GUI, account/proxy management, and an integrated Mineflayer bridge for bot automation.

## Minecraft Version

- Target Minecraft version: **1.21.4**
- Yarn mappings: `1.21.4+build.8`
- Fabric Loader: `0.16.10`
- Fabric API: `0.119.2+1.21.4`
- Java target: **21**

Source of truth: `gradle.properties` and `src/main/resources/fabric.mod.json`.

## Highlights

- Modular cheat/utility system with event-driven architecture
- 188 registered modules at startup
- Click GUI + HUD editor with persistent JSON config
- Command system with configurable prefix
- Built-in Mineflayer runtime manager + HTTP bridge API
- Offline account switching and proxy support
- Profile save/load support

## Module Inventory

Core modules are registered in `ModuleManager`, and Wurst-compatible modules are registered in `WurstModulesRegistrar`.

Core categories:

- Combat: 8
- Movement: 10
- Render: 8
- Player: 9
- World: 4
- Misc: 5
- Bots: 14

Wurst-compatible modules:

- Count: 130
- Registered via `src/main/java/dev/oblivion/client/module/wurst/WurstModulesRegistrar.java`

Total registered modules: **188**.

## Bot System

### Runtime and Bridge

The bot bridge is managed by `BotRuntimeManager` and exposed locally via:

- Health: `http://127.0.0.1:3099/health`
- Commands: `http://127.0.0.1:3099/api/bots/command`

Bridge runtime files are managed in:

- `run/run/oblivion-client/mineflayer-bridge`

### Supported Bot Actions

HTTP `action` values currently supported:

- `spawn_swarm`
- `disconnect_all`
- `set_names`
- `auto_register`
- `follow_start`
- `follow_stop`
- `mine_start`
- `mine_stop`
- `farm_start`
- `farm_stop`
- `guard_start`
- `guard_stop`
- `collect_start`
- `collect_stop`
- `deposit`
- `stop_tasks`
- `chat_send`
- `chat_spam_start`
- `chat_spam_stop`
- `status`

### Bot Modules

The in-client bot modules under `src/main/java/dev/oblivion/client/module/bots` include:

- BotSpawnSwarm
- BotDisconnectAll
- BotAutoRegister
- BotFollow
- BotMine
- BotFarm
- BotGuard
- BotCollectDrops
- BotDeposit
- BotSendChat
- BotRawCommand
- BotRandomizeNames
- BotStopTasks
- BotModule (base)

## Commands

Built-in command set (`CommandManager`):

- `help` (`h`, `?`)
- `modules` (`mods`)
- `toggle` (`t`) `toggle <module>`
- `bind` (`b`) `bind <module> <key>`
- `prefix` `prefix [char]`
- `account` (`alt`) `account <set|list|active> [name]`
- `friend` (`f`) `friend <add|remove|list> [name]`

Default command prefix is `.`.

## Configuration and Data

Runtime config is written under:

- `run/oblivion-client`

Main files:

- `modules.json` - module state + settings
- `client.json` - command prefix + active profile
- `hud.json` - HUD element config
- `gui.json` - Click GUI state
- `friends.json` - friends list
- `accounts.json` - offline account names
- `proxies.json` - proxy entries
- `profiles/*.json` - named profile snapshots

Important: `run/` is intentionally ignored in git.

## Build

### Requirements

- Java 21
- Gradle wrapper (`./gradlew`)

### Build Commands

```bash
./gradlew build
```

Useful:

```bash
./gradlew compileJava
./gradlew clean build
```

## Project Structure

- `src/main/java/dev/oblivion/client` - client source
- `src/main/resources` - Fabric metadata, mixins, access widener
- `gradle/` + Gradle scripts - build tooling
- `run/` - local runtime data and generated bridge runtime (ignored)

## Development Notes

- The bot bridge runtime is generated/updated by `BotRuntimeManager` (embedded JS template + package metadata).
- If bot actions fail unexpectedly, verify local bridge health at `/health` and that the process on port `3099` matches generated bridge code.
- `run/` may contain private or local-only data (accounts/proxies/history/logs); do not commit it.

## License

This repository includes a `LICENSE` file. See it for distribution and usage terms.
