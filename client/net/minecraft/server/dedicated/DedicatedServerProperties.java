package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class DedicatedServerProperties extends Settings<DedicatedServerProperties> {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String motd = this.get("motd", "A Minecraft Server");
   public final String bugReportLink = this.get("bug-report-link", "");
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
   public final String regionFileComression;
   public final boolean enableJmxMonitoring;
   public final boolean enableStatus;
   public final boolean hideOnlinePlayers;
   public final int entityBroadcastRangePercentage;
   public final String textFilteringConfig;
   public final int textFilteringVersion;
   public final Optional<MinecraftServer.ServerResourcePackInfo> serverResourcePackInfo;
   public final DataPackConfig initialDataPackConfiguration;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> playerIdleTimeout;
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> whiteList;
   public final boolean enforceSecureProfile;
   public final boolean logIPs;
   public final int pauseWhenEmptySeconds;
   private final WorldDimensionData worldDimensionData;
   public final WorldOptions worldOptions;
   public boolean acceptsTransfers;

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
      this.maxWorldSize = this.get("max-world-size", (var0) -> Mth.clamp(var0, 1, 29999984), 29999984);
      this.syncChunkWrites = this.get("sync-chunk-writes", true);
      this.regionFileComression = this.get("region-file-compression", "deflate");
      this.enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
      this.enableStatus = this.get("enable-status", true);
      this.hideOnlinePlayers = this.get("hide-online-players", false);
      this.entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (var0) -> Mth.clamp(var0, 10, 1000), 100);
      this.textFilteringConfig = this.get("text-filtering-config", "");
      this.textFilteringVersion = this.get("text-filtering-version", 0);
      this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
      this.whiteList = this.getMutable("white-list", false);
      this.enforceSecureProfile = this.get("enforce-secure-profile", true);
      this.logIPs = this.get("log-ips", true);
      this.pauseWhenEmptySeconds = this.get("pause-when-empty-seconds", 60);
      this.acceptsTransfers = this.get("accepts-transfers", false);
      String var2 = this.get("level-seed", "");
      boolean var3 = this.get("generate-structures", true);
      long var4 = WorldOptions.parseSeed(var2).orElse(WorldOptions.randomSeed());
      this.worldOptions = new WorldOptions(var4, var3, false);
      this.worldDimensionData = new WorldDimensionData((JsonObject)this.get("generator-settings", (var0) -> GsonHelper.parse(!var0.isEmpty() ? var0 : "{}"), new JsonObject()), (String)this.get("level-type", (var0) -> var0.toLowerCase(Locale.ROOT), WorldPresets.NORMAL.location().toString()));
      this.serverResourcePackInfo = getServerPackInfo(this.get("resource-pack-id", ""), this.get("resource-pack", ""), this.get("resource-pack-sha1", ""), this.getLegacyString("resource-pack-hash"), this.get("require-resource-pack", false), this.get("resource-pack-prompt", ""));
      this.initialDataPackConfiguration = getDatapackConfig(this.get("initial-enabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getEnabled())), this.get("initial-disabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getDisabled())));
   }

   public static DedicatedServerProperties fromFile(Path var0) {
      return new DedicatedServerProperties(loadFromFile(var0));
   }

   protected DedicatedServerProperties reload(RegistryAccess var1, Properties var2) {
      return new DedicatedServerProperties(var2);
   }

   @Nullable
   private static Component parseResourcePackPrompt(String var0) {
      if (!Strings.isNullOrEmpty(var0)) {
         try {
            return Component.Serializer.fromJson((String)var0, RegistryAccess.EMPTY);
         } catch (Exception var2) {
            LOGGER.warn("Failed to parse resource pack prompt '{}'", var0, var2);
         }
      }

      return null;
   }

   private static Optional<MinecraftServer.ServerResourcePackInfo> getServerPackInfo(String var0, String var1, String var2, @Nullable String var3, boolean var4, String var5) {
      if (var1.isEmpty()) {
         return Optional.empty();
      } else {
         String var6;
         if (!var2.isEmpty()) {
            var6 = var2;
            if (!Strings.isNullOrEmpty(var3)) {
               LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
         } else if (!Strings.isNullOrEmpty(var3)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            var6 = var3;
         } else {
            var6 = "";
         }

         if (var6.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
         } else if (!SHA1.matcher(var6).matches()) {
            LOGGER.warn("Invalid sha1 for resource-pack-sha1");
         }

         Component var7 = parseResourcePackPrompt(var5);
         UUID var8;
         if (var0.isEmpty()) {
            var8 = UUID.nameUUIDFromBytes(var1.getBytes(StandardCharsets.UTF_8));
            LOGGER.warn("resource-pack-id missing, using default of {}", var8);
         } else {
            try {
               var8 = UUID.fromString(var0);
            } catch (IllegalArgumentException var10) {
               LOGGER.warn("Failed to parse '{}' into UUID", var0);
               return Optional.empty();
            }
         }

         return Optional.of(new MinecraftServer.ServerResourcePackInfo(var8, var1, var6, var4, var7));
      }
   }

   private static DataPackConfig getDatapackConfig(String var0, String var1) {
      List var2 = COMMA_SPLITTER.splitToList(var0);
      List var3 = COMMA_SPLITTER.splitToList(var1);
      return new DataPackConfig(var2, var3);
   }

   public WorldDimensions createDimensions(HolderLookup.Provider var1) {
      return this.worldDimensionData.create(var1);
   }

   // $FF: synthetic method
   protected Settings reload(final RegistryAccess var1, final Properties var2) {
      return this.reload(var1, var2);
   }

   static record WorldDimensionData(JsonObject generatorSettings, String levelType) {
      private static final Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES;

      WorldDimensionData(JsonObject var1, String var2) {
         super();
         this.generatorSettings = var1;
         this.levelType = var2;
      }

      public WorldDimensions create(HolderLookup.Provider var1) {
         HolderLookup.RegistryLookup var2 = var1.lookupOrThrow(Registries.WORLD_PRESET);
         Holder.Reference var3 = (Holder.Reference)var2.get(WorldPresets.NORMAL).or(() -> var2.listElements().findAny()).orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
         Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(this.levelType)).map((var0) -> ResourceKey.create(Registries.WORLD_PRESET, var0)).or(() -> Optional.ofNullable((ResourceKey)LEGACY_PRESET_NAMES.get(this.levelType)));
         Objects.requireNonNull(var2);
         Holder var4 = (Holder)var10000.flatMap(var2::get).orElseGet(() -> {
            DedicatedServerProperties.LOGGER.warn("Failed to parse level-type {}, defaulting to {}", this.levelType, var3.key().location());
            return var3;
         });
         WorldDimensions var5 = ((WorldPreset)var4.value()).createWorldDimensions();
         if (var4.is(WorldPresets.FLAT)) {
            RegistryOps var6 = var1.createSerializationContext(JsonOps.INSTANCE);
            DataResult var8 = FlatLevelGeneratorSettings.CODEC.parse(new Dynamic(var6, this.generatorSettings()));
            Logger var10001 = DedicatedServerProperties.LOGGER;
            Objects.requireNonNull(var10001);
            Optional var7 = var8.resultOrPartial(var10001::error);
            if (var7.isPresent()) {
               return var5.replaceOverworldGenerator(var1, new FlatLevelSource((FlatLevelGeneratorSettings)var7.get()));
            }
         }

         return var5;
      }

      static {
         LEGACY_PRESET_NAMES = Map.of("default", WorldPresets.NORMAL, "largebiomes", WorldPresets.LARGE_BIOMES);
      }
   }
}
