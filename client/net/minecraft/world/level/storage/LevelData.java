package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;

public class LevelData {
   private String minecraftVersionName;
   private int minecraftVersion;
   private boolean snapshot;
   public static final Difficulty DEFAULT_DIFFICULTY;
   private long seed;
   private LevelType generator;
   private CompoundTag generatorOptions;
   @Nullable
   private String legacyCustomOptions;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
   private long gameTime;
   private long dayTime;
   private long lastPlayed;
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixerUpper;
   private final int playerDataVersion;
   private boolean upgradedPlayerTag;
   private CompoundTag loadedPlayerTag;
   private String levelName;
   private int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private GameType gameType;
   private boolean generateMapFeatures;
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private Difficulty difficulty;
   private boolean difficultyLocked;
   private double borderX;
   private double borderZ;
   private double borderSize;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone;
   private double borderDamagePerBlock;
   private int borderWarningBlocks;
   private int borderWarningTime;
   private final Set<String> disabledDataPacks;
   private final Set<String> enabledDataPacks;
   private final Map<DimensionType, CompoundTag> dimensionData;
   private CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   private UUID wanderingTraderId;
   private final GameRules gameRules;
   private final TimerQueue<MinecraftServer> scheduledEvents;

   protected LevelData() {
      super();
      this.generator = LevelType.NORMAL;
      this.generatorOptions = new CompoundTag();
      this.borderSize = 6.0E7D;
      this.borderSafeZone = 5.0D;
      this.borderDamagePerBlock = 0.2D;
      this.borderWarningBlocks = 5;
      this.borderWarningTime = 15;
      this.disabledDataPacks = Sets.newHashSet();
      this.enabledDataPacks = Sets.newLinkedHashSet();
      this.dimensionData = Maps.newIdentityHashMap();
      this.gameRules = new GameRules();
      this.scheduledEvents = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS);
      this.fixerUpper = null;
      this.playerDataVersion = SharedConstants.getCurrentVersion().getWorldVersion();
      this.setGeneratorOptions(new CompoundTag());
   }

   public LevelData(CompoundTag var1, DataFixer var2, int var3, @Nullable CompoundTag var4) {
      super();
      this.generator = LevelType.NORMAL;
      this.generatorOptions = new CompoundTag();
      this.borderSize = 6.0E7D;
      this.borderSafeZone = 5.0D;
      this.borderDamagePerBlock = 0.2D;
      this.borderWarningBlocks = 5;
      this.borderWarningTime = 15;
      this.disabledDataPacks = Sets.newHashSet();
      this.enabledDataPacks = Sets.newLinkedHashSet();
      this.dimensionData = Maps.newIdentityHashMap();
      this.gameRules = new GameRules();
      this.scheduledEvents = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS);
      this.fixerUpper = var2;
      CompoundTag var5;
      if (var1.contains("Version", 10)) {
         var5 = var1.getCompound("Version");
         this.minecraftVersionName = var5.getString("Name");
         this.minecraftVersion = var5.getInt("Id");
         this.snapshot = var5.getBoolean("Snapshot");
      }

      this.seed = var1.getLong("RandomSeed");
      if (var1.contains("generatorName", 8)) {
         String var9 = var1.getString("generatorName");
         this.generator = LevelType.getLevelType(var9);
         if (this.generator == null) {
            this.generator = LevelType.NORMAL;
         } else if (this.generator == LevelType.CUSTOMIZED) {
            this.legacyCustomOptions = var1.getString("generatorOptions");
         } else if (this.generator.hasReplacement()) {
            int var6 = 0;
            if (var1.contains("generatorVersion", 99)) {
               var6 = var1.getInt("generatorVersion");
            }

            this.generator = this.generator.getReplacementForVersion(var6);
         }

         this.setGeneratorOptions(var1.getCompound("generatorOptions"));
      }

      this.gameType = GameType.byId(var1.getInt("GameType"));
      if (var1.contains("legacy_custom_options", 8)) {
         this.legacyCustomOptions = var1.getString("legacy_custom_options");
      }

      if (var1.contains("MapFeatures", 99)) {
         this.generateMapFeatures = var1.getBoolean("MapFeatures");
      } else {
         this.generateMapFeatures = true;
      }

      this.xSpawn = var1.getInt("SpawnX");
      this.ySpawn = var1.getInt("SpawnY");
      this.zSpawn = var1.getInt("SpawnZ");
      this.gameTime = var1.getLong("Time");
      if (var1.contains("DayTime", 99)) {
         this.dayTime = var1.getLong("DayTime");
      } else {
         this.dayTime = this.gameTime;
      }

      this.lastPlayed = var1.getLong("LastPlayed");
      this.sizeOnDisk = var1.getLong("SizeOnDisk");
      this.levelName = var1.getString("LevelName");
      this.version = var1.getInt("version");
      this.clearWeatherTime = var1.getInt("clearWeatherTime");
      this.rainTime = var1.getInt("rainTime");
      this.raining = var1.getBoolean("raining");
      this.thunderTime = var1.getInt("thunderTime");
      this.thundering = var1.getBoolean("thundering");
      this.hardcore = var1.getBoolean("hardcore");
      if (var1.contains("initialized", 99)) {
         this.initialized = var1.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (var1.contains("allowCommands", 99)) {
         this.allowCommands = var1.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.playerDataVersion = var3;
      if (var4 != null) {
         this.loadedPlayerTag = var4;
      }

      if (var1.contains("GameRules", 10)) {
         this.gameRules.loadFromTag(var1.getCompound("GameRules"));
      }

      if (var1.contains("Difficulty", 99)) {
         this.difficulty = Difficulty.byId(var1.getByte("Difficulty"));
      }

      if (var1.contains("DifficultyLocked", 1)) {
         this.difficultyLocked = var1.getBoolean("DifficultyLocked");
      }

      if (var1.contains("BorderCenterX", 99)) {
         this.borderX = var1.getDouble("BorderCenterX");
      }

      if (var1.contains("BorderCenterZ", 99)) {
         this.borderZ = var1.getDouble("BorderCenterZ");
      }

      if (var1.contains("BorderSize", 99)) {
         this.borderSize = var1.getDouble("BorderSize");
      }

      if (var1.contains("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = var1.getLong("BorderSizeLerpTime");
      }

      if (var1.contains("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = var1.getDouble("BorderSizeLerpTarget");
      }

      if (var1.contains("BorderSafeZone", 99)) {
         this.borderSafeZone = var1.getDouble("BorderSafeZone");
      }

      if (var1.contains("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = var1.getDouble("BorderDamagePerBlock");
      }

      if (var1.contains("BorderWarningBlocks", 99)) {
         this.borderWarningBlocks = var1.getInt("BorderWarningBlocks");
      }

      if (var1.contains("BorderWarningTime", 99)) {
         this.borderWarningTime = var1.getInt("BorderWarningTime");
      }

      if (var1.contains("DimensionData", 10)) {
         var5 = var1.getCompound("DimensionData");
         Iterator var10 = var5.getAllKeys().iterator();

         while(var10.hasNext()) {
            String var7 = (String)var10.next();
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(var7)), var5.getCompound(var7));
         }
      }

      if (var1.contains("DataPacks", 10)) {
         var5 = var1.getCompound("DataPacks");
         ListTag var11 = var5.getList("Disabled", 8);

         for(int var12 = 0; var12 < var11.size(); ++var12) {
            this.disabledDataPacks.add(var11.getString(var12));
         }

         ListTag var13 = var5.getList("Enabled", 8);

         for(int var8 = 0; var8 < var13.size(); ++var8) {
            this.enabledDataPacks.add(var13.getString(var8));
         }
      }

      if (var1.contains("CustomBossEvents", 10)) {
         this.customBossEvents = var1.getCompound("CustomBossEvents");
      }

      if (var1.contains("ScheduledEvents", 9)) {
         this.scheduledEvents.load(var1.getList("ScheduledEvents", 10));
      }

      if (var1.contains("WanderingTraderSpawnDelay", 99)) {
         this.wanderingTraderSpawnDelay = var1.getInt("WanderingTraderSpawnDelay");
      }

      if (var1.contains("WanderingTraderSpawnChance", 99)) {
         this.wanderingTraderSpawnChance = var1.getInt("WanderingTraderSpawnChance");
      }

      if (var1.contains("WanderingTraderId", 8)) {
         this.wanderingTraderId = UUID.fromString(var1.getString("WanderingTraderId"));
      }

   }

   public LevelData(LevelSettings var1, String var2) {
      super();
      this.generator = LevelType.NORMAL;
      this.generatorOptions = new CompoundTag();
      this.borderSize = 6.0E7D;
      this.borderSafeZone = 5.0D;
      this.borderDamagePerBlock = 0.2D;
      this.borderWarningBlocks = 5;
      this.borderWarningTime = 15;
      this.disabledDataPacks = Sets.newHashSet();
      this.enabledDataPacks = Sets.newLinkedHashSet();
      this.dimensionData = Maps.newIdentityHashMap();
      this.gameRules = new GameRules();
      this.scheduledEvents = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS);
      this.fixerUpper = null;
      this.playerDataVersion = SharedConstants.getCurrentVersion().getWorldVersion();
      this.setLevelSettings(var1);
      this.levelName = var2;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void setLevelSettings(LevelSettings var1) {
      this.seed = var1.getSeed();
      this.gameType = var1.getGameType();
      this.generateMapFeatures = var1.isGenerateMapFeatures();
      this.hardcore = var1.isHardcore();
      this.generator = var1.getLevelType();
      this.setGeneratorOptions((CompoundTag)Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, var1.getLevelTypeOptions()));
      this.allowCommands = var1.getAllowCommands();
   }

   public CompoundTag createTag(@Nullable CompoundTag var1) {
      this.updatePlayerTag();
      if (var1 == null) {
         var1 = this.loadedPlayerTag;
      }

      CompoundTag var2 = new CompoundTag();
      this.setTagData(var2, var1);
      return var2;
   }

   private void setTagData(CompoundTag var1, CompoundTag var2) {
      CompoundTag var3 = new CompoundTag();
      var3.putString("Name", SharedConstants.getCurrentVersion().getName());
      var3.putInt("Id", SharedConstants.getCurrentVersion().getWorldVersion());
      var3.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      var1.put("Version", var3);
      var1.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var1.putLong("RandomSeed", this.seed);
      var1.putString("generatorName", this.generator.getSerialization());
      var1.putInt("generatorVersion", this.generator.getVersion());
      if (!this.generatorOptions.isEmpty()) {
         var1.put("generatorOptions", this.generatorOptions);
      }

      if (this.legacyCustomOptions != null) {
         var1.putString("legacy_custom_options", this.legacyCustomOptions);
      }

      var1.putInt("GameType", this.gameType.getId());
      var1.putBoolean("MapFeatures", this.generateMapFeatures);
      var1.putInt("SpawnX", this.xSpawn);
      var1.putInt("SpawnY", this.ySpawn);
      var1.putInt("SpawnZ", this.zSpawn);
      var1.putLong("Time", this.gameTime);
      var1.putLong("DayTime", this.dayTime);
      var1.putLong("SizeOnDisk", this.sizeOnDisk);
      var1.putLong("LastPlayed", Util.getEpochMillis());
      var1.putString("LevelName", this.levelName);
      var1.putInt("version", this.version);
      var1.putInt("clearWeatherTime", this.clearWeatherTime);
      var1.putInt("rainTime", this.rainTime);
      var1.putBoolean("raining", this.raining);
      var1.putInt("thunderTime", this.thunderTime);
      var1.putBoolean("thundering", this.thundering);
      var1.putBoolean("hardcore", this.hardcore);
      var1.putBoolean("allowCommands", this.allowCommands);
      var1.putBoolean("initialized", this.initialized);
      var1.putDouble("BorderCenterX", this.borderX);
      var1.putDouble("BorderCenterZ", this.borderZ);
      var1.putDouble("BorderSize", this.borderSize);
      var1.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      var1.putDouble("BorderSafeZone", this.borderSafeZone);
      var1.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      var1.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      var1.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
      var1.putDouble("BorderWarningTime", (double)this.borderWarningTime);
      if (this.difficulty != null) {
         var1.putByte("Difficulty", (byte)this.difficulty.getId());
      }

      var1.putBoolean("DifficultyLocked", this.difficultyLocked);
      var1.put("GameRules", this.gameRules.createTag());
      CompoundTag var4 = new CompoundTag();
      Iterator var5 = this.dimensionData.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         var4.put(String.valueOf(((DimensionType)var6.getKey()).getId()), (Tag)var6.getValue());
      }

      var1.put("DimensionData", var4);
      if (var2 != null) {
         var1.put("Player", var2);
      }

      CompoundTag var10 = new CompoundTag();
      ListTag var11 = new ListTag();
      Iterator var7 = this.enabledDataPacks.iterator();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         var11.add(new StringTag(var8));
      }

      var10.put("Enabled", var11);
      ListTag var12 = new ListTag();
      Iterator var13 = this.disabledDataPacks.iterator();

      while(var13.hasNext()) {
         String var9 = (String)var13.next();
         var12.add(new StringTag(var9));
      }

      var10.put("Disabled", var12);
      var1.put("DataPacks", var10);
      if (this.customBossEvents != null) {
         var1.put("CustomBossEvents", this.customBossEvents);
      }

      var1.put("ScheduledEvents", this.scheduledEvents.store());
      var1.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      var1.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         var1.putString("WanderingTraderId", this.wanderingTraderId.toString());
      }

   }

   public long getSeed() {
      return this.seed;
   }

   public int getXSpawn() {
      return this.xSpawn;
   }

   public int getYSpawn() {
      return this.ySpawn;
   }

   public int getZSpawn() {
      return this.zSpawn;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   private void updatePlayerTag() {
      if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
         if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
               throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }

            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
         }

         this.upgradedPlayerTag = true;
      }
   }

   public CompoundTag getLoadedPlayerTag() {
      this.updatePlayerTag();
      return this.loadedPlayerTag;
   }

   public void setXSpawn(int var1) {
      this.xSpawn = var1;
   }

   public void setYSpawn(int var1) {
      this.ySpawn = var1;
   }

   public void setZSpawn(int var1) {
      this.zSpawn = var1;
   }

   public void setGameTime(long var1) {
      this.gameTime = var1;
   }

   public void setDayTime(long var1) {
      this.dayTime = var1;
   }

   public void setSpawn(BlockPos var1) {
      this.xSpawn = var1.getX();
      this.ySpawn = var1.getY();
      this.zSpawn = var1.getZ();
   }

   public String getLevelName() {
      return this.levelName;
   }

   public void setLevelName(String var1) {
      this.levelName = var1;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int var1) {
      this.version = var1;
   }

   public long getLastPlayed() {
      return this.lastPlayed;
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
      return this.gameType;
   }

   public boolean isGenerateMapFeatures() {
      return this.generateMapFeatures;
   }

   public void setGenerateMapFeatures(boolean var1) {
      this.generateMapFeatures = var1;
   }

   public void setGameType(GameType var1) {
      this.gameType = var1;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public void setHardcore(boolean var1) {
      this.hardcore = var1;
   }

   public LevelType getGeneratorType() {
      return this.generator;
   }

   public void setGenerator(LevelType var1) {
      this.generator = var1;
   }

   public CompoundTag getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void setGeneratorOptions(CompoundTag var1) {
      this.generatorOptions = var1;
   }

   public boolean getAllowCommands() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean var1) {
      this.allowCommands = var1;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean var1) {
      this.initialized = var1;
   }

   public GameRules getGameRules() {
      return this.gameRules;
   }

   public double getBorderX() {
      return this.borderX;
   }

   public double getBorderZ() {
      return this.borderZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   public void setBorderSize(double var1) {
      this.borderSize = var1;
   }

   public long getBorderSizeLerpTime() {
      return this.borderSizeLerpTime;
   }

   public void setBorderSizeLerpTime(long var1) {
      this.borderSizeLerpTime = var1;
   }

   public double getBorderSizeLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   public void setBorderSizeLerpTarget(double var1) {
      this.borderSizeLerpTarget = var1;
   }

   public void setBorderZ(double var1) {
      this.borderZ = var1;
   }

   public void setBorderX(double var1) {
      this.borderX = var1;
   }

   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   public void setBorderSafeZone(double var1) {
      this.borderSafeZone = var1;
   }

   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   public void setBorderDamagePerBlock(double var1) {
      this.borderDamagePerBlock = var1;
   }

   public int getBorderWarningBlocks() {
      return this.borderWarningBlocks;
   }

   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   public void setBorderWarningBlocks(int var1) {
      this.borderWarningBlocks = var1;
   }

   public void setBorderWarningTime(int var1) {
      this.borderWarningTime = var1;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(Difficulty var1) {
      this.difficulty = var1;
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

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Level name", () -> {
         return this.levelName;
      });
      var1.setDetail("Level seed", () -> {
         return String.valueOf(this.seed);
      });
      var1.setDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.generator.getId(), this.generator.getName(), this.generator.getVersion(), this.generateMapFeatures);
      });
      var1.setDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      var1.setDetail("Level spawn location", () -> {
         return CrashReportCategory.formatLocation(this.xSpawn, this.ySpawn, this.zSpawn);
      });
      var1.setDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.gameTime, this.dayTime);
      });
      var1.setDetail("Level storage version", () -> {
         String var1 = "Unknown?";

         try {
            switch(this.version) {
            case 19132:
               var1 = "McRegion";
               break;
            case 19133:
               var1 = "Anvil";
            }
         } catch (Throwable var3) {
         }

         return String.format("0x%05X - %s", this.version, var1);
      });
      var1.setDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
      });
      var1.setDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameType.getName(), this.gameType.getId(), this.hardcore, this.allowCommands);
      });
   }

   public CompoundTag getDimensionData(DimensionType var1) {
      CompoundTag var2 = (CompoundTag)this.dimensionData.get(var1);
      return var2 == null ? new CompoundTag() : var2;
   }

   public void setDimensionData(DimensionType var1, CompoundTag var2) {
      this.dimensionData.put(var1, var2);
   }

   public int getMinecraftVersion() {
      return this.minecraftVersion;
   }

   public boolean isSnapshot() {
      return this.snapshot;
   }

   public String getMinecraftVersionName() {
      return this.minecraftVersionName;
   }

   public Set<String> getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set<String> getEnabledDataPacks() {
      return this.enabledDataPacks;
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

   public void setWanderingTraderId(UUID var1) {
      this.wanderingTraderId = var1;
   }

   static {
      DEFAULT_DIFFICULTY = Difficulty.NORMAL;
   }
}
