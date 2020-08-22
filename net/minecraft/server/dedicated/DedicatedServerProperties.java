package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;

public class DedicatedServerProperties extends Settings {
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final boolean spawnAnimals = this.get("spawn-animals", true);
   public final boolean spawnNpcs = this.get("spawn-npcs", true);
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String resourcePack = this.get("resource-pack", "");
   public final String motd = this.get("motd", "A Minecraft Server");
   public final boolean forceGameMode = this.get("force-gamemode", false);
   public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
   public final boolean generateStructures = this.get("generate-structures", true);
   public final Difficulty difficulty;
   public final GameType gamemode;
   public final String levelName;
   public final String levelSeed;
   public final LevelType levelType;
   public final String generatorSettings;
   public final int serverPort;
   public final int maxBuildHeight;
   public final Boolean announcePlayerAchievements;
   public final boolean enableQuery;
   public final int queryPort;
   public final boolean enableRcon;
   public final int rconPort;
   public final String rconPassword;
   public final String resourcePackHash;
   public final String resourcePackSha1;
   public final boolean hardcore;
   public final boolean allowNether;
   public final boolean spawnMonsters;
   public final boolean snooperEnabled;
   public final boolean useNativeTransport;
   public final boolean enableCommandBlock;
   public final int spawnProtection;
   public final int opPermissionLevel;
   public final int functionPermissionLevel;
   public final long maxTickTime;
   public final int viewDistance;
   public final int maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final Settings.MutableValue playerIdleTimeout;
   public final Settings.MutableValue whiteList;

   public DedicatedServerProperties(Properties var1) {
      super(var1);
      this.difficulty = (Difficulty)this.get("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
      this.gamemode = (GameType)this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
      this.levelName = this.get("level-name", "world");
      this.levelSeed = this.get("level-seed", "");
      this.levelType = (LevelType)this.get("level-type", LevelType::getLevelType, LevelType::getName, LevelType.NORMAL);
      this.generatorSettings = this.get("generator-settings", "");
      this.serverPort = this.get("server-port", 25565);
      this.maxBuildHeight = this.get("max-build-height", (var0) -> {
         return Mth.clamp((var0 + 8) / 16 * 16, 64, 256);
      }, 256);
      this.announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
      this.enableQuery = this.get("enable-query", false);
      this.queryPort = this.get("query.port", 25565);
      this.enableRcon = this.get("enable-rcon", false);
      this.rconPort = this.get("rcon.port", 25575);
      this.rconPassword = this.get("rcon.password", "");
      this.resourcePackHash = this.getLegacyString("resource-pack-hash");
      this.resourcePackSha1 = this.get("resource-pack-sha1", "");
      this.hardcore = this.get("hardcore", false);
      this.allowNether = this.get("allow-nether", true);
      this.spawnMonsters = this.get("spawn-monsters", true);
      if (this.get("snooper-enabled", true)) {
      }

      this.snooperEnabled = false;
      this.useNativeTransport = this.get("use-native-transport", true);
      this.enableCommandBlock = this.get("enable-command-block", false);
      this.spawnProtection = this.get("spawn-protection", 16);
      this.opPermissionLevel = this.get("op-permission-level", 4);
      this.functionPermissionLevel = this.get("function-permission-level", 2);
      this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.viewDistance = this.get("view-distance", 10);
      this.maxPlayers = this.get("max-players", 20);
      this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
      this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
      this.maxWorldSize = this.get("max-world-size", (var0) -> {
         return Mth.clamp(var0, 1, 29999984);
      }, 29999984);
      this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
      this.whiteList = this.getMutable("white-list", false);
   }

   public static DedicatedServerProperties fromFile(Path var0) {
      return new DedicatedServerProperties(loadFromFile(var0));
   }

   protected DedicatedServerProperties reload(Properties var1) {
      return new DedicatedServerProperties(var1);
   }

   // $FF: synthetic method
   protected Settings reload(Properties var1) {
      return this.reload(var1);
   }
}
