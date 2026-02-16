# Oblivion Client

A feature-rich Minecraft 1.21.4 utility client built on the Fabric modloader. Ships with 215+ modules spanning combat, movement, rendering, world manipulation, bot automation, server exploits, and 128 integrated Wurst-compatible modules.

<p align="center">
  <img src="logo/logo.png" alt="Oblivion Client Logo">
</p>

<p align="center">
  <img src="https://img.shields.io/github/stars/Greninja9257/Oblivion-Client">
  <img src="https://img.shields.io/github/all-contributors/Greninja9257/Oblivion-Client?color=ee8449&style=flat-square">   
</p>

## Minecraft Version

- Target Minecraft version: **1.21.4**
- Yarn mappings: `1.21.4+build.8`
- Fabric Loader: `0.16.10`
- Fabric API: `0.119.2+1.21.4`
- Java target: **21**

Source of truth: `gradle.properties` and `src/main/resources/fabric.mod.json`.

## Highlights

- Modular cheat/utility system with event-driven architecture
- 215+ registered modules at startup across 8 categories
- Click GUI + HUD editor with persistent JSON config
- Command system with configurable prefix
- Built-in Mineflayer runtime manager + HTTP bridge API for bot swarm control
- Offline account switching and proxy support
- Profile save/load support
- Comprehensive exploit/griefing toolkit (27 modules)

## Build

### Requirements

- Java 21
- Gradle wrapper (`./gradlew`)

### Build Commands

```bash
./gradlew build          # Full build
./gradlew compileJava    # Compile only
./gradlew clean build    # Clean rebuild
```

The compiled jar will be in `build/libs/`.

### Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.4
2. Drop the built jar and Fabric API into your `.minecraft/mods/` folder
3. Launch Minecraft with the Fabric profile

## Module Inventory

Core modules are registered in `ModuleManager`, Wurst-compatible modules via `WurstModulesRegistrar`.

### Combat (8 modules)
KillAura, CrystalAura, Aimbot, AutoTotem, Criticals, Reach, Velocity, AutoArmor

### Movement (10 modules)
Fly, Speed, Sprint, NoFall, Step, Jesus, ElytraFly, BoatFly, Spider, Scaffold

### Render (8 modules)
ESP, Tracers, Fullbright, Nametags, NoRender, Xray, ChestESP, Freecam

### Player (9 modules)
AutoEat, AutoSoup, AutoTool, FastPlace, FastBreak, NoSlowdown, AntiHunger, CreativeMode, InventorySort

### World (4 modules)
Nuker, AutoBuild, Timer, ChunkLoader

### Misc (5 modules)
AutoDisconnect, FakePlayer, Spammer, AutoReconnect, MiddleClickFriend

### Bots (13 modules)
BotSpawnSwarm, BotDisconnectAll, BotAutoRegister, BotFollow, BotMine, BotFarm, BotGuard, BotCollectDrops, BotDeposit, BotSendChat, BotRawCommand, BotRandomizeNames, BotStopTasks

### Exploit (27 modules)

| Module | Description |
|--------|-------------|
| ServerCrasher | Sends malformed packets (Position / Action / Interact / All modes) |
| PacketCrasher | NaN positions, oversized creative slots, random digging/placement |
| PacketFlood | Floods server with swing / sneak / digging packets |
| BookExploit | Creates books with oversized NBT data (creative) |
| BookBan | Creates ban books that crash players who open them (creative) |
| BookOP | Social-engineering book that tricks admins into running commands |
| SignCrash | Sends oversized sign update packets |
| ChunkCrash | Requests invalid chunk positions |
| ChunkOverload | Forces server to load excessive chunks |
| EntitySpam | Spams entity-spawning interactions |
| ArmorStandLag | Mass armor stand placement (creative) |
| BoatLag | Mass boat placement (creative) |
| MobSpam | Spam spawn eggs with selectable mob type (creative) |
| NBTCrash | Deeply nested NBT data items (creative) |
| TabCompleteCrash | Floods tab-complete requests |
| CommandSpam | Floods command executions |
| InventoryCrash | Malformed inventory click packets |
| WindowClickSpam | Spams window click packets |
| CreativeNBTExploit | Kill item / Crash item / Giant NBT modes (creative) |
| ItemFrameLag | Mass item frame placement (creative) |
| MapDataCrash | Maps with oversized data (creative) |
| PayloadCrash | Oversized brand payload packets |
| PluginMessageCrash | Malformed brand message flooding |
| TransactionSpam | Inventory transaction flood |
| MovementSpam | Position / Look / Full / Random movement flood |
| BlockPlacementSpam | Block placement packet flood |
| ShulkerBoxCrash | Recursively nested shulker boxes (creative) |

### Wurst-Compatible (128 modules)

A full suite of ported Wurst modules including AimAssist, AirPlace, AnchorAura, AntiAfk, AutoFarm, AutoFish, AutoMine, BowAimbot, CaveFinder, CreativeFlight, Excavator, ExtraElytra, FightBot, Glide, ItemEsp, Jetpack, KillauraLegit, MassTpa, MultiAura, NewChunks, NoClip, Parkour, Search, SpeedHack, Trajectories, TreeBot, TriggerBot, Tunneller, VeinMiner, and many more.

Registered via `WurstModulesRegistrar.java`.

## Bot System

### Runtime and Bridge

The bot bridge is managed by `BotRuntimeManager` and exposed locally via:

- Health: `http://127.0.0.1:3099/health`
- Commands: `http://127.0.0.1:3099/api/bots/command`

Bridge runtime files are located in `run/run/oblivion-client/mineflayer-bridge`.

### Supported Bot Actions

HTTP `action` values:

| Action | Description |
|--------|-------------|
| `spawn_swarm` | Spawn a configurable number of bots |
| `disconnect_all` | Disconnect all active bots |
| `set_names` | Randomize bot names |
| `auto_register` | Auto-register bots on auth servers |
| `follow_start` / `follow_stop` | Follow a target player |
| `mine_start` / `mine_stop` | Mine blocks around a position |
| `farm_start` / `farm_stop` | Farm crops automatically |
| `guard_start` / `guard_stop` | Guard a position and attack hostiles |
| `collect_start` / `collect_stop` | Collect dropped items |
| `deposit` | Deposit inventory into nearby chests |
| `stop_tasks` | Stop all running bot tasks |
| `chat_send` | Send a single chat message |
| `chat_spam_start` / `chat_spam_stop` | Spam chat messages |
| `status` | Query bot bridge status |

## Commands

Built-in command set (`CommandManager`):

| Command | Aliases | Usage |
|---------|---------|-------|
| `help` | `h`, `?` | Show available commands |
| `modules` | `mods` | List all modules |
| `toggle` | `t` | `toggle <module>` |
| `bind` | `b` | `bind <module> <key>` |
| `prefix` | | `prefix [char]` |
| `account` | `alt` | `account <set\|list\|active> [name]` |
| `friend` | `f` | `friend <add\|remove\|list> [name]` |

Default command prefix is `.`.

## Configuration and Data

Runtime config is written under `run/oblivion-client/`:

| File | Purpose |
|------|---------|
| `modules.json` | Module state + settings |
| `client.json` | Command prefix + active profile |
| `hud.json` | HUD element config |
| `gui.json` | Click GUI state |
| `friends.json` | Friends list |
| `accounts.json` | Offline account names |
| `proxies.json` | Proxy entries |
| `profiles/*.json` | Named profile snapshots |

## Architecture

```
dev.oblivion.client/
├── OblivionMod.java          # Fabric entrypoint (ClientModInitializer)
├── OblivionClient.java       # Singleton manager for all subsystems
├── module/                   # Module system
│   ├── Module.java           # Abstract base class
│   ├── Category.java         # Category enum (8 categories)
│   ├── ModuleManager.java    # Registration and lookup
│   ├── combat/               # 8 combat modules
│   ├── movement/             # 10 movement modules
│   ├── render/               # 8 render modules
│   ├── player/               # 9 player modules
│   ├── world/                # 4 world modules
│   ├── misc/                 # 5 misc modules
│   ├── bots/                 # 13 bot bridge modules + BotModule base
│   ├── exploit/              # 27 exploit modules
│   └── wurst/                # 128 Wurst-compatible modules + registrar
├── event/                    # Event bus system
│   ├── EventBus.java         # Reflection-based pub/sub with priority ordering
│   ├── EventHandler.java     # Annotation for listener methods
│   └── events/               # TickEvent, PacketEvent, RenderEvent, etc.
├── setting/                  # Settings system (builder pattern)
│   └── impl/                 # Bool, Int, Double, Enum, String, Color, Keybind
├── gui/                      # Click GUI, HUD, notifications, themes
├── command/                  # Chat command system
├── config/                   # JSON config persistence
├── bot/                      # Mineflayer bot bridge (HTTP)
├── mixin/                    # Fabric mixins
└── util/                     # Rendering, player, inventory, color utilities
```

### Key Systems

- **Event Bus** - Reflection-based pub/sub with priority ordering. Modules subscribe via `@EventHandler` annotations on methods that accept a specific `Event` subclass.
- **Settings** - Builder-pattern configuration with JSON serialization. Types: `BoolSetting`, `IntSetting`, `DoubleSetting`, `EnumSetting`, `StringSetting`, `ColorSetting`, `KeybindSetting`. Supports conditional visibility via `visible()`.
- **Bot Bridge** - HTTP-based communication with external Mineflayer bot swarms. Managed by `BotBridgeManager` and `BotRuntimeManager` (embedded JS templates).
- **Config Persistence** - Automatic save/load of module states, keybinds, and settings via `ConfigManager`.
- **Click GUI** - Draggable, themed module panels with per-module setting editors. Theming support via `gui/theme/`.

## Development Notes

- The bot bridge runtime is generated/updated by `BotRuntimeManager` (embedded JS template + package metadata).
- If bot actions fail unexpectedly, verify local bridge health at `/health` and that the process on port `3099` matches generated bridge code.
- `run/` contains private or local-only data (accounts, proxies, history, logs) and is git-ignored.
- Wurst modules follow the same `Module` base class and event system as native modules.

## License

This repository includes a `LICENSE` file. See it for distribution and usage terms.
