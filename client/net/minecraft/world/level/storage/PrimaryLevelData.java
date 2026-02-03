package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.clock.PackedClockStates;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PrimaryLevelData implements ServerLevelData, WorldData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String LEVEL_NAME = "LevelName";
   protected static final String PLAYER = "Player";
   protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
   private LevelSettings settings;
   private final WorldOptions worldOptions;
   private final SpecialWorldProperty specialWorldProperty;
   private final Lifecycle worldGenSettingsLifecycle;
   private LevelData.RespawnData respawnData;
   private long gameTime;
   private final @Nullable CompoundTag loadedPlayerTag;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private PackedClockStates clockStates;
   private boolean initialized;
   private boolean difficultyLocked;
   /** @deprecated */
   @Deprecated
   private Optional<WorldBorder.Settings> legacyWorldBorderSettings;
   private EndDragonFight.Data endDragonFightData;
   private @Nullable CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   private @Nullable UUID wanderingTraderId;
   private final Set<String> knownServerBrands;
   private boolean wasModded;
   private final Set<String> removedFeatureFlags;
   private final TimerQueue<MinecraftServer> scheduledEvents;

   private PrimaryLevelData(final @Nullable CompoundTag loadedPlayerTag, final boolean wasModded, final LevelData.RespawnData respawnData, final long gameTime, final int version, final int clearWeatherTime, final int rainTime, final boolean raining, final int thunderTime, final boolean thundering, final PackedClockStates clockStates, final boolean initialized, final boolean difficultyLocked, final Optional<WorldBorder.Settings> legacyWorldBorderSettings, final int wanderingTraderSpawnDelay, final int wanderingTraderSpawnChance, final @Nullable UUID wanderingTraderId, final Set<String> knownServerBrands, final Set<String> removedFeatureFlags, final TimerQueue<MinecraftServer> scheduledEvents, final @Nullable CompoundTag customBossEvents, final EndDragonFight.Data endDragonFightData, final LevelSettings settings, final WorldOptions worldOptions, final SpecialWorldProperty specialWorldProperty, final Lifecycle worldGenSettingsLifecycle) {
      super();
      this.wasModded = wasModded;
      this.respawnData = respawnData;
      this.gameTime = gameTime;
      this.version = version;
      this.clearWeatherTime = clearWeatherTime;
      this.rainTime = rainTime;
      this.raining = raining;
      this.thunderTime = thunderTime;
      this.thundering = thundering;
      this.clockStates = clockStates;
      this.initialized = initialized;
      this.difficultyLocked = difficultyLocked;
      this.legacyWorldBorderSettings = legacyWorldBorderSettings;
      this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
      this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
      this.wanderingTraderId = wanderingTraderId;
      this.knownServerBrands = knownServerBrands;
      this.removedFeatureFlags = removedFeatureFlags;
      this.loadedPlayerTag = loadedPlayerTag;
      this.scheduledEvents = scheduledEvents;
      this.customBossEvents = customBossEvents;
      this.endDragonFightData = endDragonFightData;
      this.settings = settings;
      this.worldOptions = worldOptions;
      this.specialWorldProperty = specialWorldProperty;
      this.worldGenSettingsLifecycle = worldGenSettingsLifecycle;
   }

   public PrimaryLevelData(final LevelSettings levelSettings, final WorldOptions worldOptions, final SpecialWorldProperty specialWorldProperty, final Lifecycle lifecycle) {
      this((CompoundTag)null, false, LevelData.RespawnData.DEFAULT, 0L, 19133, 0, 0, false, 0, false, PackedClockStates.EMPTY, false, false, Optional.empty(), 0, 0, (UUID)null, Sets.newLinkedHashSet(), new HashSet(), new TimerQueue(TimerCallbacks.SERVER_CALLBACKS), (CompoundTag)null, EndDragonFight.Data.DEFAULT, levelSettings.copy(), worldOptions, specialWorldProperty, lifecycle);
   }

   public static <T> PrimaryLevelData parse(final Dynamic<T> input, final LevelSettings settings, final SpecialWorldProperty specialWorldProperty, final WorldOptions worldOptions, final Lifecycle worldGenSettingsLifecycle) {
      long gameTime = input.get("Time").asLong(0L);
      OptionalDynamic var10002 = input.get("Player");
      Codec var10003 = CompoundTag.CODEC;
      Objects.requireNonNull(var10003);
      CompoundTag var7 = (CompoundTag)var10002.flatMap(var10003::parse).result().orElse((Object)null);
      boolean var8 = input.get("WasModded").asBoolean(false);
      LevelData.RespawnData var10004 = (LevelData.RespawnData)input.get("spawn").read(LevelData.RespawnData.CODEC).result().orElse(LevelData.RespawnData.DEFAULT);
      int var10006 = LevelVersion.parse(input).levelDataVersion();
      int var10007 = input.get("clearWeatherTime").asInt(0);
      int var10008 = input.get("rainTime").asInt(0);
      boolean var10009 = input.get("raining").asBoolean(false);
      int var10010 = input.get("thunderTime").asInt(0);
      boolean var10011 = input.get("thundering").asBoolean(false);
      PackedClockStates var10012 = (PackedClockStates)input.get("world_clocks").read(PackedClockStates.CODEC).resultOrPartial((error) -> LOGGER.warn("Failed to parse clocks: {}", error)).orElse(PackedClockStates.EMPTY);
      boolean var10013 = input.get("initialized").asBoolean(true);
      boolean var10014 = input.get("DifficultyLocked").asBoolean(false);
      Optional var10015 = WorldBorder.Settings.CODEC.parse(input.get("world_border").orElseEmptyMap()).result();
      int var10016 = input.get("WanderingTraderSpawnDelay").asInt(0);
      int var10017 = input.get("WanderingTraderSpawnChance").asInt(0);
      UUID var10018 = (UUID)input.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse((Object)null);
      Set var10019 = (Set)input.get("ServerBrands").asStream().flatMap((b) -> b.asString().result().stream()).collect(Collectors.toCollection(Sets::newLinkedHashSet));
      Set var10020 = (Set)input.get("removed_features").asStream().flatMap((b) -> b.asString().result().stream()).collect(Collectors.toSet());
      TimerQueue var10021 = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS, input.get("ScheduledEvents").asStream());
      CompoundTag var10022 = (CompoundTag)input.get("CustomBossEvents").orElseEmptyMap().getValue();
      DataResult var10023 = input.get("DragonFight").read(EndDragonFight.Data.CODEC);
      Logger var10024 = LOGGER;
      Objects.requireNonNull(var10024);
      return new PrimaryLevelData(var7, var8, var10004, gameTime, var10006, var10007, var10008, var10009, var10010, var10011, var10012, var10013, var10014, var10015, var10016, var10017, var10018, var10019, var10020, var10021, var10022, (EndDragonFight.Data)var10023.resultOrPartial(var10024::error).orElse(EndDragonFight.Data.DEFAULT), settings, worldOptions, specialWorldProperty, worldGenSettingsLifecycle);
   }

   public CompoundTag createTag(final RegistryAccess registryAccess, @Nullable CompoundTag playerData) {
      if (playerData == null) {
         playerData = this.loadedPlayerTag;
      }

      CompoundTag tag = new CompoundTag();
      this.setTagData(registryAccess, tag, playerData);
      return tag;
   }

   private void setTagData(final RegistryAccess registryAccess, final CompoundTag tag, final @Nullable CompoundTag playerTag) {
      tag.put("ServerBrands", stringCollectionToTag(this.knownServerBrands));
      tag.putBoolean("WasModded", this.wasModded);
      if (!this.removedFeatureFlags.isEmpty()) {
         tag.put("removed_features", stringCollectionToTag(this.removedFeatureFlags));
      }

      CompoundTag worldVersion = new CompoundTag();
      worldVersion.putString("Name", SharedConstants.getCurrentVersion().name());
      worldVersion.putInt("Id", SharedConstants.getCurrentVersion().dataVersion().version());
      worldVersion.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().stable());
      worldVersion.putString("Series", SharedConstants.getCurrentVersion().dataVersion().series());
      tag.put("Version", worldVersion);
      NbtUtils.addCurrentDataVersion(tag);
      DynamicOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
      DataResult var10000 = WorldGenSettings.encode(ops, this.worldOptions, registryAccess);
      Logger var10002 = LOGGER;
      Objects.requireNonNull(var10002);
      var10000.resultOrPartial(Util.prefix("WorldGenSettings: ", var10002::error)).ifPresent((s) -> tag.put("WorldGenSettings", s));
      tag.putInt("GameType", this.settings.gameType().getId());
      tag.store("spawn", LevelData.RespawnData.CODEC, this.respawnData);
      tag.putLong("Time", this.gameTime);
      tag.putLong("LastPlayed", Util.getEpochMillis());
      tag.putString("LevelName", this.settings.levelName());
      tag.putInt("version", 19133);
      tag.putInt("clearWeatherTime", this.clearWeatherTime);
      tag.putInt("rainTime", this.rainTime);
      tag.putBoolean("raining", this.raining);
      tag.putInt("thunderTime", this.thunderTime);
      tag.putBoolean("thundering", this.thundering);
      tag.store("world_clocks", PackedClockStates.CODEC, ops, this.clockStates);
      tag.putBoolean("hardcore", this.settings.hardcore());
      tag.putBoolean("allowCommands", this.settings.allowCommands());
      tag.putBoolean("initialized", this.initialized);
      this.legacyWorldBorderSettings.ifPresent((settings) -> tag.store("world_border", WorldBorder.Settings.CODEC, settings));
      tag.putByte("Difficulty", (byte)this.settings.difficulty().getId());
      tag.putBoolean("DifficultyLocked", this.difficultyLocked);
      tag.store("game_rules", GameRules.codec(this.enabledFeatures()), this.settings.gameRules());
      tag.store("DragonFight", EndDragonFight.Data.CODEC, this.endDragonFightData);
      if (playerTag != null) {
         tag.put("Player", playerTag);
      }

      tag.store(WorldDataConfiguration.MAP_CODEC, this.settings.getDataConfiguration());
      if (this.customBossEvents != null) {
         tag.put("CustomBossEvents", this.customBossEvents);
      }

      tag.put("ScheduledEvents", this.scheduledEvents.store());
      tag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      tag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      tag.storeNullable("WanderingTraderId", UUIDUtil.CODEC, this.wanderingTraderId);
   }

   private static ListTag stringCollectionToTag(final Set<String> values) {
      ListTag result = new ListTag();
      Stream var10000 = values.stream().map(StringTag::valueOf);
      Objects.requireNonNull(result);
      var10000.forEach(result::add);
      return result;
   }

   public LevelData.RespawnData getRespawnData() {
      return this.respawnData;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public @Nullable CompoundTag getLoadedPlayerTag() {
      return this.loadedPlayerTag;
   }

   public void setGameTime(final long time) {
      this.gameTime = time;
   }

   public void setSpawn(final LevelData.RespawnData respawnData) {
      this.respawnData = respawnData;
   }

   public String getLevelName() {
      return this.settings.levelName();
   }

   public int getVersion() {
      return this.version;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(final int clearWeatherTime) {
      this.clearWeatherTime = clearWeatherTime;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(final boolean thundering) {
      this.thundering = thundering;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(final int thunderTime) {
      this.thunderTime = thunderTime;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(final boolean raining) {
      this.raining = raining;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(final int rainTime) {
      this.rainTime = rainTime;
   }

   public void setClockStates(final PackedClockStates packedClocks) {
      this.clockStates = packedClocks;
   }

   public PackedClockStates clockStates() {
      return this.clockStates;
   }

   public GameType getGameType() {
      return this.settings.gameType();
   }

   public void setGameType(final GameType gameType) {
      this.settings = this.settings.withGameType(gameType);
   }

   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   public boolean isAllowCommands() {
      return this.settings.allowCommands();
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(final boolean initialized) {
      this.initialized = initialized;
   }

   public GameRules getGameRules() {
      return this.settings.gameRules();
   }

   public Optional<WorldBorder.Settings> getLegacyWorldBorderSettings() {
      return this.legacyWorldBorderSettings;
   }

   public void setLegacyWorldBorderSettings(final Optional<WorldBorder.Settings> settings) {
      this.legacyWorldBorderSettings = settings;
   }

   public Difficulty getDifficulty() {
      return this.settings.difficulty();
   }

   public void setDifficulty(final Difficulty difficulty) {
      this.settings = this.settings.withDifficulty(difficulty);
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(final boolean difficultyLocked) {
      this.difficultyLocked = difficultyLocked;
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void fillCrashReportCategory(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor) {
      ServerLevelData.super.fillCrashReportCategory(category, levelHeightAccessor);
      WorldData.super.fillCrashReportCategory(category);
   }

   public WorldOptions worldGenOptions() {
      return this.worldOptions;
   }

   public boolean isFlatWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.FLAT;
   }

   public boolean isDebugWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.DEBUG;
   }

   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   public EndDragonFight.Data endDragonFightData() {
      return this.endDragonFightData;
   }

   public void setEndDragonFightData(final EndDragonFight.Data data) {
      this.endDragonFightData = data;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.settings.getDataConfiguration();
   }

   public void setDataConfiguration(final WorldDataConfiguration dataConfiguration) {
      this.settings = this.settings.withDataConfiguration(dataConfiguration);
   }

   public @Nullable CompoundTag getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(final @Nullable CompoundTag customBossEvents) {
      this.customBossEvents = customBossEvents;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(final int wanderingTraderSpawnDelay) {
      this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(final int wanderingTraderSpawnChance) {
      this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
   }

   public @Nullable UUID getWanderingTraderId() {
      return this.wanderingTraderId;
   }

   public void setWanderingTraderId(final UUID wanderingTraderId) {
      this.wanderingTraderId = wanderingTraderId;
   }

   public void setModdedInfo(final String serverBrand, final boolean isModded) {
      this.knownServerBrands.add(serverBrand);
      this.wasModded |= isModded;
   }

   public boolean wasModded() {
      return this.wasModded;
   }

   public Set<String> getKnownServerBrands() {
      return ImmutableSet.copyOf(this.knownServerBrands);
   }

   public Set<String> getRemovedFeatureFlags() {
      return Set.copyOf(this.removedFeatureFlags);
   }

   public ServerLevelData overworldData() {
      return this;
   }

   public LevelSettings getLevelSettings() {
      return this.settings.copy();
   }

   /** @deprecated */
   @Deprecated
   public static enum SpecialWorldProperty {
      NONE,
      FLAT,
      DEBUG;

      private SpecialWorldProperty() {
      }

      // $FF: synthetic method
      private static SpecialWorldProperty[] $values() {
         return new SpecialWorldProperty[]{NONE, FLAT, DEBUG};
      }
   }
}
