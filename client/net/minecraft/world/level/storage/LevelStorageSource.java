package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.level.validation.PathAllowList;
import org.slf4j.Logger;

public class LevelStorageSource {
   static final Logger LOGGER = LogUtils.getLogger();
   static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
      .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
      .appendLiteral('-')
      .appendValue(ChronoField.MONTH_OF_YEAR, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.DAY_OF_MONTH, 2)
      .appendLiteral('_')
      .appendValue(ChronoField.HOUR_OF_DAY, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
      .toFormatter();
   private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of(
      "RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest"
   );
   private static final String TAG_DATA = "Data";
   private static final PathAllowList NO_SYMLINKS_ALLOWED = new PathAllowList(List.of());
   public static final String ALLOWED_SYMLINKS_CONFIG_NAME = "allowed_symlinks.txt";
   private final Path baseDir;
   private final Path backupDir;
   final DataFixer fixerUpper;
   private final DirectoryValidator worldDirValidator;

   public LevelStorageSource(Path var1, Path var2, DirectoryValidator var3, DataFixer var4) {
      super();
      this.fixerUpper = var4;

      try {
         FileUtil.createDirectoriesSafe(var1);
      } catch (IOException var6) {
         throw new UncheckedIOException(var6);
      }

      this.baseDir = var1;
      this.backupDir = var2;
      this.worldDirValidator = var3;
   }

   public static DirectoryValidator parseValidator(Path var0) {
      if (Files.exists(var0)) {
         try {
            DirectoryValidator var2;
            try (BufferedReader var1 = Files.newBufferedReader(var0)) {
               var2 = new DirectoryValidator(PathAllowList.readPlain(var1));
            }

            return var2;
         } catch (Exception var6) {
            LOGGER.error("Failed to parse {}, disallowing all symbolic links", "allowed_symlinks.txt", var6);
         }
      }

      return new DirectoryValidator(NO_SYMLINKS_ALLOWED);
   }

   public static LevelStorageSource createDefault(Path var0) {
      DirectoryValidator var1 = parseValidator(var0.resolve("allowed_symlinks.txt"));
      return new LevelStorageSource(var0, var0.resolve("../backups"), var1, DataFixers.getDataFixer());
   }

   private static <T> DataResult<WorldGenSettings> readWorldGenSettings(Dynamic<T> var0, DataFixer var1, int var2) {
      Dynamic var3 = var0.get("WorldGenSettings").orElseEmptyMap();
      UnmodifiableIterator var4 = OLD_SETTINGS_KEYS.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         Optional var6 = var0.get(var5).result();
         if (var6.isPresent()) {
            var3 = var3.set(var5, (Dynamic)var6.get());
         }
      }

      Dynamic var7 = DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion(var1, var3, var2);
      return WorldGenSettings.CODEC.parse(var7);
   }

   private static WorldDataConfiguration readDataConfig(Dynamic<?> var0) {
      return WorldDataConfiguration.CODEC.parse(var0).resultOrPartial(LOGGER::error).orElse(WorldDataConfiguration.DEFAULT);
   }

   public String getName() {
      return "Anvil";
   }

   public LevelStorageSource.LevelCandidates findLevelCandidates() throws LevelStorageException {
      if (!Files.isDirectory(this.baseDir)) {
         throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
      } else {
         try {
            LevelStorageSource.LevelCandidates var3;
            try (Stream var1 = Files.list(this.baseDir)) {
               List var2 = var1.filter(var0 -> Files.isDirectory(var0))
                  .map(LevelStorageSource.LevelDirectory::new)
                  .filter(var0 -> Files.isRegularFile(var0.dataFile()) || Files.isRegularFile(var0.oldDataFile()))
                  .toList();
               var3 = new LevelStorageSource.LevelCandidates(var2);
            }

            return var3;
         } catch (IOException var6) {
            throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
         }
      }
   }

   public CompletableFuture<List<LevelSummary>> loadLevelSummaries(LevelStorageSource.LevelCandidates var1) {
      ArrayList var2 = new ArrayList(var1.levels.size());

      for(LevelStorageSource.LevelDirectory var4 : var1.levels) {
         var2.add(
            CompletableFuture.supplyAsync(
               () -> {
                  boolean var2x;
                  try {
                     var2x = DirectoryLock.isLocked(var4.path());
                  } catch (Exception var6) {
                     LOGGER.warn("Failed to read {} lock", var4.path(), var6);
                     return null;
                  }
      
                  try {
                     LevelSummary var3 = this.readLevelData(var4, this.levelSummaryReader(var4, var2x));
                     return var3 != null ? var3 : null;
                  } catch (OutOfMemoryError var4x) {
                     MemoryReserve.release();
                     System.gc();
                     LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", var4.directoryName());
                     throw var4x;
                  } catch (StackOverflowError var5) {
                     LOGGER.error(
                        LogUtils.FATAL_MARKER,
                        "Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.",
                        var4.directoryName()
                     );
                     Util.safeReplaceOrMoveFile(var4.dataFile(), var4.oldDataFile(), var4.corruptedDataFile(LocalDateTime.now()), true);
                     throw var5;
                  }
               },
               Util.backgroundExecutor()
            )
         );
      }

      return Util.sequenceFailFastAndCancel(var2).thenApply(var0 -> var0.stream().filter(Objects::nonNull).sorted().toList());
   }

   private int getStorageVersion() {
      return 19133;
   }

   @Nullable
   <T> T readLevelData(LevelStorageSource.LevelDirectory var1, BiFunction<Path, DataFixer, T> var2) {
      if (!Files.exists(var1.path())) {
         return null;
      } else {
         Path var3 = var1.dataFile();
         if (Files.exists(var3)) {
            Object var4 = var2.apply(var3, this.fixerUpper);
            if (var4 != null) {
               return (T)var4;
            }
         }

         var3 = var1.oldDataFile();
         return (T)(Files.exists(var3) ? var2.apply(var3, this.fixerUpper) : null);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   private static WorldDataConfiguration getDataConfiguration(Path var0, DataFixer var1) {
      try {
         Tag var2 = readLightweightData(var0);
         if (var2 instanceof CompoundTag var3) {
            CompoundTag var4 = var3.getCompound("Data");
            int var5 = NbtUtils.getDataVersion(var4, -1);
            Dynamic var6 = DataFixTypes.LEVEL.updateToCurrentVersion(var1, new Dynamic(NbtOps.INSTANCE, var4), var5);
            return readDataConfig(var6);
         }
      } catch (Exception var7) {
         LOGGER.error("Exception reading {}", var0, var7);
      }

      return null;
   }

   static BiFunction<Path, DataFixer, Pair<WorldData, WorldDimensions.Complete>> getLevelData(
      DynamicOps<Tag> var0, WorldDataConfiguration var1, Registry<LevelStem> var2, Lifecycle var3
   ) {
      return (var4, var5) -> {
         CompoundTag var6;
         try {
            var6 = NbtIo.readCompressed(var4.toFile());
         } catch (IOException var17) {
            throw new UncheckedIOException(var17);
         }

         CompoundTag var7 = var6.getCompound("Data");
         CompoundTag var8 = var7.contains("Player", 10) ? var7.getCompound("Player") : null;
         var7.remove("Player");
         int var9 = NbtUtils.getDataVersion(var7, -1);
         Dynamic var10 = DataFixTypes.LEVEL.updateToCurrentVersion(var5, new Dynamic(var0, var7), var9);
         WorldGenSettings var11 = (WorldGenSettings)readWorldGenSettings(var10, var5, var9)
            .getOrThrow(false, Util.prefix("WorldGenSettings: ", LOGGER::error));
         LevelVersion var12 = LevelVersion.parse(var10);
         LevelSettings var13 = LevelSettings.parse(var10, var1);
         WorldDimensions.Complete var14 = var11.dimensions().bake(var2);
         Lifecycle var15 = var14.lifecycle().add(var3);
         PrimaryLevelData var16 = PrimaryLevelData.parse(var10, var5, var9, var8, var13, var12, var14.specialWorldProperty(), var11.options(), var15);
         return Pair.of(var16, var14);
      };
   }

   BiFunction<Path, DataFixer, LevelSummary> levelSummaryReader(LevelStorageSource.LevelDirectory var1, boolean var2) {
      return (var3, var4) -> {
         try {
            if (Files.isSymbolicLink(var3)) {
               ArrayList var5 = new ArrayList();
               this.worldDirValidator.validateSymlink(var3, var5);
               if (!var5.isEmpty()) {
                  LOGGER.warn(ContentValidationException.getMessage(var3, var5));
                  return new LevelSummary.SymlinkLevelSummary(var1.directoryName(), var1.iconFile());
               }
            }

            Tag var19 = readLightweightData(var3);
            if (var19 instanceof CompoundTag var6) {
               CompoundTag var7 = var6.getCompound("Data");
               int var8 = NbtUtils.getDataVersion(var7, -1);
               Dynamic var9 = DataFixTypes.LEVEL.updateToCurrentVersion(var4, new Dynamic(NbtOps.INSTANCE, var7), var8);
               LevelVersion var10 = LevelVersion.parse(var9);
               int var11 = var10.levelDataVersion();
               if (var11 == 19132 || var11 == 19133) {
                  boolean var12 = var11 != this.getStorageVersion();
                  Path var13 = var1.iconFile();
                  WorldDataConfiguration var14 = readDataConfig(var9);
                  LevelSettings var15 = LevelSettings.parse(var9, var14);
                  FeatureFlagSet var16 = parseFeatureFlagsFromSummary(var9);
                  boolean var17 = FeatureFlags.isExperimental(var16);
                  return new LevelSummary(var15, var10, var1.directoryName(), var12, var2, var17, var13);
               }
            } else {
               LOGGER.warn("Invalid root tag in {}", var3);
            }

            return null;
         } catch (Exception var18) {
            LOGGER.error("Exception reading {}", var3, var18);
            return null;
         }
      };
   }

   private static FeatureFlagSet parseFeatureFlagsFromSummary(Dynamic<?> var0) {
      Set var1 = var0.get("enabled_features")
         .asStream()
         .flatMap(var0x -> var0x.asString().result().map(ResourceLocation::tryParse).stream())
         .collect(Collectors.toSet());
      return FeatureFlags.REGISTRY.fromNames(var1, var0x -> {
      });
   }

   @Nullable
   private static Tag readLightweightData(Path var0) throws IOException {
      SkipFields var1 = new SkipFields(new FieldSelector("Data", CompoundTag.TYPE, "Player"), new FieldSelector("Data", CompoundTag.TYPE, "WorldGenSettings"));
      NbtIo.parseCompressed(var0.toFile(), var1);
      return var1.getResult();
   }

   public boolean isNewLevelIdAcceptable(String var1) {
      try {
         Path var2 = this.getLevelPath(var1);
         Files.createDirectory(var2);
         Files.deleteIfExists(var2);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public boolean levelExists(String var1) {
      return Files.isDirectory(this.getLevelPath(var1));
   }

   private Path getLevelPath(String var1) {
      return this.baseDir.resolve(var1);
   }

   public Path getBaseDir() {
      return this.baseDir;
   }

   public Path getBackupPath() {
      return this.backupDir;
   }

   public LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String var1) throws IOException, ContentValidationException {
      Path var2 = this.getLevelPath(var1);
      List var3 = this.worldDirValidator.validateSave(var2, true);
      if (!var3.isEmpty()) {
         throw new ContentValidationException(var2, var3);
      } else {
         return new LevelStorageSource.LevelStorageAccess(var1, var2);
      }
   }

   public LevelStorageSource.LevelStorageAccess createAccess(String var1) throws IOException {
      Path var2 = this.getLevelPath(var1);
      return new LevelStorageSource.LevelStorageAccess(var1, var2);
   }

   public DirectoryValidator getWorldDirValidator() {
      return this.worldDirValidator;
   }

   public static record LevelCandidates(List<LevelStorageSource.LevelDirectory> a) implements Iterable<LevelStorageSource.LevelDirectory> {
      final List<LevelStorageSource.LevelDirectory> levels;

      public LevelCandidates(List<LevelStorageSource.LevelDirectory> var1) {
         super();
         this.levels = var1;
      }

      public boolean isEmpty() {
         return this.levels.isEmpty();
      }

      @Override
      public Iterator<LevelStorageSource.LevelDirectory> iterator() {
         return this.levels.iterator();
      }
   }

   public static record LevelDirectory(Path a) {
      private final Path path;

      public LevelDirectory(Path var1) {
         super();
         this.path = var1;
      }

      public String directoryName() {
         return this.path.getFileName().toString();
      }

      public Path dataFile() {
         return this.resourcePath(LevelResource.LEVEL_DATA_FILE);
      }

      public Path oldDataFile() {
         return this.resourcePath(LevelResource.OLD_LEVEL_DATA_FILE);
      }

      public Path corruptedDataFile(LocalDateTime var1) {
         return this.path.resolve(LevelResource.LEVEL_DATA_FILE.getId() + "_corrupted_" + var1.format(LevelStorageSource.FORMATTER));
      }

      public Path iconFile() {
         return this.resourcePath(LevelResource.ICON_FILE);
      }

      public Path lockFile() {
         return this.resourcePath(LevelResource.LOCK_FILE);
      }

      public Path resourcePath(LevelResource var1) {
         return this.path.resolve(var1.getId());
      }
   }

   public class LevelStorageAccess implements AutoCloseable {
      final DirectoryLock lock;
      final LevelStorageSource.LevelDirectory levelDirectory;
      private final String levelId;
      private final Map<LevelResource, Path> resources = Maps.newHashMap();

      LevelStorageAccess(String var2, Path var3) throws IOException {
         super();
         this.levelId = var2;
         this.levelDirectory = new LevelStorageSource.LevelDirectory(var3);
         this.lock = DirectoryLock.create(var3);
      }

      public String getLevelId() {
         return this.levelId;
      }

      public Path getLevelPath(LevelResource var1) {
         return this.resources.computeIfAbsent(var1, this.levelDirectory::resourcePath);
      }

      public Path getDimensionPath(ResourceKey<Level> var1) {
         return DimensionType.getStorageFolder(var1, this.levelDirectory.path());
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

      @Nullable
      public LevelSummary getSummary() {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource.this.levelSummaryReader(this.levelDirectory, false));
      }

      @Nullable
      public Pair<WorldData, WorldDimensions.Complete> getDataTag(DynamicOps<Tag> var1, WorldDataConfiguration var2, Registry<LevelStem> var3, Lifecycle var4) {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource.getLevelData(var1, var2, var3, var4));
      }

      @Nullable
      public WorldDataConfiguration getDataConfiguration() {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource::getDataConfiguration);
      }

      public void saveDataTag(RegistryAccess var1, WorldData var2) {
         this.saveDataTag(var1, var2, null);
      }

      public void saveDataTag(RegistryAccess var1, WorldData var2, @Nullable CompoundTag var3) {
         File var4 = this.levelDirectory.path().toFile();
         CompoundTag var5 = var2.createTag(var1, var3);
         CompoundTag var6 = new CompoundTag();
         var6.put("Data", var5);

         try {
            File var7 = File.createTempFile("level", ".dat", var4);
            NbtIo.writeCompressed(var6, var7);
            File var8 = this.levelDirectory.oldDataFile().toFile();
            File var9 = this.levelDirectory.dataFile().toFile();
            Util.safeReplaceFile(var9, var7, var8);
         } catch (Exception var10) {
            LevelStorageSource.LOGGER.error("Failed to save level {}", var4, var10);
         }
      }

      public Optional<Path> getIconFile() {
         return !this.lock.isValid() ? Optional.empty() : Optional.of(this.levelDirectory.iconFile());
      }

      public void deleteLevel() throws IOException {
         this.checkLock();
         final Path var1 = this.levelDirectory.lockFile();
         LevelStorageSource.LOGGER.info("Deleting level {}", this.levelId);

         for(int var2 = 1; var2 <= 5; ++var2) {
            LevelStorageSource.LOGGER.info("Attempt {}...", var2);

            try {
               Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
                  public FileVisitResult visitFile(Path var1x, BasicFileAttributes var2) throws IOException {
                     if (!var1x.equals(var1)) {
                        LevelStorageSource.LOGGER.debug("Deleting {}", var1x);
                        Files.delete(var1x);
                     }

                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(Path var1x, @Nullable IOException var2) throws IOException {
                     if (var2 != null) {
                        throw var2;
                     } else {
                        if (var1x.equals(LevelStorageAccess.this.levelDirectory.path())) {
                           LevelStorageAccess.this.lock.close();
                           Files.deleteIfExists(var1);
                        }

                        Files.delete(var1x);
                        return FileVisitResult.CONTINUE;
                     }
                  }
               });
               break;
            } catch (IOException var6) {
               if (var2 >= 5) {
                  throw var6;
               }

               LevelStorageSource.LOGGER.warn("Failed to delete {}", this.levelDirectory.path(), var6);

               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }
      }

      public void renameLevel(String var1) throws IOException {
         this.checkLock();
         Path var2 = this.levelDirectory.dataFile();
         if (Files.exists(var2)) {
            CompoundTag var3 = NbtIo.readCompressed(var2.toFile());
            CompoundTag var4 = var3.getCompound("Data");
            var4.putString("LevelName", var1);
            NbtIo.writeCompressed(var3, var2.toFile());
         }
      }

      public long makeWorldBackup() throws IOException {
         this.checkLock();
         String var1 = LocalDateTime.now().format(LevelStorageSource.FORMATTER) + "_" + this.levelId;
         Path var2 = LevelStorageSource.this.getBackupPath();

         try {
            FileUtil.createDirectoriesSafe(var2);
         } catch (IOException var9) {
            throw new RuntimeException(var9);
         }

         Path var3 = var2.resolve(FileUtil.findAvailableName(var2, var1, ".zip"));

         try (final ZipOutputStream var4 = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(var3)))) {
            final Path var5 = Paths.get(this.levelId);
            Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
               public FileVisitResult visitFile(Path var1, BasicFileAttributes var2) throws IOException {
                  if (var1.endsWith("session.lock")) {
                     return FileVisitResult.CONTINUE;
                  } else {
                     String var3 = var5.resolve(LevelStorageAccess.this.levelDirectory.path().relativize(var1)).toString().replace('\\', '/');
                     ZipEntry var4x = new ZipEntry(var3);
                     var4.putNextEntry(var4x);
                     com.google.common.io.Files.asByteSource(var1.toFile()).copyTo(var4);
                     var4.closeEntry();
                     return FileVisitResult.CONTINUE;
                  }
               }
            });
         }

         return Files.size(var3);
      }

      @Override
      public void close() throws IOException {
         this.lock.close();
      }
   }
}
