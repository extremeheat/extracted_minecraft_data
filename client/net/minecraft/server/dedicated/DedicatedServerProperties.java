package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class DedicatedServerProperties extends Settings<DedicatedServerProperties> {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final boolean spawnAnimals = this.get("spawn-animals", true);
   public final boolean spawnNpcs = this.get("spawn-npcs", true);
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String motd = this.get("motd", "A Minecraft Server");
   public final boolean forceGameMode = this.get("force-gamemode", false);
   public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
   public final Difficulty difficulty;
   public final GameType gamemode;
   public final String levelName;
   public final int serverPort;
   @Nullable
   public final Boolean announcePlayerAchievements;
   public final boolean enableQuery;
   public final int queryPort;
   public final boolean enableRcon;
   public final int rconPort;
   public final String rconPassword;
   public final boolean hardcore;
   public final boolean allowNether;
   public final boolean spawnMonsters;
   public final boolean useNativeTransport;
   public final boolean enableCommandBlock;
   public final int spawnProtection;
   public final int opPermissionLevel;
   public final int functionPermissionLevel;
   public final long maxTickTime;
   public final int maxChainedNeighborUpdates;
   public final int rateLimitPacketsPerSecond;
   public final int viewDistance;
   public final int simulationDistance;
   public final int maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final boolean syncChunkWrites;
   public final boolean enableJmxMonitoring;
   public final boolean enableStatus;
   public final boolean hideOnlinePlayers;
   public final int entityBroadcastRangePercentage;
   public final String textFilteringConfig;
   public Optional<MinecraftServer.ServerResourcePackInfo> serverResourcePackInfo;
   public final boolean previewsChat;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> playerIdleTimeout;
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> whiteList;
   public final boolean enforceSecureProfile;
   private final WorldGenProperties worldGenProperties;
   @Nullable
   private WorldGenSettings worldGenSettings;

   public DedicatedServerProperties(Properties var1) {
      super(var1);
      this.difficulty = (Difficulty)this.get("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
      this.gamemode = (GameType)this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
      this.levelName = this.get("level-name", "world");
      this.serverPort = this.get("server-port", 25565);
      this.announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
      this.enableQuery = this.get("enable-query", false);
      this.queryPort = this.get("query.port", 25565);
      this.enableRcon = this.get("enable-rcon", false);
      this.rconPort = this.get("rcon.port", 25575);
      this.rconPassword = this.get("rcon.password", "");
      this.hardcore = this.get("hardcore", false);
      this.allowNether = this.get("allow-nether", true);
      this.spawnMonsters = this.get("spawn-monsters", true);
      this.useNativeTransport = this.get("use-native-transport", true);
      this.enableCommandBlock = this.get("enable-command-block", false);
      this.spawnProtection = this.get("spawn-protection", 16);
      this.opPermissionLevel = this.get("op-permission-level", 4);
      this.functionPermissionLevel = this.get("function-permission-level", 2);
      this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.maxChainedNeighborUpdates = this.get("max-chained-neighbor-updates", 1000000);
      this.rateLimitPacketsPerSecond = this.get("rate-limit", 0);
      this.viewDistance = this.get("view-distance", 10);
      this.simulationDistance = this.get("simulation-distance", 10);
      this.maxPlayers = this.get("max-players", 20);
      this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
      this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
      this.maxWorldSize = this.get("max-world-size", (var0) -> {
         return Mth.clamp((int)var0, (int)1, (int)29999984);
      }, 29999984);
      this.syncChunkWrites = this.get("sync-chunk-writes", true);
      this.enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
      this.enableStatus = this.get("enable-status", true);
      this.hideOnlinePlayers = this.get("hide-online-players", false);
      this.entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (var0) -> {
         return Mth.clamp((int)var0, (int)10, (int)1000);
      }, 100);
      this.textFilteringConfig = this.get("text-filtering-config", "");
      this.previewsChat = this.get("previews-chat", false);
      this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
      this.whiteList = this.getMutable("white-list", false);
      this.enforceSecureProfile = this.get("enforce-secure-profile", false);
      this.worldGenProperties = new WorldGenProperties(this.get("level-seed", ""), (JsonObject)this.get("generator-settings", (var0) -> {
         return GsonHelper.parse(!var0.isEmpty() ? var0 : "{}");
      }, new JsonObject()), this.get("generate-structures", true), (String)this.get("level-type", (var0) -> {
         return var0.toLowerCase(Locale.ROOT);
      }, WorldPresets.NORMAL.location().toString()));
      this.serverResourcePackInfo = getServerPackInfo(this.get("resource-pack", ""), this.get("resource-pack-sha1", ""), this.getLegacyString("resource-pack-hash"), this.get("require-resource-pack", false), this.get("resource-pack-prompt", ""));
   }

   public static DedicatedServerProperties fromFile(Path var0) {
      return new DedicatedServerProperties(loadFromFile(var0));
   }

   protected DedicatedServerProperties reload(RegistryAccess var1, Properties var2) {
      DedicatedServerProperties var3 = new DedicatedServerProperties(var2);
      var3.getWorldGenSettings(var1);
      return var3;
   }

   @Nullable
   private static Component parseResourcePackPrompt(String var0) {
      if (!Strings.isNullOrEmpty(var0)) {
         try {
            return Component.Serializer.fromJson(var0);
         } catch (Exception var2) {
            LOGGER.warn("Failed to parse resource pack prompt '{}'", var0, var2);
         }
      }

      return null;
   }

   private static Optional<MinecraftServer.ServerResourcePackInfo> getServerPackInfo(String var0, String var1, @Nullable String var2, boolean var3, String var4) {
      if (var0.isEmpty()) {
         return Optional.empty();
      } else {
         String var5;
         if (!var1.isEmpty()) {
            var5 = var1;
            if (!Strings.isNullOrEmpty(var2)) {
               LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
         } else if (!Strings.isNullOrEmpty(var2)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            var5 = var2;
         } else {
            var5 = "";
         }

         if (var5.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
         } else if (!SHA1.matcher(var5).matches()) {
            LOGGER.warn("Invalid sha1 for resource-pack-sha1");
         }

         Component var6 = parseResourcePackPrompt(var4);
         return Optional.of(new MinecraftServer.ServerResourcePackInfo(var0, var5, var3, var6));
      }
   }

   public WorldGenSettings getWorldGenSettings(RegistryAccess var1) {
      if (this.worldGenSettings == null) {
         this.worldGenSettings = this.worldGenProperties.create(var1);
      }

      return this.worldGenSettings;
   }

   // $FF: synthetic method
   protected Settings reload(RegistryAccess var1, Properties var2) {
      return this.reload(var1, var2);
   }

   public static record WorldGenProperties(String a, JsonObject b, boolean c, String d) {
      private final String levelSeed;
      private final JsonObject generatorSettings;
      private final boolean generateStructures;
      private final String levelType;
      private static final Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES;

      public WorldGenProperties(String var1, JsonObject var2, boolean var3, String var4) {
         super();
         this.levelSeed = var1;
         this.generatorSettings = var2;
         this.generateStructures = var3;
         this.levelType = var4;
      }

      public WorldGenSettings create(RegistryAccess var1) {
         long var2 = WorldGenSettings.parseSeed(this.levelSeed()).orElse(RandomSource.create().nextLong());
         Registry var4 = var1.registryOrThrow(Registry.WORLD_PRESET_REGISTRY);
         Holder var5 = (Holder)var4.getHolder(WorldPresets.NORMAL).or(() -> {
            return var4.holders().findAny();
         }).orElseThrow(() -> {
            return new IllegalStateException("Invalid datapack contents: can't find default preset");
         });
         Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(this.levelType)).map((var0) -> {
            return ResourceKey.create(Registry.WORLD_PRESET_REGISTRY, var0);
         }).or(() -> {
            return Optional.ofNullable((ResourceKey)LEGACY_PRESET_NAMES.get(this.levelType));
         });
         Objects.requireNonNull(var4);
         Holder var6 = (Holder)var10000.flatMap(var4::getHolder).orElseGet(() -> {
            DedicatedServerProperties.LOGGER.warn("Failed to parse level-type {}, defaulting to {}", this.levelType, var5.unwrapKey().map((var0) -> {
               return var0.location().toString();
            }).orElse("[unnamed]"));
            return var5;
         });
         WorldGenSettings var7 = ((WorldPreset)var6.value()).createWorldGenSettings(var2, this.generateStructures, false);
         if (var6.is(WorldPresets.FLAT)) {
            RegistryOps var8 = RegistryOps.create(JsonOps.INSTANCE, var1);
            DataResult var11 = FlatLevelGeneratorSettings.CODEC.parse(new Dynamic(var8, this.generatorSettings()));
            Logger var10001 = DedicatedServerProperties.LOGGER;
            Objects.requireNonNull(var10001);
            Optional var9 = var11.resultOrPartial(var10001::error);
            if (var9.isPresent()) {
               Registry var10 = var1.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
               return WorldGenSettings.replaceOverworldGenerator(var1, var7, new FlatLevelSource(var10, (FlatLevelGeneratorSettings)var9.get()));
            }
         }

         return var7;
      }

      public String levelSeed() {
         return this.levelSeed;
      }

      public JsonObject generatorSettings() {
         return this.generatorSettings;
      }

      public boolean generateStructures() {
         return this.generateStructures;
      }

      public String levelType() {
         return this.levelType;
      }

      static {
         LEGACY_PRESET_NAMES = Map.of("default", WorldPresets.NORMAL, "largebiomes", WorldPresets.LARGE_BIOMES);
      }
   }
}
