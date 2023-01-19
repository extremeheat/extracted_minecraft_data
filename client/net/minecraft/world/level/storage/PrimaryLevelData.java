package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
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
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.slf4j.Logger;

public class PrimaryLevelData implements ServerLevelData, WorldData {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static final String PLAYER = "Player";
   protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
   private LevelSettings settings;
   private final WorldGenSettings worldGenSettings;
   private final Lifecycle worldGenSettingsLifecycle;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
   private float spawnAngle;
   private long gameTime;
   private long dayTime;
   @Nullable
   private final DataFixer fixerUpper;
   private final int playerDataVersion;
   private boolean upgradedPlayerTag;
   @Nullable
   private CompoundTag loadedPlayerTag;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private boolean initialized;
   private boolean difficultyLocked;
   private WorldBorder.Settings worldBorder;
   private CompoundTag endDragonFightData;
   @Nullable
   private CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   @Nullable
   private UUID wanderingTraderId;
   private final Set<String> knownServerBrands;
   private boolean wasModded;
   private final TimerQueue<MinecraftServer> scheduledEvents;

   private PrimaryLevelData(
      @Nullable DataFixer var1,
      int var2,
      @Nullable CompoundTag var3,
      boolean var4,
      int var5,
      int var6,
      int var7,
      float var8,
      long var9,
      long var11,
      int var13,
      int var14,
      int var15,
      boolean var16,
      int var17,
      boolean var18,
      boolean var19,
      boolean var20,
      WorldBorder.Settings var21,
      int var22,
      int var23,
      @Nullable UUID var24,
      Set<String> var25,
      TimerQueue<MinecraftServer> var26,
      @Nullable CompoundTag var27,
      CompoundTag var28,
      LevelSettings var29,
      WorldGenSettings var30,
      Lifecycle var31
   ) {
      super();
      if (!var30.dimensions().containsKey(LevelStem.OVERWORLD)) {
         throw new IllegalStateException("Missing Overworld dimension data");
      } else {
         this.fixerUpper = var1;
         this.wasModded = var4;
         this.xSpawn = var5;
         this.ySpawn = var6;
         this.zSpawn = var7;
         this.spawnAngle = var8;
         this.gameTime = var9;
         this.dayTime = var11;
         this.version = var13;
         this.clearWeatherTime = var14;
         this.rainTime = var15;
         this.raining = var16;
         this.thunderTime = var17;
         this.thundering = var18;
         this.initialized = var19;
         this.difficultyLocked = var20;
         this.worldBorder = var21;
         this.wanderingTraderSpawnDelay = var22;
         this.wanderingTraderSpawnChance = var23;
         this.wanderingTraderId = var24;
         this.knownServerBrands = var25;
         this.loadedPlayerTag = var3;
         this.playerDataVersion = var2;
         this.scheduledEvents = var26;
         this.customBossEvents = var27;
         this.endDragonFightData = var28;
         this.settings = var29;
         this.worldGenSettings = var30;
         this.worldGenSettingsLifecycle = var31;
      }
   }

   public PrimaryLevelData(LevelSettings var1, WorldGenSettings var2, Lifecycle var3) {
      this(
         null,
         SharedConstants.getCurrentVersion().getWorldVersion(),
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
         new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS),
         null,
         new CompoundTag(),
         var1.copy(),
         var2,
         var3
      );
   }

   public static PrimaryLevelData parse(
      Dynamic<Tag> var0, DataFixer var1, int var2, @Nullable CompoundTag var3, LevelSettings var4, LevelVersion var5, WorldGenSettings var6, Lifecycle var7
   ) {
      long var8 = var0.get("Time").asLong(0L);
      CompoundTag var10 = var0.get("DragonFight")
         .result()
         .<CompoundTag>map(Dynamic::getValue)
         .orElseGet(() -> (Tag)var0.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
      return new PrimaryLevelData(
         var1,
         var2,
         var3,
         var0.get("WasModded").asBoolean(false),
         var0.get("SpawnX").asInt(0),
         var0.get("SpawnY").asInt(0),
         var0.get("SpawnZ").asInt(0),
         var0.get("SpawnAngle").asFloat(0.0F),
         var8,
         var0.get("DayTime").asLong(var8),
         var5.levelDataVersion(),
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
         (UUID)var0.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse(null),
         var0.get("ServerBrands").asStream().flatMap(var0x -> var0x.asString().result().stream()).collect(Collectors.toCollection(Sets::newLinkedHashSet)),
         new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS, var0.get("ScheduledEvents").asStream()),
         (CompoundTag)var0.get("CustomBossEvents").orElseEmptyMap().getValue(),
         var10,
         var4,
         var6,
         var7
      );
   }

   @Override
   public CompoundTag createTag(RegistryAccess var1, @Nullable CompoundTag var2) {
      this.updatePlayerTag();
      if (var2 == null) {
         var2 = this.loadedPlayerTag;
      }

      CompoundTag var3 = new CompoundTag();
      this.setTagData(var1, var3, var2);
      return var3;
   }

   private void setTagData(RegistryAccess var1, CompoundTag var2, @Nullable CompoundTag var3) {
      ListTag var4 = new ListTag();
      this.knownServerBrands.stream().map(StringTag::valueOf).forEach(var4::add);
      var2.put("ServerBrands", var4);
      var2.putBoolean("WasModded", this.wasModded);
      CompoundTag var5 = new CompoundTag();
      var5.putString("Name", SharedConstants.getCurrentVersion().getName());
      var5.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
      var5.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      var5.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
      var2.put("Version", var5);
      var2.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      RegistryOps var6 = RegistryOps.create(NbtOps.INSTANCE, var1);
      WorldGenSettings.CODEC
         .encodeStart(var6, this.worldGenSettings)
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
      var2.put("DragonFight", this.endDragonFightData);
      if (var3 != null) {
         var2.put("Player", var3);
      }

      DataPackConfig.CODEC.encodeStart(NbtOps.INSTANCE, this.settings.getDataPackConfig()).result().ifPresent(var1x -> var2.put("DataPacks", var1x));
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

   private void updatePlayerTag() {
      if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
         if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
               throw (NullPointerException)Util.pauseInIde(
                  new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.")
               );
            }

            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
         }

         this.upgradedPlayerTag = true;
      }
   }

   @Override
   public CompoundTag getLoadedPlayerTag() {
      this.updatePlayerTag();
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
   public WorldGenSettings worldGenSettings() {
      return this.worldGenSettings;
   }

   @Override
   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   @Override
   public CompoundTag endDragonFightData() {
      return this.endDragonFightData;
   }

   @Override
   public void setEndDragonFightData(CompoundTag var1) {
      this.endDragonFightData = var1;
   }

   @Override
   public DataPackConfig getDataPackConfig() {
      return this.settings.getDataPackConfig();
   }

   @Override
   public void setDataPackConfig(DataPackConfig var1) {
      this.settings = this.settings.withDataPackConfig(var1);
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
   public ServerLevelData overworldData() {
      return this;
   }

   @Override
   public LevelSettings getLevelSettings() {
      return this.settings.copy();
   }
}
