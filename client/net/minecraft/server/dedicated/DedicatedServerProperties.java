package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
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
   public final boolean spawnAnimals = this.get("spawn-animals", true);
   public final boolean spawnNpcs = this.get("spawn-npcs", true);
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String motd = this.get("motd", "A Minecraft Server");
   public final boolean forceGameMode = this.get("force-gamemode", false);
   public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
   public final Difficulty difficulty = this.get(
      "difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY
   );
   public final GameType gamemode = this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
   public final String levelName = this.get("level-name", "world");
   public final int serverPort = this.get("server-port", 25565);
   @Nullable
   public final Boolean announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
   public final boolean enableQuery = this.get("enable-query", false);
   public final int queryPort = this.get("query.port", 25565);
   public final boolean enableRcon = this.get("enable-rcon", false);
   public final int rconPort = this.get("rcon.port", 25575);
   public final String rconPassword = this.get("rcon.password", "");
   public final boolean hardcore = this.get("hardcore", false);
   public final boolean allowNether = this.get("allow-nether", true);
   public final boolean spawnMonsters = this.get("spawn-monsters", true);
   public final boolean useNativeTransport = this.get("use-native-transport", true);
   public final boolean enableCommandBlock = this.get("enable-command-block", false);
   public final int spawnProtection = this.get("spawn-protection", 16);
   public final int opPermissionLevel = this.get("op-permission-level", 4);
   public final int functionPermissionLevel = this.get("function-permission-level", 2);
   public final long maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
   public final int maxChainedNeighborUpdates = this.get("max-chained-neighbor-updates", 1000000);
   public final int rateLimitPacketsPerSecond = this.get("rate-limit", 0);
   public final int viewDistance = this.get("view-distance", 10);
   public final int simulationDistance = this.get("simulation-distance", 10);
   public final int maxPlayers = this.get("max-players", 20);
   public final int networkCompressionThreshold = this.get("network-compression-threshold", 256);
   public final boolean broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
   public final boolean broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
   public final int maxWorldSize = this.get("max-world-size", var0 -> Mth.clamp(var0, 1, 29999984), 29999984);
   public final boolean syncChunkWrites = this.get("sync-chunk-writes", true);
   public final String regionFileComression = this.get("region-file-compression", "deflate");
   public final boolean enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
   public final boolean enableStatus = this.get("enable-status", true);
   public final boolean hideOnlinePlayers = this.get("hide-online-players", false);
   public final int entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", var0 -> Mth.clamp(var0, 10, 1000), 100);
   public final String textFilteringConfig = this.get("text-filtering-config", "");
   public final Optional<MinecraftServer.ServerResourcePackInfo> serverResourcePackInfo;
   public final DataPackConfig initialDataPackConfiguration;
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> whiteList = this.getMutable("white-list", false);
   public final boolean enforceSecureProfile = this.get("enforce-secure-profile", true);
   public final boolean logIPs = this.get("log-ips", true);
   private final DedicatedServerProperties.WorldDimensionData worldDimensionData;
   public final WorldOptions worldOptions;
   public boolean acceptsTransfers = this.get("accepts-transfers", false);

   public DedicatedServerProperties(Properties var1) {
      super(var1);
      String var2 = this.get("level-seed", "");
      boolean var3 = this.get("generate-structures", true);
      long var4 = WorldOptions.parseSeed(var2).orElse(WorldOptions.randomSeed());
      this.worldOptions = new WorldOptions(var4, var3, false);
      this.worldDimensionData = new DedicatedServerProperties.WorldDimensionData(
         this.get("generator-settings", var0 -> GsonHelper.parse(!var0.isEmpty() ? var0 : "{}"), new JsonObject()),
         this.get("level-type", var0 -> var0.toLowerCase(Locale.ROOT), WorldPresets.NORMAL.location().toString())
      );
      this.serverResourcePackInfo = getServerPackInfo(
         this.get("resource-pack-id", ""),
         this.get("resource-pack", ""),
         this.get("resource-pack-sha1", ""),
         this.getLegacyString("resource-pack-hash"),
         this.get("require-resource-pack", false),
         this.get("resource-pack-prompt", "")
      );
      this.initialDataPackConfiguration = getDatapackConfig(
         this.get("initial-enabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getEnabled())),
         this.get("initial-disabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getDisabled()))
      );
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
            return Component.Serializer.fromJson(var0, RegistryAccess.EMPTY);
         } catch (Exception var2) {
            LOGGER.warn("Failed to parse resource pack prompt '{}'", var0, var2);
         }
      }

      return null;
   }

   private static Optional<MinecraftServer.ServerResourcePackInfo> getServerPackInfo(
      String var0, String var1, String var2, @Nullable String var3, boolean var4, String var5
   ) {
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
            LOGGER.warn(
               "You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack."
            );
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

   public WorldDimensions createDimensions(RegistryAccess var1) {
      return this.worldDimensionData.create(var1);
   }

   static record WorldDimensionData(JsonObject a, String b) {
      private final JsonObject generatorSettings;
      private final String levelType;
      private static final Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES = Map.of(
         "default", WorldPresets.NORMAL, "largebiomes", WorldPresets.LARGE_BIOMES
      );

      WorldDimensionData(JsonObject var1, String var2) {
         super();
         this.generatorSettings = var1;
         this.levelType = var2;
      }

      public WorldDimensions create(RegistryAccess var1) {
         Registry var2 = var1.registryOrThrow(Registries.WORLD_PRESET);
         Holder.Reference var3 = var2.getHolder(WorldPresets.NORMAL)
            .or(() -> var2.holders().findAny())
            .orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
         Holder var4 = Optional.ofNullable(ResourceLocation.tryParse(this.levelType))
            .map(var0 -> ResourceKey.create(Registries.WORLD_PRESET, var0))
            .or(() -> Optional.ofNullable(LEGACY_PRESET_NAMES.get(this.levelType)))
            .flatMap(var2::getHolder)
            .orElseGet(() -> {
               DedicatedServerProperties.LOGGER.warn("Failed to parse level-type {}, defaulting to {}", this.levelType, var3.key().location());
               return var3;
            });
         WorldDimensions var5 = ((WorldPreset)var4.value()).createWorldDimensions();
         if (var4.is(WorldPresets.FLAT)) {
            RegistryOps var6 = var1.createSerializationContext(JsonOps.INSTANCE);
            Optional var7 = FlatLevelGeneratorSettings.CODEC
               .parse(new Dynamic(var6, this.generatorSettings()))
               .resultOrPartial(DedicatedServerProperties.LOGGER::error);
            if (var7.isPresent()) {
               return var5.replaceOverworldGenerator(var1, new FlatLevelSource((FlatLevelGeneratorSettings)var7.get()));
            }
         }

         return var5;
      }
   }
}