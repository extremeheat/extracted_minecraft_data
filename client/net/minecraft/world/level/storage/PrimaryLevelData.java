package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
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
   private BlockPos spawnPos;
   private float spawnAngle;
   private long gameTime;
   private long dayTime;
   @Nullable
   private final CompoundTag loadedPlayerTag;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private boolean initialized;
   private boolean difficultyLocked;
   private WorldBorder.Settings worldBorder;
   private EndDragonFight.Data endDragonFightData;
   @Nullable
   private CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   @Nullable
   private UUID wanderingTraderId;
   private final Set<String> knownServerBrands;
   private boolean wasModded;
   private final Set<String> removedFeatureFlags;
   private final TimerQueue<MinecraftServer> scheduledEvents;

   private PrimaryLevelData(@Nullable CompoundTag var1, boolean var2, BlockPos var3, float var4, long var5, long var7, int var9, int var10, int var11, boolean var12, int var13, boolean var14, boolean var15, boolean var16, WorldBorder.Settings var17, int var18, int var19, @Nullable UUID var20, Set<String> var21, Set<String> var22, TimerQueue<MinecraftServer> var23, @Nullable CompoundTag var24, EndDragonFight.Data var25, LevelSettings var26, WorldOptions var27, SpecialWorldProperty var28, Lifecycle var29) {
      super();
      this.wasModded = var2;
      this.spawnPos = var3;
      this.spawnAngle = var4;
      this.gameTime = var5;
      this.dayTime = var7;
      this.version = var9;
      this.clearWeatherTime = var10;
      this.rainTime = var11;
      this.raining = var12;
      this.thunderTime = var13;
      this.thundering = var14;
      this.initialized = var15;
      this.difficultyLocked = var16;
      this.worldBorder = var17;
      this.wanderingTraderSpawnDelay = var18;
      this.wanderingTraderSpawnChance = var19;
      this.wanderingTraderId = var20;
      this.knownServerBrands = var21;
      this.removedFeatureFlags = var22;
      this.loadedPlayerTag = var1;
      this.scheduledEvents = var23;
      this.customBossEvents = var24;
      this.endDragonFightData = var25;
      this.settings = var26;
      this.worldOptions = var27;
      this.specialWorldProperty = var28;
      this.worldGenSettingsLifecycle = var29;
   }

   public PrimaryLevelData(LevelSettings var1, WorldOptions var2, SpecialWorldProperty var3, Lifecycle var4) {
      this((CompoundTag)null, false, BlockPos.ZERO, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new HashSet(), new TimerQueue(TimerCallbacks.SERVER_CALLBACKS), (CompoundTag)null, EndDragonFight.Data.DEFAULT, var1.copy(), var2, var3, var4);
   }

   public static <T> PrimaryLevelData parse(Dynamic<T> var0, LevelSettings var1, SpecialWorldProperty var2, WorldOptions var3, Lifecycle var4) {
      long var5 = var0.get("Time").asLong(0L);
      OptionalDynamic var10002 = var0.get("Player");
      Codec var10003 = CompoundTag.CODEC;
      Objects.requireNonNull(var10003);
      CompoundTag var7 = (CompoundTag)var10002.flatMap(var10003::parse).result().orElse((Object)null);
      boolean var8 = var0.get("WasModded").asBoolean(false);
      BlockPos var10004 = new BlockPos(var0.get("SpawnX").asInt(0), var0.get("SpawnY").asInt(0), var0.get("SpawnZ").asInt(0));
      float var10005 = var0.get("SpawnAngle").asFloat(0.0F);
      long var10007 = var0.get("DayTime").asLong(var5);
      int var10008 = LevelVersion.parse(var0).levelDataVersion();
      int var10009 = var0.get("clearWeatherTime").asInt(0);
      int var10010 = var0.get("rainTime").asInt(0);
      boolean var10011 = var0.get("raining").asBoolean(false);
      int var10012 = var0.get("thunderTime").asInt(0);
      boolean var10013 = var0.get("thundering").asBoolean(false);
      boolean var10014 = var0.get("initialized").asBoolean(true);
      boolean var10015 = var0.get("DifficultyLocked").asBoolean(false);
      WorldBorder.Settings var10016 = WorldBorder.Settings.read(var0, WorldBorder.DEFAULT_SETTINGS);
      int var10017 = var0.get("WanderingTraderSpawnDelay").asInt(0);
      int var10018 = var0.get("WanderingTraderSpawnChance").asInt(0);
      UUID var10019 = (UUID)var0.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse((Object)null);
      Set var10020 = (Set)var0.get("ServerBrands").asStream().flatMap((var0x) -> {
         return var0x.asString().result().stream();
      }).collect(Collectors.toCollection(Sets::newLinkedHashSet));
      Set var10021 = (Set)var0.get("removed_features").asStream().flatMap((var0x) -> {
         return var0x.asString().result().stream();
      }).collect(Collectors.toSet());
      TimerQueue var10022 = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS, var0.get("ScheduledEvents").asStream());
      CompoundTag var10023 = (CompoundTag)var0.get("CustomBossEvents").orElseEmptyMap().getValue();
      DataResult var10024 = var0.get("DragonFight").read(EndDragonFight.Data.CODEC);
      Logger var10025 = LOGGER;
      Objects.requireNonNull(var10025);
      return new PrimaryLevelData(var7, var8, var10004, var10005, var5, var10007, var10008, var10009, var10010, var10011, var10012, var10013, var10014, var10015, var10016, var10017, var10018, var10019, var10020, var10021, var10022, var10023, (EndDragonFight.Data)var10024.resultOrPartial(var10025::error).orElse(EndDragonFight.Data.DEFAULT), var1, var3, var2, var4);
   }

   public CompoundTag createTag(RegistryAccess var1, @Nullable CompoundTag var2) {
      if (var2 == null) {
         var2 = this.loadedPlayerTag;
      }

      CompoundTag var3 = new CompoundTag();
      this.setTagData(var1, var3, var2);
      return var3;
   }

   private void setTagData(RegistryAccess var1, CompoundTag var2, @Nullable CompoundTag var3) {
      var2.put("ServerBrands", stringCollectionToTag(this.knownServerBrands));
      var2.putBoolean("WasModded", this.wasModded);
      if (!this.removedFeatureFlags.isEmpty()) {
         var2.put("removed_features", stringCollectionToTag(this.removedFeatureFlags));
      }

      CompoundTag var4 = new CompoundTag();
      var4.putString("Name", SharedConstants.getCurrentVersion().getName());
      var4.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
      var4.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      var4.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
      var2.put("Version", var4);
      NbtUtils.addCurrentDataVersion(var2);
      RegistryOps var5 = var1.createSerializationContext(NbtOps.INSTANCE);
      DataResult var10000 = WorldGenSettings.encode(var5, this.worldOptions, (RegistryAccess)var1);
      Logger var10002 = LOGGER;
      Objects.requireNonNull(var10002);
      var10000.resultOrPartial(Util.prefix("WorldGenSettings: ", var10002::error)).ifPresent((var1x) -> {
         var2.put("WorldGenSettings", var1x);
      });
      var2.putInt("GameType", this.settings.gameType().getId());
      var2.putInt("SpawnX", this.spawnPos.getX());
      var2.putInt("SpawnY", this.spawnPos.getY());
      var2.putInt("SpawnZ", this.spawnPos.getZ());
      var2.putFloat("SpawnAngle", this.spawnAngle);
      var2.putLong("Time", this.gameTime);
      var2.putLong("DayTime", this.dayTime);
      var2.putLong("LastPlayed", Util.getEpochMillis());
      var2.putString("LevelName", this.settings.levelName());
      var2.putInt("version", 19133);
      var2.putInt("clearWeatherTime", this.clearWeatherTime);
      var2.putInt("rainTime", this.rainTime);
      var2.putBoolean("raining", this.raining);
      var2.putInt("thunderTime", this.thunderTime);
      var2.putBoolean("thundering", this.thundering);
      var2.putBoolean("hardcore", this.settings.hardcore());
      var2.putBoolean("allowCommands", this.settings.allowCommands());
      var2.putBoolean("initialized", this.initialized);
      this.worldBorder.write(var2);
      var2.putByte("Difficulty", (byte)this.settings.difficulty().getId());
      var2.putBoolean("DifficultyLocked", this.difficultyLocked);
      var2.put("GameRules", this.settings.gameRules().createTag());
      var2.put("DragonFight", (Tag)EndDragonFight.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.endDragonFightData).getOrThrow());
      if (var3 != null) {
         var2.put("Player", var3);
      }

      WorldDataConfiguration.CODEC.encodeStart(NbtOps.INSTANCE, this.settings.getDataConfiguration()).ifSuccess((var1x) -> {
         var2.merge((CompoundTag)var1x);
      }).ifError((var0) -> {
         LOGGER.warn("Failed to encode configuration {}", var0.message());
      });
      if (this.customBossEvents != null) {
         var2.put("CustomBossEvents", this.customBossEvents);
      }

      var2.put("ScheduledEvents", this.scheduledEvents.store());
      var2.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      var2.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         var2.putUUID("WanderingTraderId", this.wanderingTraderId);
      }

   }

   private static ListTag stringCollectionToTag(Set<String> var0) {
      ListTag var1 = new ListTag();
      Stream var10000 = var0.stream().map(StringTag::valueOf);
      Objects.requireNonNull(var1);
      var10000.forEach(var1::add);
      return var1;
   }

   public BlockPos getSpawnPos() {
      return this.spawnPos;
   }

   public float getSpawnAngle() {
      return this.spawnAngle;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   @Nullable
   public CompoundTag getLoadedPlayerTag() {
      return this.loadedPlayerTag;
   }

   public void setGameTime(long var1) {
      this.gameTime = var1;
   }

   public void setDayTime(long var1) {
      this.dayTime = var1;
   }

   public void setSpawn(BlockPos var1, float var2) {
      this.spawnPos = var1.immutable();
      this.spawnAngle = var2;
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

   public void setClearWeatherTime(int var1) {
      this.clearWeatherTime = var1;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean var1) {
      this.thundering = var1;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int var1) {
      this.thunderTime = var1;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean var1) {
      this.raining = var1;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int var1) {
      this.rainTime = var1;
   }

   public GameType getGameType() {
      return this.settings.gameType();
   }

   public void setGameType(GameType var1) {
      this.settings = this.settings.withGameType(var1);
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

   public void setInitialized(boolean var1) {
      this.initialized = var1;
   }

   public GameRules getGameRules() {
      return this.settings.gameRules();
   }

   public WorldBorder.Settings getWorldBorder() {
      return this.worldBorder;
   }

   public void setWorldBorder(WorldBorder.Settings var1) {
      this.worldBorder = var1;
   }

   public Difficulty getDifficulty() {
      return this.settings.difficulty();
   }

   public void setDifficulty(Difficulty var1) {
      this.settings = this.settings.withDifficulty(var1);
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean var1) {
      this.difficultyLocked = var1;
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void fillCrashReportCategory(CrashReportCategory var1, LevelHeightAccessor var2) {
      ServerLevelData.super.fillCrashReportCategory(var1, var2);
      WorldData.super.fillCrashReportCategory(var1);
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

   public void setEndDragonFightData(EndDragonFight.Data var1) {
      this.endDragonFightData = var1;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.settings.getDataConfiguration();
   }

   public void setDataConfiguration(WorldDataConfiguration var1) {
      this.settings = this.settings.withDataConfiguration(var1);
   }

   @Nullable
   public CompoundTag getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundTag var1) {
      this.customBossEvents = var1;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int var1) {
      this.wanderingTraderSpawnDelay = var1;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int var1) {
      this.wanderingTraderSpawnChance = var1;
   }

   @Nullable
   public UUID getWanderingTraderId() {
      return this.wanderingTraderId;
   }

   public void setWanderingTraderId(UUID var1) {
      this.wanderingTraderId = var1;
   }

   public void setModdedInfo(String var1, boolean var2) {
      this.knownServerBrands.add(var1);
      this.wasModded |= var2;
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
