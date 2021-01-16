package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorageSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateTimeFormatter FORMATTER;
   private static final ImmutableList<String> OLD_SETTINGS_KEYS;
   private final Path baseDir;
   private final Path backupDir;
   private final DataFixer fixerUpper;

   public LevelStorageSource(Path var1, Path var2, DataFixer var3) {
      super();
      this.fixerUpper = var3;

      try {
         Files.createDirectories(Files.exists(var1, new LinkOption[0]) ? var1.toRealPath() : var1);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.baseDir = var1;
      this.backupDir = var2;
   }

   public static LevelStorageSource createDefault(Path var0) {
      return new LevelStorageSource(var0, var0.resolve("../backups"), DataFixers.getDataFixer());
   }

   private static <T> Pair<WorldGenSettings, Lifecycle> readWorldGenSettings(Dynamic<T> var0, DataFixer var1, int var2) {
      Dynamic var3 = var0.get("WorldGenSettings").orElseEmptyMap();
      UnmodifiableIterator var4 = OLD_SETTINGS_KEYS.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         Optional var6 = var0.get(var5).result();
         if (var6.isPresent()) {
            var3 = var3.set(var5, (Dynamic)var6.get());
         }
      }

      Dynamic var8 = var1.update(References.WORLD_GEN_SETTINGS, var3, var2, SharedConstants.getCurrentVersion().getWorldVersion());
      DataResult var7 = WorldGenSettings.CODEC.parse(var8);
      Logger var10002 = LOGGER;
      var10002.getClass();
      return Pair.of(var7.resultOrPartial(Util.prefix("WorldGenSettings: ", var10002::error)).orElseGet(() -> {
         DataResult var10000 = RegistryLookupCodec.create(Registry.DIMENSION_TYPE_REGISTRY).codec().parse(var8);
         Logger var10002 = LOGGER;
         var10002.getClass();
         Registry var1 = (Registry)var10000.resultOrPartial(Util.prefix("Dimension type registry: ", var10002::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get dimension registry");
         });
         var10000 = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).codec().parse(var8);
         var10002 = LOGGER;
         var10002.getClass();
         Registry var2 = (Registry)var10000.resultOrPartial(Util.prefix("Biome registry: ", var10002::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get biome registry");
         });
         var10000 = RegistryLookupCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).codec().parse(var8);
         var10002 = LOGGER;
         var10002.getClass();
         Registry var3 = (Registry)var10000.resultOrPartial(Util.prefix("Noise settings registry: ", var10002::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get noise settings registry");
         });
         return WorldGenSettings.makeDefault(var1, var2, var3);
      }), var7.lifecycle());
   }

   private static DataPackConfig readDataPackConfig(Dynamic<?> var0) {
      DataResult var10000 = DataPackConfig.CODEC.parse(var0);
      Logger var10001 = LOGGER;
      var10001.getClass();
      return (DataPackConfig)var10000.resultOrPartial(var10001::error).orElse(DataPackConfig.DEFAULT);
   }

   private int getStorageVersion() {
      return 19133;
   }

   @Nullable
   private <T> T readLevelData(File var1, BiFunction<File, DataFixer, T> var2) {
      if (!var1.exists()) {
         return null;
      } else {
         File var3 = new File(var1, "level.dat");
         if (var3.exists()) {
            Object var4 = var2.apply(var3, this.fixerUpper);
            if (var4 != null) {
               return var4;
            }
         }

         var3 = new File(var1, "level.dat_old");
         return var3.exists() ? var2.apply(var3, this.fixerUpper) : null;
      }
   }

   @Nullable
   private static DataPackConfig getDataPacks(File var0, DataFixer var1) {
      try {
         CompoundTag var2 = NbtIo.readCompressed(var0);
         CompoundTag var3 = var2.getCompound("Data");
         var3.remove("Player");
         int var4 = var3.contains("DataVersion", 99) ? var3.getInt("DataVersion") : -1;
         Dynamic var5 = var1.update(DataFixTypes.LEVEL.getType(), new Dynamic(NbtOps.INSTANCE, var3), var4, SharedConstants.getCurrentVersion().getWorldVersion());
         return (DataPackConfig)var5.get("DataPacks").result().map(LevelStorageSource::readDataPackConfig).orElse(DataPackConfig.DEFAULT);
      } catch (Exception var6) {
         LOGGER.error((String)"Exception reading {}", (Object)var0, (Object)var6);
         return null;
      }
   }

   private static BiFunction<File, DataFixer, PrimaryLevelData> getLevelData(DynamicOps<Tag> var0, DataPackConfig var1) {
      return (var2, var3) -> {
         try {
            CompoundTag var4 = NbtIo.readCompressed(var2);
            CompoundTag var5 = var4.getCompound("Data");
            CompoundTag var6 = var5.contains("Player", 10) ? var5.getCompound("Player") : null;
            var5.remove("Player");
            int var7 = var5.contains("DataVersion", 99) ? var5.getInt("DataVersion") : -1;
            Dynamic var8 = var3.update(DataFixTypes.LEVEL.getType(), new Dynamic(var0, var5), var7, SharedConstants.getCurrentVersion().getWorldVersion());
            Pair var9 = readWorldGenSettings(var8, var3, var7);
            LevelVersion var10 = LevelVersion.parse(var8);
            LevelSettings var11 = LevelSettings.parse(var8, var1);
            return PrimaryLevelData.parse(var8, var3, var7, var6, var11, var10, (WorldGenSettings)var9.getFirst(), (Lifecycle)var9.getSecond());
         } catch (Exception var12) {
            LOGGER.error((String)"Exception reading {}", (Object)var2, (Object)var12);
            return null;
         }
      };
   }

   private BiFunction<File, DataFixer, LevelSummary> levelSummaryReader(File var1, boolean var2) {
      return (var3, var4) -> {
         try {
            CompoundTag var5 = NbtIo.readCompressed(var3);
            CompoundTag var6 = var5.getCompound("Data");
            var6.remove("Player");
            int var7 = var6.contains("DataVersion", 99) ? var6.getInt("DataVersion") : -1;
            Dynamic var8 = var4.update(DataFixTypes.LEVEL.getType(), new Dynamic(NbtOps.INSTANCE, var6), var7, SharedConstants.getCurrentVersion().getWorldVersion());
            LevelVersion var9 = LevelVersion.parse(var8);
            int var10 = var9.levelDataVersion();
            if (var10 != 19132 && var10 != 19133) {
               return null;
            } else {
               boolean var11 = var10 != this.getStorageVersion();
               File var12 = new File(var1, "icon.png");
               DataPackConfig var13 = (DataPackConfig)var8.get("DataPacks").result().map(LevelStorageSource::readDataPackConfig).orElse(DataPackConfig.DEFAULT);
               LevelSettings var14 = LevelSettings.parse(var8, var13);
               return new LevelSummary(var14, var9, var1.getName(), var11, var2, var12);
            }
         } catch (Exception var15) {
            LOGGER.error((String)"Exception reading {}", (Object)var3, (Object)var15);
            return null;
         }
      };
   }

   public LevelStorageSource.LevelStorageAccess createAccess(String var1) throws IOException {
      return new LevelStorageSource.LevelStorageAccess(var1);
   }

   static {
      FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
      OLD_SETTINGS_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
   }

   public class LevelStorageAccess implements AutoCloseable {
      private final DirectoryLock lock;
      private final Path levelPath;
      private final String levelId;
      private final Map<LevelResource, Path> resources = Maps.newHashMap();

      public LevelStorageAccess(String var2) throws IOException {
         super();
         this.levelId = var2;
         this.levelPath = LevelStorageSource.this.baseDir.resolve(var2);
         this.lock = DirectoryLock.create(this.levelPath);
      }

      public String getLevelId() {
         return this.levelId;
      }

      public Path getLevelPath(LevelResource var1) {
         return (Path)this.resources.computeIfAbsent(var1, (var1x) -> {
            return this.levelPath.resolve(var1x.getId());
         });
      }

      public File getDimensionPath(ResourceKey<Level> var1) {
         return DimensionType.getStorageFolder(var1, this.levelPath.toFile());
      }

      private void checkLock() {
         if (!this.lock.isValid()) {
            throw new IllegalStateException("Lock is no longer valid");
         }
      }

      public PlayerDataStorage createPlayerStorage() {
         this.checkLock();
         return new PlayerDataStorage(this, LevelStorageSource.this.fixerUpper);
      }

      public boolean requiresConversion() {
         LevelSummary var1 = this.getSummary();
         return var1 != null && var1.levelVersion().levelDataVersion() != LevelStorageSource.this.getStorageVersion();
      }

      public boolean convertLevel(ProgressListener var1) {
         this.checkLock();
         return McRegionUpgrader.convertLevel(this, var1);
      }

      @Nullable
      public LevelSummary getSummary() {
         this.checkLock();
         return (LevelSummary)LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource.this.levelSummaryReader(this.levelPath.toFile(), false));
      }

      @Nullable
      public WorldData getDataTag(DynamicOps<Tag> var1, DataPackConfig var2) {
         this.checkLock();
         return (WorldData)LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource.getLevelData(var1, var2));
      }

      @Nullable
      public DataPackConfig getDataPacks() {
         this.checkLock();
         return (DataPackConfig)LevelStorageSource.this.readLevelData(this.levelPath.toFile(), (var0, var1) -> {
            return LevelStorageSource.getDataPacks(var0, var1);
         });
      }

      public void saveDataTag(RegistryAccess var1, WorldData var2) {
         this.saveDataTag(var1, var2, (CompoundTag)null);
      }

      public void saveDataTag(RegistryAccess var1, WorldData var2, @Nullable CompoundTag var3) {
         File var4 = this.levelPath.toFile();
         CompoundTag var5 = var2.createTag(var1, var3);
         CompoundTag var6 = new CompoundTag();
         var6.put("Data", var5);

         try {
            File var7 = File.createTempFile("level", ".dat", var4);
            NbtIo.writeCompressed(var6, var7);
            File var8 = new File(var4, "level.dat_old");
            File var9 = new File(var4, "level.dat");
            Util.safeReplaceFile(var9, var7, var8);
         } catch (Exception var10) {
            LevelStorageSource.LOGGER.error((String)"Failed to save level {}", (Object)var4, (Object)var10);
         }

      }

      public File getIconFile() {
         this.checkLock();
         return this.levelPath.resolve("icon.png").toFile();
      }

      public void close() throws IOException {
         this.lock.close();
      }
   }
}
