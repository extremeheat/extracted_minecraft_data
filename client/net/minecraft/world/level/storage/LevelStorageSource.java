package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtFormatException;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
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
   static final DateTimeFormatter FORMATTER = FileNameDateFormatter.create();
   private static final String TAG_DATA = "Data";
   private static final PathMatcher NO_SYMLINKS_ALLOWED = var0 -> false;
   public static final String ALLOWED_SYMLINKS_CONFIG_NAME = "allowed_symlinks.txt";
   private static final int UNCOMPRESSED_NBT_QUOTA = 104857600;
   private static final int DISK_SPACE_WARNING_THRESHOLD = 67108864;
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

   public static WorldDataConfiguration readDataConfig(Dynamic<?> var0) {
      return (WorldDataConfiguration)WorldDataConfiguration.CODEC.parse(var0).resultOrPartial(LOGGER::error).orElse(WorldDataConfiguration.DEFAULT);
   }

   public static WorldLoader.PackConfig getPackConfig(Dynamic<?> var0, PackRepository var1, boolean var2) {
      return new WorldLoader.PackConfig(var1, readDataConfig(var0), var2, false);
   }

   public static LevelDataAndDimensions getLevelDataAndDimensions(
      Dynamic<?> var0, WorldDataConfiguration var1, Registry<LevelStem> var2, RegistryAccess.Frozen var3
   ) {
      Dynamic var4 = RegistryOps.injectRegistryContext(var0, var3);
      Dynamic var5 = var4.get("WorldGenSettings").orElseEmptyMap();
      WorldGenSettings var6 = (WorldGenSettings)WorldGenSettings.CODEC.parse(var5).getOrThrow(false, Util.prefix("WorldGenSettings: ", LOGGER::error));
      LevelSettings var7 = LevelSettings.parse(var4, var1);
      WorldDimensions.Complete var8 = var6.dimensions().bake(var2);
      Lifecycle var9 = var8.lifecycle().add(var3.allRegistriesLifecycle());
      PrimaryLevelData var10 = PrimaryLevelData.parse(var4, var7, var8.specialWorldProperty(), var6.options(), var9);
      return new LevelDataAndDimensions(var10, var8);
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
         var2.add(CompletableFuture.supplyAsync(() -> {
            boolean var2x;
            try {
               var2x = DirectoryLock.isLocked(var4.path());
            } catch (Exception var13) {
               LOGGER.warn("Failed to read {} lock", var4.path(), var13);
               return null;
            }

            try {
               return this.readLevelSummary(var4, var2x);
            } catch (OutOfMemoryError var12) {
               MemoryReserve.release();
               System.gc();
               String var4xx = "Ran out of memory trying to read summary of world folder \"" + var4.directoryName() + "\"";
               LOGGER.error(LogUtils.FATAL_MARKER, var4xx);
               OutOfMemoryError var5 = new OutOfMemoryError("Ran out of memory reading level data");
               var5.initCause(var12);
               CrashReport var6 = CrashReport.forThrowable(var5, var4xx);
               CrashReportCategory var7 = var6.addCategory("World details");
               var7.setDetail("Folder Name", var4.directoryName());

               try {
                  long var8 = Files.size(var4.dataFile());
                  var7.setDetail("level.dat size", var8);
               } catch (IOException var11) {
                  var7.setDetailError("level.dat size", var11);
               }

               throw new ReportedException(var6);
            }
         }, Util.backgroundExecutor()));
      }

      return Util.sequenceFailFastAndCancel(var2).thenApply(var0 -> var0.stream().filter(Objects::nonNull).sorted().toList());
   }

   private int getStorageVersion() {
      return 19133;
   }

   static CompoundTag readLevelDataTagRaw(Path var0) throws IOException {
      return NbtIo.readCompressed(var0, NbtAccounter.create(104857600L));
   }

   static Dynamic<?> readLevelDataTagFixed(Path var0, DataFixer var1) throws IOException {
      CompoundTag var2 = readLevelDataTagRaw(var0);
      CompoundTag var3 = var2.getCompound("Data");
      int var4 = NbtUtils.getDataVersion(var3, -1);
      Dynamic var5 = DataFixTypes.LEVEL.updateToCurrentVersion(var1, new Dynamic(NbtOps.INSTANCE, var3), var4);
      Dynamic var6 = var5.get("Player").orElseEmptyMap();
      Dynamic var7 = DataFixTypes.PLAYER.updateToCurrentVersion(var1, var6, var4);
      var5 = var5.set("Player", var7);
      Dynamic var8 = var5.get("WorldGenSettings").orElseEmptyMap();
      Dynamic var9 = DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion(var1, var8, var4);
      return var5.set("WorldGenSettings", var9);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private LevelSummary readLevelSummary(LevelStorageSource.LevelDirectory var1, boolean var2) {
      Path var3 = var1.dataFile();
      if (Files.exists(var3)) {
         try {
            if (Files.isSymbolicLink(var3)) {
               List var4 = this.worldDirValidator.validateSymlink(var3);
               if (!var4.isEmpty()) {
                  LOGGER.warn("{}", ContentValidationException.getMessage(var3, var4));
                  return new LevelSummary.SymlinkLevelSummary(var1.directoryName(), var1.iconFile());
               }
            }

            Tag var10 = readLightweightData(var3);
            if (var10 instanceof CompoundTag var5) {
               CompoundTag var6 = var5.getCompound("Data");
               int var7 = NbtUtils.getDataVersion(var6, -1);
               Dynamic var8 = DataFixTypes.LEVEL.updateToCurrentVersion(this.fixerUpper, new Dynamic(NbtOps.INSTANCE, var6), var7);
               return this.makeLevelSummary(var8, var1, var2);
            }

            LOGGER.warn("Invalid root tag in {}", var3);
         } catch (Exception var9) {
            LOGGER.error("Exception reading {}", var3, var9);
         }
      }

      return new LevelSummary.CorruptedLevelSummary(var1.directoryName(), var1.iconFile(), getFileModificationTime(var1));
   }

   private static long getFileModificationTime(LevelStorageSource.LevelDirectory var0) {
      Instant var1 = getFileModificationTime(var0.dataFile());
      if (var1 == null) {
         var1 = getFileModificationTime(var0.oldDataFile());
      }

      return var1 == null ? -1L : var1.toEpochMilli();
   }

   @Nullable
   static Instant getFileModificationTime(Path var0) {
      try {
         return Files.getLastModifiedTime(var0).toInstant();
      } catch (IOException var2) {
         return null;
      }
   }

   LevelSummary makeLevelSummary(Dynamic<?> var1, LevelStorageSource.LevelDirectory var2, boolean var3) {
      LevelVersion var4 = LevelVersion.parse(var1);
      int var5 = var4.levelDataVersion();
      if (var5 != 19132 && var5 != 19133) {
         throw new NbtFormatException("Unknown data version: " + Integer.toHexString(var5));
      } else {
         boolean var6 = var5 != this.getStorageVersion();
         Path var7 = var2.iconFile();
         WorldDataConfiguration var8 = readDataConfig(var1);
         LevelSettings var9 = LevelSettings.parse(var1, var8);
         FeatureFlagSet var10 = parseFeatureFlagsFromSummary(var1);
         boolean var11 = FeatureFlags.isExperimental(var10);
         return new LevelSummary(var9, var4, var2.directoryName(), var6, var3, var11, var7);
      }
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
      NbtIo.parseCompressed(var0, var1, NbtAccounter.create(104857600L));
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
      try {
         return Files.isDirectory(this.getLevelPath(var1));
      } catch (InvalidPathException var3) {
         return false;
      }
   }

   public Path getLevelPath(String var1) {
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
      List var3 = this.worldDirValidator.validateDirectory(var2, true);
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
      final Path path;

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

      public Path rawDataFile(LocalDateTime var1) {
         return this.path.resolve(LevelResource.LEVEL_DATA_FILE.getId() + "_raw_" + var1.format(LevelStorageSource.FORMATTER));
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

      public long estimateDiskSpace() {
         try {
            return Files.getFileStore(this.levelDirectory.path).getUsableSpace();
         } catch (Exception var2) {
            return 9223372036854775807L;
         }
      }

      public boolean checkForLowDiskSpace() {
         return this.estimateDiskSpace() < 67108864L;
      }

      public void safeClose() {
         try {
            this.close();
         } catch (IOException var2) {
            LevelStorageSource.LOGGER.warn("Failed to unlock access to level {}", this.getLevelId(), var2);
         }
      }

      public LevelStorageSource parent() {
         return LevelStorageSource.this;
      }

      public LevelStorageSource.LevelDirectory getLevelDirectory() {
         return this.levelDirectory;
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

      public LevelSummary getSummary(Dynamic<?> var1) {
         this.checkLock();
         return LevelStorageSource.this.makeLevelSummary(var1, this.levelDirectory, false);
      }

      public Dynamic<?> getDataTag() throws IOException {
         return this.getDataTag(false);
      }

      public Dynamic<?> getDataTagFallback() throws IOException {
         return this.getDataTag(true);
      }

      private Dynamic<?> getDataTag(boolean var1) throws IOException {
         this.checkLock();
         return LevelStorageSource.readLevelDataTagFixed(
            var1 ? this.levelDirectory.oldDataFile() : this.levelDirectory.dataFile(), LevelStorageSource.this.fixerUpper
         );
      }

      public void saveDataTag(RegistryAccess var1, WorldData var2) {
         this.saveDataTag(var1, var2, null);
      }

      public void saveDataTag(RegistryAccess var1, WorldData var2, @Nullable CompoundTag var3) {
         CompoundTag var4 = var2.createTag(var1, var3);
         CompoundTag var5 = new CompoundTag();
         var5.put("Data", var4);
         this.saveLevelData(var5);
      }

      private void saveLevelData(CompoundTag var1) {
         Path var2 = this.levelDirectory.path();

         try {
            Path var3 = Files.createTempFile(var2, "level", ".dat");
            NbtIo.writeCompressed(var1, var3);
            Path var4 = this.levelDirectory.oldDataFile();
            Path var5 = this.levelDirectory.dataFile();
            Util.safeReplaceFile(var5, var3, var4);
         } catch (Exception var6) {
            LevelStorageSource.LOGGER.error("Failed to save level {}", var2, var6);
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
         this.modifyLevelDataWithoutDatafix(var1x -> var1x.putString("LevelName", var1.trim()));
      }

      public void renameAndDropPlayer(String var1) throws IOException {
         this.modifyLevelDataWithoutDatafix(var1x -> {
            var1x.putString("LevelName", var1.trim());
            var1x.remove("Player");
         });
      }

      private void modifyLevelDataWithoutDatafix(Consumer<CompoundTag> var1) throws IOException {
         this.checkLock();
         CompoundTag var2 = LevelStorageSource.readLevelDataTagRaw(this.levelDirectory.dataFile());
         var1.accept(var2.getCompound("Data"));
         this.saveLevelData(var2);
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

      public boolean hasWorldData() {
         return Files.exists(this.levelDirectory.dataFile()) || Files.exists(this.levelDirectory.oldDataFile());
      }

      @Override
      public void close() throws IOException {
         this.lock.close();
      }

      public boolean restoreLevelDataFromOld() {
         return Util.safeReplaceOrMoveFile(
            this.levelDirectory.dataFile(), this.levelDirectory.oldDataFile(), this.levelDirectory.corruptedDataFile(LocalDateTime.now()), true
         );
      }

      @Nullable
      public Instant getFileModificationTime(boolean var1) {
         return LevelStorageSource.getFileModificationTime(var1 ? this.levelDirectory.oldDataFile() : this.levelDirectory.dataFile());
      }
   }
}
