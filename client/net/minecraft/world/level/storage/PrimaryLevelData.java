package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
   private final PrimaryLevelData.SpecialWorldProperty specialWorldProperty;
   private final Lifecycle worldGenSettingsLifecycle;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
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

   private PrimaryLevelData(
      @Nullable CompoundTag var1,
      boolean var2,
      int var3,
      int var4,
      int var5,
      float var6,
      long var7,
      long var9,
      int var11,
      int var12,
      int var13,
      boolean var14,
      int var15,
      boolean var16,
      boolean var17,
      boolean var18,
      WorldBorder.Settings var19,
      int var20,
      int var21,
      @Nullable UUID var22,
      Set<String> var23,
      Set<String> var24,
      TimerQueue<MinecraftServer> var25,
      @Nullable CompoundTag var26,
      EndDragonFight.Data var27,
      LevelSettings var28,
      WorldOptions var29,
      PrimaryLevelData.SpecialWorldProperty var30,
      Lifecycle var31
   ) {
      super();
      this.wasModded = var2;
      this.xSpawn = var3;
      this.ySpawn = var4;
      this.zSpawn = var5;
      this.spawnAngle = var6;
      this.gameTime = var7;
      this.dayTime = var9;
      this.version = var11;
      this.clearWeatherTime = var12;
      this.rainTime = var13;
      this.raining = var14;
      this.thunderTime = var15;
      this.thundering = var16;
      this.initialized = var17;
      this.difficultyLocked = var18;
      this.worldBorder = var19;
      this.wanderingTraderSpawnDelay = var20;
      this.wanderingTraderSpawnChance = var21;
      this.wanderingTraderId = var22;
      this.knownServerBrands = var23;
      this.removedFeatureFlags = var24;
      this.loadedPlayerTag = var1;
      this.scheduledEvents = var25;
      this.customBossEvents = var26;
      this.endDragonFightData = var27;
      this.settings = var28;
      this.worldOptions = var29;
      this.specialWorldProperty = var30;
      this.worldGenSettingsLifecycle = var31;
   }

   public PrimaryLevelData(LevelSettings var1, WorldOptions var2, PrimaryLevelData.SpecialWorldProperty var3, Lifecycle var4) {
      this(
         null,
         false,
         0,
         0,
         0,
         0.0F,
         0L,
         0L,
         19133,
         0,
         0,
         false,
         0,
         false,
         false,
         false,
         WorldBorder.DEFAULT_SETTINGS,
         0,
         0,
         null,
         Sets.newLinkedHashSet(),
         new HashSet<>(),
         new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS),
         null,
         EndDragonFight.Data.DEFAULT,
         var1.copy(),
         var2,
         var3,
         var4
      );
   }

   public static <T> PrimaryLevelData parse(Dynamic<T> var0, LevelSettings var1, PrimaryLevelData.SpecialWorldProperty var2, WorldOptions var3, Lifecycle var4) {
      long var5 = var0.get("Time").asLong(0L);
      return new PrimaryLevelData(
         (CompoundTag)CompoundTag.CODEC.parse(var0.get("Player").orElseEmptyMap()).result().orElse((T)null),
         var0.get("WasModded").asBoolean(false),
         var0.get("SpawnX").asInt(0),
         var0.get("SpawnY").asInt(0),
         var0.get("SpawnZ").asInt(0),
         var0.get("SpawnAngle").asFloat(0.0F),
         var5,
         var0.get("DayTime").asLong(var5),
         LevelVersion.parse(var0).levelDataVersion(),
         var0.get("clearWeatherTime").asInt(0),
         var0.get("rainTime").asInt(0),
         var0.get("raining").asBoolean(false),
         var0.get("thunderTime").asInt(0),
         var0.get("thundering").asBoolean(false),
         var0.get("initialized").asBoolean(true),
         var0.get("DifficultyLocked").asBoolean(false),
         WorldBorder.Settings.read(var0, WorldBorder.DEFAULT_SETTINGS),
         var0.get("WanderingTraderSpawnDelay").asInt(0),
         var0.get("WanderingTraderSpawnChance").asInt(0),
         (UUID)var0.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse((T)null),
         var0.get("ServerBrands").asStream().flatMap(var0x -> var0x.asString().result().stream()).collect(Collectors.toCollection(Sets::newLinkedHashSet)),
         var0.get("removed_features").asStream().flatMap(var0x -> var0x.asString().result().stream()).collect(Collectors.toSet()),
         new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS, var0.get("ScheduledEvents").asStream()),
         (CompoundTag)var0.get("CustomBossEvents").orElseEmptyMap().getValue(),
         var0.get("DragonFight").read(EndDragonFight.Data.CODEC).resultOrPartial(LOGGER::error).orElse(EndDragonFight.Data.DEFAULT),
         var1,
         var3,
         var2,
         var4
      );
   }

   @Override
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
      RegistryOps var5 = RegistryOps.create(NbtOps.INSTANCE, var1);
      WorldGenSettings.encode(var5, this.worldOptions, var1)
         .resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error))
         .ifPresent(var1x -> var2.put("WorldGenSettings", var1x));
      var2.putInt("GameType", this.settings.gameType().getId());
      var2.putInt("SpawnX", this.xSpawn);
      var2.putInt("SpawnY", this.ySpawn);
      var2.putInt("SpawnZ", this.zSpawn);
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
      var2.put("DragonFight", Util.getOrThrow(EndDragonFight.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.endDragonFightData), IllegalStateException::new));
      if (var3 != null) {
         var2.put("Player", var3);
      }

      DataResult var6 = WorldDataConfiguration.CODEC.encodeStart(NbtOps.INSTANCE, this.settings.getDataConfiguration());
      var6.get().ifLeft(var1x -> var2.merge((CompoundTag)var1x)).ifRight(var0 -> LOGGER.warn("Failed to encode configuration {}", var0.message()));
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
      var0.stream().map(StringTag::valueOf).forEach(var1::add);
      return var1;
   }

   @Override
   public int getXSpawn() {
      return this.xSpawn;
   }

   @Override
   public int getYSpawn() {
      return this.ySpawn;
   }

   @Override
   public int getZSpawn() {
      return this.zSpawn;
   }

   @Override
   public float getSpawnAngle() {
      return this.spawnAngle;
   }

   @Override
   public long getGameTime() {
      return this.gameTime;
   }

   @Override
   public long getDayTime() {
      return this.dayTime;
   }

   @Nullable
   @Override
   public CompoundTag getLoadedPlayerTag() {
      return this.loadedPlayerTag;
   }

   @Override
   public void setXSpawn(int var1) {
      this.xSpawn = var1;
   }

   @Override
   public void setYSpawn(int var1) {
      this.ySpawn = var1;
   }

   @Override
   public void setZSpawn(int var1) {
      this.zSpawn = var1;
   }

   @Override
   public void setSpawnAngle(float var1) {
      this.spawnAngle = var1;
   }

   @Override
   public void setGameTime(long var1) {
      this.gameTime = var1;
   }

   @Override
   public void setDayTime(long var1) {
      this.dayTime = var1;
   }

   @Override
   public void setSpawn(BlockPos var1, float var2) {
      this.xSpawn = var1.getX();
      this.ySpawn = var1.getY();
      this.zSpawn = var1.getZ();
      this.spawnAngle = var2;
   }

   @Override
   public String getLevelName() {
      return this.settings.levelName();
   }

   @Override
   public int getVersion() {
      return this.version;
   }

   @Override
   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   @Override
   public void setClearWeatherTime(int var1) {
      this.clearWeatherTime = var1;
   }

   @Override
   public boolean isThundering() {
      return this.thundering;
   }

   @Override
   public void setThundering(boolean var1) {
      this.thundering = var1;
   }

   @Override
   public int getThunderTime() {
      return this.thunderTime;
   }

   @Override
   public void setThunderTime(int var1) {
      this.thunderTime = var1;
   }

   @Override
   public boolean isRaining() {
      return this.raining;
   }

   @Override
   public void setRaining(boolean var1) {
      this.raining = var1;
   }

   @Override
   public int getRainTime() {
      return this.rainTime;
   }

   @Override
   public void setRainTime(int var1) {
      this.rainTime = var1;
   }

   @Override
   public GameType getGameType() {
      return this.settings.gameType();
   }

   @Override
   public void setGameType(GameType var1) {
      this.settings = this.settings.withGameType(var1);
   }

   @Override
   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   @Override
   public boolean getAllowCommands() {
      return this.settings.allowCommands();
   }

   @Override
   public boolean isInitialized() {
      return this.initialized;
   }

   @Override
   public void setInitialized(boolean var1) {
      this.initialized = var1;
   }

   @Override
   public GameRules getGameRules() {
      return this.settings.gameRules();
   }

   @Override
   public WorldBorder.Settings getWorldBorder() {
      return this.worldBorder;
   }

   @Override
   public void setWorldBorder(WorldBorder.Settings var1) {
      this.worldBorder = var1;
   }

   @Override
   public Difficulty getDifficulty() {
      return this.settings.difficulty();
   }

   @Override
   public void setDifficulty(Difficulty var1) {
      this.settings = this.settings.withDifficulty(var1);
   }

   @Override
   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   @Override
   public void setDifficultyLocked(boolean var1) {
      this.difficultyLocked = var1;
   }

   @Override
   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   @Override
   public void fillCrashReportCategory(CrashReportCategory var1, LevelHeightAccessor var2) {
      ServerLevelData.super.fillCrashReportCategory(var1, var2);
      WorldData.super.fillCrashReportCategory(var1);
   }

   @Override
   public WorldOptions worldGenOptions() {
      return this.worldOptions;
   }

   @Override
   public boolean isFlatWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.FLAT;
   }

   @Override
   public boolean isDebugWorld() {
      return this.specialWorldProperty == PrimaryLevelData.SpecialWorldProperty.DEBUG;
   }

   @Override
   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   @Override
   public EndDragonFight.Data endDragonFightData() {
      return this.endDragonFightData;
   }

   @Override
   public void setEndDragonFightData(EndDragonFight.Data var1) {
      this.endDragonFightData = var1;
   }

   @Override
   public WorldDataConfiguration getDataConfiguration() {
      return this.settings.getDataConfiguration();
   }

   @Override
   public void setDataConfiguration(WorldDataConfiguration var1) {
      this.settings = this.settings.withDataConfiguration(var1);
   }

   @Nullable
   @Override
   public CompoundTag getCustomBossEvents() {
      return this.customBossEvents;
   }

   @Override
   public void setCustomBossEvents(@Nullable CompoundTag var1) {
      this.customBossEvents = var1;
   }

   @Override
   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   @Override
   public void setWanderingTraderSpawnDelay(int var1) {
      this.wanderingTraderSpawnDelay = var1;
   }

   @Override
   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   @Override
   public void setWanderingTraderSpawnChance(int var1) {
      this.wanderingTraderSpawnChance = var1;
   }

   @Nullable
   @Override
   public UUID getWanderingTraderId() {
      return this.wanderingTraderId;
   }

   @Override
   public void setWanderingTraderId(UUID var1) {
      this.wanderingTraderId = var1;
   }

   @Override
   public void setModdedInfo(String var1, boolean var2) {
      this.knownServerBrands.add(var1);
      this.wasModded |= var2;
   }

   @Override
   public boolean wasModded() {
      return this.wasModded;
   }

   @Override
   public Set<String> getKnownServerBrands() {
      return ImmutableSet.copyOf(this.knownServerBrands);
   }

   @Override
   public Set<String> getRemovedFeatureFlags() {
      return Set.copyOf(this.removedFeatureFlags);
   }

   @Override
   public ServerLevelData overworldData() {
      return this;
   }

   @Override
   public LevelSettings getLevelSettings() {
      return this.settings.copy();
   }

   @Deprecated
   public static enum SpecialWorldProperty {
      NONE,
      FLAT,
      DEBUG;

      private SpecialWorldProperty() {
      }
   }
}
