package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.security.SecurityConfig;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.StrictJsonParser;
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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DedicatedServerProperties extends Settings<DedicatedServerProperties> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();
   public static final String MANAGEMENT_SERVER_TLS_ENABLED_KEY = "management-server-tls-enabled";
   public static final String MANAGEMENT_SERVER_TLS_KEYSTORE_KEY = "management-server-tls-keystore";
   public static final String MANAGEMENT_SERVER_TLS_KEYSTORE_PASSWORD_KEY = "management-server-tls-keystore-password";
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> allowFlight = this.getMutable("allow-flight", false);
   public final Settings<DedicatedServerProperties>.MutableValue<String> motd = this.getMutable("motd", "A Minecraft Server");
   public final boolean codeOfConduct = this.get("enable-code-of-conduct", false);
   public final String bugReportLink = this.get("bug-report-link", "");
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> forceGameMode = this.getMutable("force-gamemode", false);
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> enforceWhitelist = this.getMutable("enforce-whitelist", false);
   public final Settings<DedicatedServerProperties>.MutableValue<Difficulty> difficulty;
   public final Settings<DedicatedServerProperties>.MutableValue<GameType> gameMode;
   public final String levelName;
   public final int serverPort;
   public final boolean managementServerEnabled;
   public final String managementServerHost;
   public final int managementServerPort;
   public final String managementServerSecret;
   public final boolean managementServerTlsEnabled;
   public final String managementServerTlsKeystore;
   public final String managementServerTlsKeystorePassword;
   public final String managementServerAllowedOrigins;
   public final @Nullable Boolean announcePlayerAchievements;
   public final boolean enableQuery;
   public final int queryPort;
   public final boolean enableRcon;
   public final int rconPort;
   public final String rconPassword;
   public final boolean hardcore;
   public final boolean useNativeTransport;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> spawnProtection;
   public final Settings<DedicatedServerProperties>.MutableValue<LevelBasedPermissionSet> opPermissions;
   public final LevelBasedPermissionSet functionPermissions;
   public final long maxTickTime;
   public final int maxChainedNeighborUpdates;
   public final int rateLimitPacketsPerSecond;
   public final int commandSpamThresholdSeconds;
   public final int chatSpamThresholdSeconds;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> viewDistance;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> simulationDistance;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final boolean syncChunkWrites;
   public final String regionFileComression;
   public final boolean enableJmxMonitoring;
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> enableStatus;
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> hideOnlinePlayers;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> entityBroadcastRangePercentage;
   public final String textFilteringConfig;
   public final int textFilteringVersion;
   public final Optional<MinecraftServer.ServerResourcePackInfo> serverResourcePackInfo;
   public final DataPackConfig initialDataPackConfiguration;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> playerIdleTimeout;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> statusHeartbeatInterval;
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> whiteList;
   public final boolean enforceSecureProfile;
   public final boolean logIPs;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> pauseWhenEmptySeconds;
   private final WorldDimensionData worldDimensionData;
   public final WorldOptions worldOptions;
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> acceptsTransfers;

   public DedicatedServerProperties(final Properties settings) {
      super(settings);
      this.difficulty = this.getMutable("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getSerializedName, Difficulty.EASY);
      this.gameMode = this.getMutable("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
      this.levelName = this.get("level-name", "world");
      this.serverPort = this.get("server-port", 25565);
      this.managementServerEnabled = this.get("management-server-enabled", false);
      this.managementServerHost = this.get("management-server-host", "localhost");
      this.managementServerPort = this.get("management-server-port", 0);
      this.managementServerSecret = this.get("management-server-secret", SecurityConfig.generateSecretKey());
      this.managementServerTlsEnabled = this.get("management-server-tls-enabled", true);
      this.managementServerTlsKeystore = this.get("management-server-tls-keystore", "");
      this.managementServerTlsKeystorePassword = this.get("management-server-tls-keystore-password", "");
      this.managementServerAllowedOrigins = this.get("management-server-allowed-origins", "");
      this.announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
      this.enableQuery = this.get("enable-query", false);
      this.queryPort = this.get("query.port", 25565);
      this.enableRcon = this.get("enable-rcon", false);
      this.rconPort = this.get("rcon.port", 25575);
      this.rconPassword = this.get("rcon.password", "");
      this.hardcore = this.get("hardcore", false);
      this.useNativeTransport = this.get("use-native-transport", true);
      this.spawnProtection = this.getMutable("spawn-protection", 16);
      this.opPermissions = this.getMutable("op-permission-level", DedicatedServerProperties::deserializePermission, DedicatedServerProperties::serializePermission, LevelBasedPermissionSet.OWNER);
      this.functionPermissions = (LevelBasedPermissionSet)this.get("function-permission-level", DedicatedServerProperties::deserializePermission, DedicatedServerProperties::serializePermission, LevelBasedPermissionSet.GAMEMASTER);
      this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.maxChainedNeighborUpdates = this.get("max-chained-neighbor-updates", 1000000);
      this.rateLimitPacketsPerSecond = this.get("rate-limit", 0);
      this.commandSpamThresholdSeconds = this.get("command-spam-threshold-seconds", 10);
      this.chatSpamThresholdSeconds = this.get("chat-spam-threshold-seconds", 10);
      this.viewDistance = this.getMutable("view-distance", 10);
      this.simulationDistance = this.getMutable("simulation-distance", 10);
      this.maxPlayers = this.getMutable("max-players", 20);
      this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
      this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
      this.maxWorldSize = this.get("max-world-size", (v) -> Mth.clamp(v, 1, 29999984), 29999984);
      this.syncChunkWrites = this.get("sync-chunk-writes", true);
      this.regionFileComression = this.get("region-file-compression", "deflate");
      this.enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
      this.enableStatus = this.getMutable("enable-status", true);
      this.hideOnlinePlayers = this.getMutable("hide-online-players", false);
      this.entityBroadcastRangePercentage = this.getMutable("entity-broadcast-range-percentage", (v) -> Mth.clamp(Integer.parseInt(v), 10, 1000), 100);
      this.textFilteringConfig = this.get("text-filtering-config", "");
      this.textFilteringVersion = this.get("text-filtering-version", 0);
      this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
      this.statusHeartbeatInterval = this.getMutable("status-heartbeat-interval", 0);
      this.whiteList = this.getMutable("white-list", true);
      this.enforceSecureProfile = this.get("enforce-secure-profile", true);
      this.logIPs = this.get("log-ips", true);
      this.pauseWhenEmptySeconds = this.getMutable("pause-when-empty-seconds", 60);
      this.acceptsTransfers = this.getMutable("accepts-transfers", false);
      String levelSeed = this.get("level-seed", "");
      boolean generateStructures = this.get("generate-structures", true);
      long seed = WorldOptions.parseSeed(levelSeed).orElse(WorldOptions.randomSeed());
      this.worldOptions = new WorldOptions(seed, generateStructures, false);
      this.worldDimensionData = new WorldDimensionData((JsonObject)this.get("generator-settings", (s) -> GsonHelper.parse(!s.isEmpty() ? s : "{}"), new JsonObject()), (String)this.get("level-type", (v) -> v.toLowerCase(Locale.ROOT), WorldPresets.NORMAL.identifier().toString()));
      this.serverResourcePackInfo = getServerPackInfo(this.get("resource-pack-id", ""), this.get("resource-pack", ""), this.get("resource-pack-sha1", ""), this.getLegacyString("resource-pack-hash"), this.get("require-resource-pack", false), this.get("resource-pack-prompt", ""));
      this.initialDataPackConfiguration = getDatapackConfig(this.get("initial-enabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getEnabled())), this.get("initial-disabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getDisabled())));
   }

   public static DedicatedServerProperties fromFile(final Path file) {
      return new DedicatedServerProperties(loadFromFile(file));
   }

   protected DedicatedServerProperties reload(final RegistryAccess registryAccess, final Properties properties) {
      return new DedicatedServerProperties(properties);
   }

   private static @Nullable Component parseResourcePackPrompt(final String prompt) {
      if (!Strings.isNullOrEmpty(prompt)) {
         try {
            JsonElement element = StrictJsonParser.parse(prompt);
            return (Component)ComponentSerialization.CODEC.parse(RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), element).resultOrPartial((msg) -> LOGGER.warn("Failed to parse resource pack prompt '{}': {}", prompt, msg)).orElse((Object)null);
         } catch (Exception e) {
            LOGGER.warn("Failed to parse resource pack prompt '{}'", prompt, e);
         }
      }

      return null;
   }

   private static Optional<MinecraftServer.ServerResourcePackInfo> getServerPackInfo(final String id, final String url, final String resourcePackSha1, final @Nullable String resourcePackHash, final boolean requireResourcePack, final String resourcePackPrompt) {
      if (url.isEmpty()) {
         return Optional.empty();
      } else {
         String hash;
         if (!resourcePackSha1.isEmpty()) {
            hash = resourcePackSha1;
            if (!Strings.isNullOrEmpty(resourcePackHash)) {
               LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
         } else if (!Strings.isNullOrEmpty(resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            hash = resourcePackHash;
         } else {
            hash = "";
         }

         if (hash.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
         } else if (!SHA1.matcher(hash).matches()) {
            LOGGER.warn("Invalid sha1 for resource-pack-sha1");
         }

         Component prompt = parseResourcePackPrompt(resourcePackPrompt);
         UUID parsedId;
         if (id.isEmpty()) {
            parsedId = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
            LOGGER.warn("resource-pack-id missing, using default of {}", parsedId);
         } else {
            try {
               parsedId = UUID.fromString(id);
            } catch (IllegalArgumentException var10) {
               LOGGER.warn("Failed to parse '{}' into UUID", id);
               return Optional.empty();
            }
         }

         return Optional.of(new MinecraftServer.ServerResourcePackInfo(parsedId, url, hash, requireResourcePack, prompt));
      }
   }

   private static DataPackConfig getDatapackConfig(final String enabledPacks, final String disabledPacks) {
      List<String> enabledPacksIds = COMMA_SPLITTER.splitToList(enabledPacks);
      List<String> disabledPacksIds = COMMA_SPLITTER.splitToList(disabledPacks);
      return new DataPackConfig(enabledPacksIds, disabledPacksIds);
   }

   public static @Nullable LevelBasedPermissionSet deserializePermission(final String value) {
      try {
         PermissionLevel permissionLevel = PermissionLevel.byId(Integer.parseInt(value));
         return LevelBasedPermissionSet.forLevel(permissionLevel);
      } catch (NumberFormatException var2) {
         return null;
      }
   }

   public static String serializePermission(final LevelBasedPermissionSet permission) {
      return Integer.toString(permission.level().id());
   }

   public WorldDimensions createDimensions(final HolderLookup.Provider registries) {
      return this.worldDimensionData.create(registries);
   }

   private static record WorldDimensionData(JsonObject generatorSettings, String levelType) {
      private static final Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES;

      private WorldDimensionData {
         super();
      }

      public WorldDimensions create(final HolderLookup.Provider registries) {
         HolderLookup<WorldPreset> worldPresets = registries.lookupOrThrow(Registries.WORLD_PRESET);
         Holder.Reference<WorldPreset> defaultHolder = (Holder.Reference)worldPresets.get(WorldPresets.NORMAL).or(() -> worldPresets.listElements().findAny()).orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
         Optional var10000 = Optional.ofNullable(Identifier.tryParse(this.levelType)).map((id) -> ResourceKey.create(Registries.WORLD_PRESET, id)).or(() -> Optional.ofNullable((ResourceKey)LEGACY_PRESET_NAMES.get(this.levelType)));
         Objects.requireNonNull(worldPresets);
         Holder<WorldPreset> worldPreset = (Holder)var10000.flatMap(worldPresets::get).orElseGet(() -> {
            DedicatedServerProperties.LOGGER.warn("Failed to parse level-type {}, defaulting to {}", this.levelType, defaultHolder.key().identifier());
            return defaultHolder;
         });
         WorldDimensions worldDimensions = ((WorldPreset)worldPreset.value()).createWorldDimensions();
         if (worldPreset.is(WorldPresets.FLAT)) {
            RegistryOps<JsonElement> ops = registries.<JsonElement>createSerializationContext(JsonOps.INSTANCE);
            DataResult var8 = FlatLevelGeneratorSettings.CODEC.parse(new Dynamic(ops, this.generatorSettings()));
            Logger var10001 = DedicatedServerProperties.LOGGER;
            Objects.requireNonNull(var10001);
            Optional<FlatLevelGeneratorSettings> parsedSettings = var8.resultOrPartial(var10001::error);
            if (parsedSettings.isPresent()) {
               return worldDimensions.replaceOverworldGenerator(registries, new FlatLevelSource((FlatLevelGeneratorSettings)parsedSettings.get()));
            }
         }

         return worldDimensions;
      }

      static {
         LEGACY_PRESET_NAMES = Map.of("default", WorldPresets.NORMAL, "largebiomes", WorldPresets.LARGE_BIOMES);
      }
   }
}
