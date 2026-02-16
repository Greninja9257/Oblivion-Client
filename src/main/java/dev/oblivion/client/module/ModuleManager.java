package dev.oblivion.client.module;

import dev.oblivion.client.module.combat.*;
import dev.oblivion.client.module.movement.*;
import dev.oblivion.client.module.render.*;
import dev.oblivion.client.module.player.*;
import dev.oblivion.client.module.world.*;
import dev.oblivion.client.module.misc.*;
import dev.oblivion.client.module.bots.*;
import dev.oblivion.client.module.exploit.*;
import dev.oblivion.client.module.wurst.WurstModulesRegistrar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<? extends Module>, Module> byClass = new HashMap<>();
    private final Map<String, Module> byName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void init() {
        // Combat
        register(new KillAura());
        register(new CrystalAura());
        register(new Aimbot());
        register(new AutoTotem());
        register(new Criticals());
        register(new Reach());
        register(new Velocity());
        register(new AutoArmor());
        register(new AnchorAura());
        register(new AutoWeapon());
        register(new BedAura());
        register(new Offhand());
        register(new Trigger());

        // Movement
        register(new Fly());
        register(new Speed());
        register(new Sprint());
        register(new NoFall());
        register(new Step());
        register(new Jesus());
        register(new ElytraFly());
        register(new BoatFly());
        register(new Spider());
        register(new Scaffold());
        register(new AirJump());
        register(new AntiAFK());
        register(new AutoWalk());
        register(new Blink());
        register(new EntitySpeed());
        register(new FastClimb());
        register(new HighJump());
        register(new LongJump());
        register(new SafeWalk());
        register(new Sneak());
        register(new ReverseStep());

        // Render
        register(new ESP());
        register(new Tracers());
        register(new Fullbright());
        register(new Nametags());
        register(new NoRender());
        register(new Xray());
        register(new ChestESP());
        register(new Freecam());
        register(new Breadcrumbs());
        register(new Chams());
        register(new FreeLook());
        register(new HoleESP());
        register(new ItemHighlight());
        register(new LightOverlay());
        register(new LogoutSpots());
        register(new Trajectories());
        register(new VoidESP());
        register(new Zoom());

        // Player
        register(new AutoEat());
        register(new AutoSoup());
        register(new AutoTool());
        register(new FastPlace());
        register(new FastBreak());
        register(new NoSlowdown());
        register(new AntiHunger());
        register(new CreativeMode());
        register(new InventorySort());
        register(new AutoFish());
        register(new AutoReplenish());
        register(new ChestSwap());
        register(new DeathPosition());
        register(new GhostHand());
        register(new NoRotate());
        register(new PacketMine());
        register(new Portals());
        register(new PotionSaver());

        // World
        register(new Nuker());
        register(new AutoBuild());
        register(new Timer());
        register(new ChunkLoader());
        register(new EndermanLook());
        register(new LiquidFiller());
        register(new SpawnProofer());
        register(new StashFinder());
        register(new VeinMiner());

        // Misc
        register(new AutoDisconnect());
        register(new FakePlayer());
        register(new Spammer());
        register(new AutoReconnect());
        register(new MiddleClickFriend());
        register(new AntiPacketKick());
        register(new AutoClicker());
        register(new AutoLog());
        register(new BetterChat());
        register(new NameProtect());
        register(new PacketCanceller());
        register(new ServerSpoof());
        register(new SoundBlocker());

        // Bots
        register(new BotSpawnSwarm());
        register(new BotDisconnectAll());
        register(new BotAutoRegister());
        register(new BotFollow());
        register(new BotMine());
        register(new BotFarm());
        register(new BotGuard());
        register(new BotCollectDrops());
        register(new BotDeposit());
        register(new BotSendChat());
        register(new BotRawCommand());
        register(new BotRandomizeNames());
        register(new BotStopTasks());

        // Exploit
        register(new ServerCrasher());
        register(new PacketCrasher());
        register(new PacketFlood());
        register(new BookExploit());
        register(new BookBan());
        register(new SignCrash());
        register(new ChunkCrash());
        register(new ChunkOverload());
        register(new EntitySpam());
        register(new ArmorStandLag());
        register(new BoatLag());
        register(new MobSpam());
        register(new NBTCrash());
        register(new TabCompleteCrash());
        register(new CommandSpam());
        register(new InventoryCrash());
        register(new WindowClickSpam());
        register(new CreativeNBTExploit());
        register(new ItemFrameLag());
        register(new MapDataCrash());
        register(new PayloadCrash());
        register(new PluginMessageCrash());
        register(new TransactionSpam());
        register(new MovementSpam());
        register(new BlockPlacementSpam());
        register(new ShulkerBoxCrash());
        register(new BookOP());
        register(new AntiCrashItem());

        // Individually registered Wurst-compatible modules.
        WurstModulesRegistrar.registerAll(this);
    }

    public void register(Module module) {
        modules.add(module);
        byClass.put(module.getClass(), module);
        byName.put(module.name, module);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) byClass.get(clazz);
    }

    public Module get(String name) {
        return byName.get(name);
    }

    public List<Module> getAll() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getByCategory(Category category) {
        return modules.stream()
            .filter(m -> m.category == category)
            .collect(Collectors.toList());
    }

    public List<Module> getEnabled() {
        return modules.stream()
            .filter(Module::isEnabled)
            .collect(Collectors.toList());
    }

    public void onKeyPress(int key) {
        if (key <= 0) return;
        for (Module module : modules) {
            if (module.getKeybind() == key) {
                module.toggle();
            }
        }
    }
}
