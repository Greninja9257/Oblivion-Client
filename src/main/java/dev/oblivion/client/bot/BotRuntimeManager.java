package dev.oblivion.client.bot;

import dev.oblivion.client.OblivionClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BotRuntimeManager {
    private static final String BRIDGE_VERSION = "6";
    private static final String HEALTH_URL = "http://127.0.0.1:3099/health";

    private static final String PACKAGE_JSON = """
        {
          "name": "oblivion-mineflayer-bridge",
          "version": "1.0.0",
          "private": true,
          "type": "commonjs",
          "dependencies": {
            "express": "^4.21.2",
            "mineflayer": "^4.35.0",
            "minecraft-data": "^3.86.0",
            "mineflayer-pathfinder": "^2.4.5",
            "mineflayer-collectblock": "^1.6.0",
            "mineflayer-pvp": "^1.3.2"
          }
        }
        """;

    private static final String BRIDGE_JS = """
        const express = require("express");
        const mineflayer = require("mineflayer");
        const { pathfinder, goals } = require("mineflayer-pathfinder");
        const collectBlockPlugin = require("mineflayer-collectblock").plugin;
        const pvpPlugin = require("mineflayer-pvp").plugin;
        const mcDataLoader = require("minecraft-data");
        
        const app = express();
        app.use(express.json());
        
        const TRANSIENT_CODES = new Set(["ECONNRESET", "ECONNREFUSED", "ETIMEDOUT", "EPIPE", "UND_ERR_SOCKET"]);
        const KNOWN_PARSE_SNIPPETS = [
          "PartialReadError",
          "Unexpected buffer end while reading VarInt",
          "Missing characters in string"
        ];
        
        process.on("uncaughtException", (err) => {
          const msg = String(err?.message || err || "");
          if (msg.includes("physicTick")) return;
          if (isKnownParseNoise(err)) return;
          console.error("bridge uncaughtException:", msg);
        });
        process.on("unhandledRejection", (err) => {
          const msg = String(err?.message || err || "");
          if (msg.includes("physicTick")) return;
          if (isKnownParseNoise(err)) return;
          console.error("bridge unhandledRejection:", msg);
        });
        
        const bots = new Map();
        let autoRegisterCfg = null;
        let pendingNames = [];
        
        function makeName(prefix = "obv_", len = 6) {
          const chars = "abcdefghijklmnopqrstuvwxyz0123456789";
          let s = prefix;
          for (let i = 0; i < len; i++) s += chars[Math.floor(Math.random() * chars.length)];
          return s;
        }
        
        function stateOf(bot) {
          if (!bot.__obvState) bot.__obvState = {};
          return bot.__obvState;
        }
        
        function clearIntervals(state) {
          const keys = ["followInterval", "collectInterval", "guardInterval", "farmInterval", "chatInterval"];
          for (const key of keys) {
            if (state[key]) {
              clearInterval(state[key]);
              state[key] = null;
            }
          }
          state.mineRunning = false;
          state.guardTarget = null;
          state.collecting = false;
          state.farming = false;
        }
        
        function cleanupBot(bot) {
          clearIntervals(stateOf(bot));
          if (bot?.username && bots.get(bot.username) === bot) {
            bots.delete(bot.username);
          }
        }
        
        function isKnownParseNoise(err) {
          const msg = String(err?.message || err || "");
          if (!msg) return false;
          return KNOWN_PARSE_SNIPPETS.some((snippet) => msg.includes(snippet));
        }
        
        function isTransientError(err) {
          const code = String(err?.code || "");
          if (TRANSIENT_CODES.has(code)) return true;
          return isKnownParseNoise(err);
        }
        
        function normalizeVersion(rawVersion) {
          if (rawVersion === false) return false;
          const value = String(rawVersion || "").trim();
        
          // Default to an explicit tested version to avoid pre-login ping timeouts.
          if (!value) return mineflayer.latestSupportedVersion;
        
          const lowered = value.toLowerCase();
          if (lowered === "auto") return false;
        
          const cleaned = value.replace(/^v/i, "");
          if (Array.isArray(mineflayer.testedVersions) && mineflayer.testedVersions.includes(cleaned)) {
            return cleaned;
          }
        
          console.warn(`bridge warning: untested Mineflayer version '${cleaned}', attempting anyway`);
          return cleaned;
        }
        
        function applyAutoRegister(bot) {
          bot.on("messagestr", (msg) => {
            if (!autoRegisterCfg) return;
            const line = String(msg || "").toLowerCase();
            const p = autoRegisterCfg.password;
            const reg = autoRegisterCfg.registerFormat.replaceAll("{p}", p);
            const log = autoRegisterCfg.loginFormat.replaceAll("{p}", p);
            if (line.includes("/register")) bot.chat(reg);
            if (line.includes("/login")) bot.chat(log);
          });
        }
        
        function wireBot(bot) {
          try { bot.loadPlugin(pathfinder); } catch {}
          try { bot.loadPlugin(collectBlockPlugin); } catch {}
          try { bot.loadPlugin(pvpPlugin); } catch {}
          applyAutoRegister(bot);
        
          let cleaned = false;
          const cleanupOnce = () => {
            if (cleaned) return;
            cleaned = true;
            cleanupBot(bot);
          };
        
          bot.once("end", cleanupOnce);
          bot.once("kicked", cleanupOnce);
        
          bot.on("error", (err) => {
            if (!isTransientError(err)) {
              const msg = String(err?.message || err || "unknown error");
              console.error("bot error:", bot.username || "unknown", msg);
            }
            cleanupOnce();
          });
        
          if (bot._client && typeof bot._client.on === "function") {
            bot._client.on("error", (err) => {
              if (isTransientError(err)) return;
              const msg = String(err?.message || err || "unknown client error");
              console.error("bot client error:", bot.username || "unknown", msg);
            });
          }
        }
        
        function selectedBots(count = 1) {
          const all = Array.from(bots.values());
          return all.slice(0, Math.max(1, Number(count || 1)));
        }
        
        async function runMineTask(bot, blockId, maxBlocks) {
          const state = stateOf(bot);
          state.mineRunning = true;
          let mined = 0;
        
          while (state.mineRunning && bot.entity && mined < maxBlocks) {
            const mcData = mcDataLoader(bot.version);
            const blockInfo = mcData.blocksByName[(blockId || "").replace("minecraft:", "")];
            if (!blockInfo) break;
        
            const target = bot.findBlock({
              matching: (b) => b.type === blockInfo.id,
              maxDistance: 24
            });
            if (!target) break;
        
            try {
              if (bot.pathfinder && goals && goals.GoalNear) {
                bot.pathfinder.setGoal(new goals.GoalNear(target.position.x, target.position.y, target.position.z, 1));
                await new Promise((r) => setTimeout(r, 600));
              }
              if (bot.canDigBlock(target)) {
                await bot.dig(target);
                mined++;
              } else {
                break;
              }
            } catch {
              break;
            }
          }
          state.mineRunning = false;
        }
        
        function startFollow(bot, targetName, distance) {
          const state = stateOf(bot);
          if (state.followInterval) clearInterval(state.followInterval);
          state.followInterval = setInterval(() => {
            try {
              if (!bot || !bot.entity || !bot.pathfinder || !goals || !goals.GoalFollow) return;
              const players = bot.players && typeof bot.players === "object" ? bot.players : {};
              const target = players[targetName]?.entity;
              if (!target) return;
              bot.pathfinder.setGoal(new goals.GoalFollow(target, distance), true);
            } catch {}
          }, 500);
        }
        
        function startCollect(bot, radius) {
          const state = stateOf(bot);
          state.collecting = true;
          if (state.collectInterval) clearInterval(state.collectInterval);
          state.collectInterval = setInterval(async () => {
            if (!state.collecting || !bot.entity) return;
            const items = Object.values(bot.entities).filter(
              (e) => e && e.name === "item" && bot.entity.position.distanceTo(e.position) <= radius
            );
            if (!items.length) return;
            const closest = items.sort((a, b) => bot.entity.position.distanceTo(a.position) - bot.entity.position.distanceTo(b.position))[0];
            if (bot.pathfinder && goals && goals.GoalNear) {
              bot.pathfinder.setGoal(new goals.GoalNear(closest.position.x, closest.position.y, closest.position.z, 1));
            }
          }, 350);
        }
        
        function startGuard(bot, targetName) {
          const state = stateOf(bot);
          state.guardTarget = targetName;
          if (state.guardInterval) clearInterval(state.guardInterval);
          state.guardInterval = setInterval(() => {
            try {
              if (!bot || !bot.entity) return;
              const players = bot.players && typeof bot.players === "object" ? bot.players : {};
              const target = players[targetName]?.entity;
              if (!target) return;
        
              const nearestEnemy = Object.values(bot.entities)
                .filter((e) => e && e.type === "mob" && e.position.distanceTo(target.position) <= 8)
                .sort((a, b) => eDist(bot, a) - eDist(bot, b))[0];
        
              if (nearestEnemy) {
                if (bot.pvp) bot.pvp.attack(nearestEnemy);
                else if (bot.pathfinder && goals && goals.GoalNear) {
                  bot.pathfinder.setGoal(new goals.GoalNear(nearestEnemy.position.x, nearestEnemy.position.y, nearestEnemy.position.z, 1));
                }
              } else if (bot.pathfinder && goals && goals.GoalFollow) {
                bot.pathfinder.setGoal(new goals.GoalFollow(target, 2), true);
              }
            } catch {}
          }, 450);
        }
        
        function eDist(bot, e) {
          return bot.entity ? bot.entity.position.distanceTo(e.position) : 9999;
        }
        
        function startFarm(bot, cropName) {
          const state = stateOf(bot);
          state.farming = true;
          if (state.farmInterval) clearInterval(state.farmInterval);
          state.farmInterval = setInterval(async () => {
            if (!state.farming || !bot.entity) return;
            const crop = String(cropName || "wheat").toLowerCase();
            const target = bot.findBlock({
              matching: (b) => {
                const n = (b.name || "").toLowerCase();
                return n.includes(crop) || (crop === "wheat" && n.includes("wheat"));
              },
              maxDistance: 16
            });
            if (!target) return;
        
            try {
              if (bot.pathfinder && goals && goals.GoalNear) {
                bot.pathfinder.setGoal(new goals.GoalNear(target.position.x, target.position.y, target.position.z, 1));
                await new Promise((r) => setTimeout(r, 450));
              }
              if (bot.canDigBlock(target)) await bot.dig(target);
            } catch {}
          }, 700);
        }
        
        function startChatSpam(bot, message, delayMs) {
          const state = stateOf(bot);
          if (state.chatInterval) clearInterval(state.chatInterval);
          const delay = Math.max(250, Number(delayMs || 1000));
          state.chatInterval = setInterval(() => {
            try {
              if (!bot || !bot.entity || typeof bot.chat !== "function") return;
              bot.chat(String(message || "").slice(0, 256));
            } catch {}
          }, delay);
        }
        
        async function deposit(bot, x, y, z) {
          if (!bot.pathfinder || !goals || !goals.GoalNear) return;
          bot.pathfinder.setGoal(new goals.GoalNear(x, y, z, 1));
          await new Promise((r) => setTimeout(r, 1200));
        
          const block = bot.blockAt({ x, y, z });
          if (!block || !String(block.name).includes("chest")) return;
        
          try {
            const chest = await bot.openChest(block);
            const items = bot.inventory.items();
            for (const item of items) {
              await chest.deposit(item.type, null, item.count);
            }
            chest.close();
          } catch {}
        }
        
        app.get("/health", (_req, res) => {
          res.json({ ok: true, bots: bots.size, autoRegister: !!autoRegisterCfg });
        });
        
        app.post("/api/bots/command", async (req, res) => {
          const body = req.body || {};
          const action = body.action;
          const count = Number(body.count || 1);
          const randomNames = !!body.randomNames;
          const namePrefix = body.namePrefix || "obv_";
          const randomLength = Number(body.randomLength || 6);
          const selected = selectedBots(count);
        
          try {
            if (action === "spawn_swarm") {
              const host = body.serverHost || "127.0.0.1";
              const port = Number(body.serverPort || 25565);
              const version = normalizeVersion(body.version);
              const fixedName = body.fixedName || "obv_bot";
              const spawned = [];
              const failed = [];
        
              let names = Array.isArray(body.names) ? body.names.slice() : [];
              if (!names.length && pendingNames.length) {
                names = pendingNames.slice();
                pendingNames = [];
              }
        
              for (let i = 0; i < count; i++) {
                const username = names[i]
                  || (randomNames ? makeName(namePrefix, randomLength) : `${fixedName}${i + 1}`);
                if (bots.has(username)) continue;
        
                try {
                  const bot = mineflayer.createBot({
                    host,
                    port,
                    username,
                    version,
                    hideErrors: true,
                    checkTimeoutInterval: 15_000
                  });
                  bots.set(username, bot);
                  wireBot(bot);
                  spawned.push(username);
                } catch (error) {
                  failed.push({ username, error: String(error?.message || error) });
                }
              }
        
              return res.json({ ok: true, action, bots: bots.size, spawned, failed });
            }
        
            if (action === "disconnect_all") {
              let disconnected = 0;
              for (const bot of bots.values()) {
                try {
                  clearIntervals(stateOf(bot));
                  if (typeof bot.quit === "function") bot.quit("disconnect_all");
                  disconnected++;
                } catch {}
              }
              bots.clear();
              return res.json({ ok: true, action, disconnected });
            }
        
            if (action === "set_names") {
              pendingNames = Array.isArray(body.names) ? body.names.slice(0, 500) : [];
              return res.json({ ok: true, action, queued: pendingNames.length });
            }
        
            if (action === "auto_register") {
              autoRegisterCfg = body.enabled ? {
                password: body.password || "oblivion123",
                registerFormat: body.registerFormat || "/register {p} {p}",
                loginFormat: body.loginFormat || "/login {p}"
              } : null;
              return res.json({ ok: true, action, enabled: !!autoRegisterCfg });
            }
        
            if (action === "follow_start") {
              for (const bot of selected) startFollow(bot, body.target || "player", Number(body.distance || 2.5));
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "follow_stop") {
              for (const bot of selected) {
                const state = stateOf(bot);
                if (state.followInterval) clearInterval(state.followInterval);
                state.followInterval = null;
                if (bot.pathfinder) bot.pathfinder.setGoal(null);
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "mine_start") {
              for (const bot of selected) runMineTask(bot, body.block || "minecraft:stone", Number(body.maxBlocks || 64));
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "mine_stop") {
              for (const bot of selected) stateOf(bot).mineRunning = false;
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "farm_start") {
              for (const bot of selected) startFarm(bot, body.crop || "wheat");
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "farm_stop") {
              for (const bot of selected) {
                const state = stateOf(bot);
                state.farming = false;
                if (state.farmInterval) clearInterval(state.farmInterval);
                state.farmInterval = null;
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "guard_start") {
              for (const bot of selected) startGuard(bot, body.target || "player");
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "guard_stop") {
              for (const bot of selected) {
                const state = stateOf(bot);
                if (state.guardInterval) clearInterval(state.guardInterval);
                state.guardInterval = null;
                if (bot.pvp) bot.pvp.stop();
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "collect_start") {
              for (const bot of selected) startCollect(bot, Number(body.radius || 10));
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "collect_stop") {
              for (const bot of selected) {
                const state = stateOf(bot);
                state.collecting = false;
                if (state.collectInterval) clearInterval(state.collectInterval);
                state.collectInterval = null;
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "deposit") {
              const x = Number(body.x || 0);
              const y = Number(body.y || 64);
              const z = Number(body.z || 0);
              for (const bot of selected) deposit(bot, x, y, z);
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "stop_tasks") {
              for (const bot of selected) {
                const state = stateOf(bot);
                clearIntervals(state);
                if (bot.pathfinder) bot.pathfinder.setGoal(null);
                if (bot.pvp) bot.pvp.stop();
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "chat_send") {
              const message = String(body.message || "").trim();
              if (!message) return res.status(400).json({ ok: false, error: "message is required" });
              if (!selected.length) return res.status(409).json({ ok: false, error: "no bots connected" });
              for (const bot of selected) {
                try {
                  if (bot && typeof bot.chat === "function") {
                    bot.chat(message.slice(0, 256));
                  }
                } catch {}
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "chat_spam_start") {
              const message = String(body.message || "").trim();
              if (!message) return res.status(400).json({ ok: false, error: "message is required" });
              if (!selected.length) return res.status(409).json({ ok: false, error: "no bots connected" });
              const delayMs = Math.max(250, Number(body.delayMs || 1000));
              for (const bot of selected) {
                startChatSpam(bot, message, delayMs);
              }
              return res.json({ ok: true, action, bots: selected.length, delayMs });
            }
        
            if (action === "chat_spam_stop") {
              for (const bot of selected) {
                const state = stateOf(bot);
                if (state.chatInterval) clearInterval(state.chatInterval);
                state.chatInterval = null;
              }
              return res.json({ ok: true, action, bots: selected.length });
            }
        
            if (action === "status") {
              return res.json({
                ok: true,
                action,
                bots: Array.from(bots.keys()),
                count: bots.size,
                autoRegister: !!autoRegisterCfg
              });
            }
        
            return res.status(400).json({ ok: false, error: `Unknown action: ${action}` });
          } catch (error) {
            return res.status(500).json({ ok: false, error: String(error?.message || error) });
          }
        });
        
        app.listen(3099, "127.0.0.1", () => {
          console.log("Oblivion Mineflayer bridge listening on http://127.0.0.1:3099");
        });
        """;

    private final Path bridgeDir = Path.of("run", "run", "oblivion-client", "mineflayer-bridge");
    private final Path packageFile = bridgeDir.resolve("package.json");
    private final Path bridgeFile = bridgeDir.resolve("bridge.js");
    private final Path versionFile = bridgeDir.resolve(".bridge-version");
    private final Path logFile = bridgeDir.resolve("bridge.log");

    private final HttpClient healthHttp = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(2))
        .build();

    private Process bridgeProcess;

    public synchronized void start() {
        ensureReady();
    }

    public synchronized void ensureReady() {
        try {
            Files.createDirectories(bridgeDir);
            boolean updated = writeBridgeFilesIfNeeded();
            boolean healthy = isHealthy();

            if (healthy && !updated) {
                return;
            }

            if (!hasExecutable("node") || !hasExecutable("npm")) {
                OblivionClient.LOGGER.error("Bots runtime unavailable: install Node.js (node + npm) to use bot modules.");
                return;
            }

            if (healthy && updated) {
                if (bridgeProcess != null && bridgeProcess.isAlive()) {
                    OblivionClient.LOGGER.info("Bridge template updated to v{}, restarting managed bridge...", BRIDGE_VERSION);
                    shutdown();
                } else {
                    // A healthy bridge is running, but not managed by this runtime instance.
                    OblivionClient.LOGGER.warn("Bridge template updated to v{}, but external bridge is active. Restart Minecraft/bridge to apply updates.", BRIDGE_VERSION);
                    return;
                }
            }

            installDependenciesIfNeeded(updated);
            startBridgeProcess();
            waitForHealthy();
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to start Mineflayer bridge runtime", e);
        }
    }

    public synchronized void shutdown() {
        if (bridgeProcess == null) {
            return;
        }

        try {
            bridgeProcess.destroy();
            if (!bridgeProcess.waitFor(3, TimeUnit.SECONDS)) {
                bridgeProcess.destroyForcibly();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            bridgeProcess = null;
        }
    }

    public synchronized boolean isHealthy() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(HEALTH_URL))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
            HttpResponse<String> res = healthHttp.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return res.statusCode() >= 200 && res.statusCode() < 300;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean writeBridgeFilesIfNeeded() throws IOException {
        boolean outdated = true;
        if (Files.exists(versionFile)) {
            String current = Files.readString(versionFile, StandardCharsets.UTF_8).trim();
            outdated = !BRIDGE_VERSION.equals(current);
        }

        if (outdated || !Files.exists(packageFile)) {
            Files.writeString(packageFile, PACKAGE_JSON, StandardCharsets.UTF_8);
        }
        if (outdated || !Files.exists(bridgeFile)) {
            Files.writeString(bridgeFile, BRIDGE_JS, StandardCharsets.UTF_8);
        }
        if (outdated || !Files.exists(versionFile)) {
            Files.writeString(versionFile, BRIDGE_VERSION, StandardCharsets.UTF_8);
        }
        return outdated;
    }

    private void installDependenciesIfNeeded(boolean forceInstall) throws IOException, InterruptedException {
        Path nodeModules = bridgeDir.resolve("node_modules");
        if (!forceInstall && Files.exists(nodeModules) && Files.isDirectory(nodeModules)) {
            return;
        }

        OblivionClient.LOGGER.info("Installing Mineflayer bridge dependencies...");
        ProcessBuilder pb = new ProcessBuilder("npm", "install", "--no-audit", "--no-fund");
        pb.directory(bridgeDir.toFile());
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
        Process p = pb.start();
        if (!p.waitFor(90, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            throw new IOException("npm install timed out");
        }
        if (p.exitValue() != 0) {
            throw new IOException("npm install failed with exit code " + p.exitValue());
        }
    }

    private void startBridgeProcess() throws IOException {
        if (bridgeProcess != null && bridgeProcess.isAlive()) {
            return;
        }

        ProcessBuilder pb = new ProcessBuilder("node", "bridge.js");
        pb.directory(bridgeDir.toFile());
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
        bridgeProcess = pb.start();
    }

    private void waitForHealthy() throws InterruptedException, IOException {
        for (int i = 0; i < 25; i++) {
            if (isHealthy()) {
                return;
            }
            if (bridgeProcess != null && !bridgeProcess.isAlive()) {
                throw new IOException("Bridge process exited unexpectedly");
            }
            Thread.sleep(300);
        }
        throw new IOException("Bridge did not become healthy in time");
    }

    private boolean hasExecutable(String executable) {
        try {
            ProcessBuilder pb = new ProcessBuilder(executable, "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader ignored = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                // Consume stream to avoid process blocking on some systems.
            }
            if (!process.waitFor(4, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return false;
            }
            return process.exitValue() == 0;
        } catch (Exception e) {
            OblivionClient.LOGGER.debug("Executable {} is unavailable on {}", executable, System.getProperty("os.name").toLowerCase(Locale.ROOT));
            return false;
        }
    }
}
