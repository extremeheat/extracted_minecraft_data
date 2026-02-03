package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZonedDateTime;
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
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.FileUtil;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.Util;
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
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import net.minecraft.world.level.validation.PathAllowList;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class LevelStorageSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String TAG_DATA = "Data";
   private static final PathMatcher NO_SYMLINKS_ALLOWED = (path) -> false;
   public static final String ALLOWED_SYMLINKS_CONFIG_NAME = "allowed_symlinks.txt";
   private static final int DISK_SPACE_WARNING_THRESHOLD = 67108864;
   private final Path baseDir;
   private final Path backupDir;
   private final DataFixer fixerUpper;
   private final DirectoryValidator worldDirValidator;

   public LevelStorageSource(final Path baseDir, final Path backupDir, final DirectoryValidator worldDirValidator, final DataFixer fixerUpper) {
      super();
      this.fixerUpper = fixerUpper;

      try {
         FileUtil.createDirectoriesSafe(baseDir);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }

      this.baseDir = baseDir;
      this.backupDir = backupDir;
      this.worldDirValidator = worldDirValidator;
   }

   public static DirectoryValidator parseValidator(final Path configPath) {
      if (Files.exists(configPath, new LinkOption[0])) {
         try {
            BufferedReader reader = Files.newBufferedReader(configPath);

            DirectoryValidator var2;
            try {
               var2 = new DirectoryValidator(PathAllowList.readPlain(reader));
            } catch (Throwable var5) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (reader != null) {
               reader.close();
            }

            return var2;
         } catch (Exception e) {
            LOGGER.error("Failed to parse {}, disallowing all symbolic links", "allowed_symlinks.txt", e);
         }
      }

      return new DirectoryValidator(NO_SYMLINKS_ALLOWED);
   }

   public static LevelStorageSource createDefault(final Path path) {
      DirectoryValidator validator = parseValidator(path.resolve("allowed_symlinks.txt"));
      return new LevelStorageSource(path, path.resolve("../backups"), validator, DataFixers.getDataFixer());
   }

   public static WorldDataConfiguration readDataConfig(final Dynamic<?> levelData) {
      DataResult var10000 = WorldDataConfiguration.CODEC.parse(levelData);
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      return (WorldDataConfiguration)var10000.resultOrPartial(var10001::error).orElse(WorldDataConfiguration.DEFAULT);
   }

   public static WorldLoader.PackConfig getPackConfig(final Dynamic<?> levelDataTag, final PackRepository packRepository, final boolean safeMode) {
      return new WorldLoader.PackConfig(packRepository, readDataConfig(levelDataTag), safeMode, false);
   }

   public static LevelDataAndDimensions getLevelDataAndDimensions(final Dynamic<?> levelDataTag, final WorldDataConfiguration dataConfiguration, final Registry<LevelStem> datapackDimensions, final HolderLookup.Provider registryAccess) {
      Dynamic<?> dataTag = RegistryOps.injectRegistryContext(levelDataTag, registryAccess);
      Dynamic<?> worldGenSettingsTag = dataTag.get("WorldGenSettings").orElseEmptyMap();
      WorldGenSettings worldGenSettings = (WorldGenSettings)WorldGenSettings.CODEC.parse(worldGenSettingsTag).getOrThrow();
      LevelSettings settings = LevelSettings.parse(dataTag, dataConfiguration);
      WorldDimensions.Complete dimensions = worldGenSettings.dimensions().bake(datapackDimensions);
      Lifecycle lifecycle = dimensions.lifecycle().add(registryAccess.allRegistriesLifecycle());
      PrimaryLevelData worldData = PrimaryLevelData.parse(dataTag, settings, dimensions.specialWorldProperty(), worldGenSettings.options(), lifecycle);
      return new LevelDataAndDimensions(worldData, dimensions);
   }

   public String getName() {
      return "Anvil";
   }

   public LevelCandidates findLevelCandidates() throws LevelStorageException {
      if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
         throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
      } else {
         try {
            Stream<Path> paths = Files.list(this.baseDir);

            LevelCandidates var3;
            try {
               List<LevelDirectory> candidates = paths.filter((x$0) -> Files.isDirectory(x$0, new LinkOption[0])).map(LevelDirectory::new).filter((directory) -> Files.isRegularFile(directory.dataFile(), new LinkOption[0]) || Files.isRegularFile(directory.oldDataFile(), new LinkOption[0])).toList();
               var3 = new LevelCandidates(candidates);
            } catch (Throwable var5) {
               if (paths != null) {
                  try {
                     paths.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (paths != null) {
               paths.close();
            }

            return var3;
         } catch (IOException var6) {
            throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
         }
      }
   }

   public CompletableFuture<List<LevelSummary>> loadLevelSummaries(final LevelCandidates candidates) {
      List<CompletableFuture<LevelSummary>> futures = new ArrayList(candidates.levels.size());

      for(LevelDirectory level : candidates.levels) {
         futures.add(CompletableFuture.supplyAsync(() -> {
            boolean locked;
            try {
               locked = DirectoryLock.isLocked(level.path());
            } catch (Exception e) {
               LOGGER.warn("Failed to read {} lock", level.path(), e);
               return null;
            }

            try {
               return this.readLevelSummary(level, locked);
            } catch (OutOfMemoryError e) {
               MemoryReserve.release();
               String detailedMessage = "Ran out of memory trying to read summary of world folder \"" + level.directoryName() + "\"";
               LOGGER.error(LogUtils.FATAL_MARKER, detailedMessage);
               OutOfMemoryError detailedException = new OutOfMemoryError("Ran out of memory reading level data");
               detailedException.initCause(e);
               CrashReport crashReport = CrashReport.forThrowable(detailedException, detailedMessage);
               CrashReportCategory worldDetails = crashReport.addCategory("World details");
               worldDetails.setDetail("Folder Name", level.directoryName());

               try {
                  long size = Files.size(level.dataFile());
                  worldDetails.setDetail("level.dat size", size);
               } catch (IOException ex) {
                  worldDetails.setDetailError("level.dat size", ex);
               }

               throw new ReportedException(crashReport);
            }
         }, Util.backgroundExecutor().forName("loadLevelSummaries")));
      }

      return Util.sequenceFailFastAndCancel(futures).thenApply((levels) -> levels.stream().filter(Objects::nonNull).sorted().toList());
   }

   private int getStorageVersion() {
      return 19133;
   }

   private static CompoundTag readLevelDataTagRaw(final Path dataFile) throws IOException {
      return NbtIo.readCompressed(dataFile, NbtAccounter.uncompressedQuota());
   }

   private static Dynamic<?> readLevelDataTagFixed(final Path dataFile, final DataFixer dataFixer) throws IOException {
      CompoundTag root = readLevelDataTagRaw(dataFile);
      CompoundTag dataTag = root.getCompoundOrEmpty("Data");
      int dataVersion = NbtUtils.getDataVersion(dataTag);
      Dynamic<?> updated = DataFixTypes.LEVEL.updateToCurrentVersion(dataFixer, new Dynamic(NbtOps.INSTANCE, dataTag), dataVersion);
      updated = updated.update("Player", (playerTagUnfixed) -> DataFixTypes.PLAYER.updateToCurrentVersion(dataFixer, playerTagUnfixed, dataVersion));
      updated = updated.update("WorldGenSettings", (worldGenSettingsTagUnfixed) -> DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion(dataFixer, worldGenSettingsTagUnfixed, dataVersion));
      return updated;
   }

   private LevelSummary readLevelSummary(final LevelDirectory level, final boolean locked) {
      Path dataFile = level.dataFile();
      if (Files.exists(dataFile, new LinkOption[0])) {
         try {
            if (Files.isSymbolicLink(dataFile)) {
               List<ForbiddenSymlinkInfo> issues = this.worldDirValidator.validateSymlink(dataFile);
               if (!issues.isEmpty()) {
                  LOGGER.warn("{}", ContentValidationException.getMessage(dataFile, issues));
                  return new LevelSummary.SymlinkLevelSummary(level.directoryName(), level.iconFile());
               }
            }

            Tag result = readLightweightData(dataFile);
            if (result instanceof CompoundTag) {
               CompoundTag root = (CompoundTag)result;
               CompoundTag tag = root.getCompoundOrEmpty("Data");
               int dataVersion = NbtUtils.getDataVersion(tag);
               Dynamic<?> updated = DataFixTypes.LEVEL_SUMMARY.updateToCurrentVersion(this.fixerUpper, new Dynamic(NbtOps.INSTANCE, tag), dataVersion);
               return this.makeLevelSummary(updated, level, locked);
            }

            LOGGER.warn("Invalid root tag in {}", dataFile);
         } catch (Exception e) {
            LOGGER.error("Exception reading {}", dataFile, e);
         }
      }

      return new LevelSummary.CorruptedLevelSummary(level.directoryName(), level.iconFile(), getFileModificationTime(level));
   }

   private static long getFileModificationTime(final LevelDirectory level) {
      Instant timeStamp = getFileModificationTime(level.dataFile());
      if (timeStamp == null) {
         timeStamp = getFileModificationTime(level.oldDataFile());
      }

      return timeStamp == null ? -1L : timeStamp.toEpochMilli();
   }

   private static @Nullable Instant getFileModificationTime(final Path path) {
      try {
         return Files.getLastModifiedTime(path).toInstant();
      } catch (IOException var2) {
         return null;
      }
   }

   private LevelSummary makeLevelSummary(final Dynamic<?> dataTag, final LevelDirectory levelDirectory, final boolean locked) {
      LevelVersion levelVersion = LevelVersion.parse(dataTag);
      int levelDataVersion = levelVersion.levelDataVersion();
      if (levelDataVersion != 19132 && levelDataVersion != 19133) {
         throw new NbtFormatException("Unknown data version: " + Integer.toHexString(levelDataVersion));
      } else {
         boolean requiresManualConversion = levelDataVersion != this.getStorageVersion();
         Path icon = levelDirectory.iconFile();
         WorldDataConfiguration dataConfiguration = readDataConfig(dataTag);
         LevelSettings settings = LevelSettings.parse(dataTag, dataConfiguration);
         FeatureFlagSet enabledFeatureFlags = parseFeatureFlagsFromSummary(dataTag);
         boolean experimental = FeatureFlags.isExperimental(enabledFeatureFlags);
         return new LevelSummary(settings, levelVersion, levelDirectory.directoryName(), requiresManualConversion, locked, experimental, icon);
      }
   }

   private static FeatureFlagSet parseFeatureFlagsFromSummary(final Dynamic<?> tag) {
      Set<Identifier> enabledFlags = (Set)tag.get("enabled_features").asStream().flatMap((entry) -> entry.asString().result().map(Identifier::tryParse).stream()).collect(Collectors.toSet());
      return FeatureFlags.REGISTRY.fromNames(enabledFlags, (unknownId) -> {
      });
   }

   private static @Nullable Tag readLightweightData(final Path dataFile) throws IOException {
      SkipFields parser = new SkipFields(new FieldSelector[]{new FieldSelector("Data", CompoundTag.TYPE, "Player"), new FieldSelector("Data", CompoundTag.TYPE, "WorldGenSettings")});
      NbtIo.parseCompressed((Path)dataFile, parser, NbtAccounter.uncompressedQuota());
      return parser.getResult();
   }

   public boolean isNewLevelIdAcceptable(final String levelId) {
      try {
         Path fullPath = this.getLevelPath(levelId);
         Files.createDirectory(fullPath);
         Files.deleteIfExists(fullPath);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public boolean levelExists(final String levelId) {
      try {
         return Files.isDirectory(this.getLevelPath(levelId), new LinkOption[0]);
      } catch (InvalidPathException var3) {
         return false;
      }
   }

   public Path getLevelPath(final String levelId) {
      return this.baseDir.resolve(levelId);
   }

   public Path getBaseDir() {
      return this.baseDir;
   }

   public Path getBackupPath() {
      return this.backupDir;
   }

   public LevelStorageAccess validateAndCreateAccess(final String levelId) throws IOException, ContentValidationException {
      Path levelPath = this.getLevelPath(levelId);
      List<ForbiddenSymlinkInfo> validationResults = this.worldDirValidator.validateDirectory(levelPath, true);
      if (!validationResults.isEmpty()) {
         throw new ContentValidationException(levelPath, validationResults);
      } else {
         return new LevelStorageAccess(levelId, levelPath);
      }
   }

   public LevelStorageAccess createAccess(final String levelId) throws IOException {
      Path levelPath = this.getLevelPath(levelId);
      return new LevelStorageAccess(levelId, levelPath);
   }

   public DirectoryValidator getWorldDirValidator() {
      return this.worldDirValidator;
   }

   public class LevelStorageAccess implements AutoCloseable {
      private final DirectoryLock lock;
      private final LevelDirectory levelDirectory;
      private final String levelId;
      private final Map<LevelResource, Path> resources;

      private LevelStorageAccess(final String levelId, final Path path) throws IOException {
         Objects.requireNonNull(LevelStorageSource.this);
         super();
         this.resources = Maps.newHashMap();
         this.levelId = levelId;
         this.levelDirectory = new LevelDirectory(path);
         this.lock = DirectoryLock.create(path);
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
         } catch (IOException e) {
            LevelStorageSource.LOGGER.warn("Failed to unlock access to level {}", this.getLevelId(), e);
         }

      }

      public LevelStorageSource parent() {
         return LevelStorageSource.this;
      }

      public LevelDirectory getLevelDirectory() {
         return this.levelDirectory;
      }

      public String getLevelId() {
         return this.levelId;
      }

      public Path getLevelPath(final LevelResource resource) {
         Map var10000 = this.resources;
         LevelDirectory var10002 = this.levelDirectory;
         Objects.requireNonNull(var10002);
         return (Path)var10000.computeIfAbsent(resource, var10002::resourcePath);
      }

      public Path getDimensionPath(final ResourceKey<Level> name) {
         return DimensionType.getStorageFolder(name, this.levelDirectory.path());
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

      public LevelSummary getSummary(final Dynamic<?> dataTag) {
         this.checkLock();
         return LevelStorageSource.this.makeLevelSummary(dataTag, this.levelDirectory, false);
      }

      public Dynamic<?> getDataTag() throws IOException {
         return this.getDataTag(false);
      }

      public Dynamic<?> getDataTagFallback() throws IOException {
         return this.getDataTag(true);
      }

      private Dynamic<?> getDataTag(final boolean useFallback) throws IOException {
         this.checkLock();
         return LevelStorageSource.readLevelDataTagFixed(useFallback ? this.levelDirectory.oldDataFile() : this.levelDirectory.dataFile(), LevelStorageSource.this.fixerUpper);
      }

      public void saveDataTag(final RegistryAccess registryAccess, final WorldData levelData) {
         this.saveDataTag(registryAccess, levelData, (CompoundTag)null);
      }

      public void saveDataTag(final RegistryAccess registryAccess, final WorldData levelData, final @Nullable CompoundTag playerData) {
         CompoundTag dataTag = levelData.createTag(registryAccess, playerData);
         CompoundTag root = new CompoundTag();
         root.put("Data", dataTag);
         this.saveLevelData(root);
      }

      private void saveLevelData(final CompoundTag root) {
         Path worldDir = this.levelDirectory.path();

         try {
            Path dataFile = Files.createTempFile(worldDir, "level", ".dat");
            NbtIo.writeCompressed(root, dataFile);
            Path oldDataFile = this.levelDirectory.oldDataFile();
            Path currentFile = this.levelDirectory.dataFile();
            Util.safeReplaceFile(currentFile, dataFile, oldDataFile);
         } catch (Exception e) {
            LevelStorageSource.LOGGER.error("Failed to save level {}", worldDir, e);
         }

      }

      public Optional<Path> getIconFile() {
         return !this.lock.isValid() ? Optional.empty() : Optional.of(this.levelDirectory.iconFile());
      }

      public void deleteLevel() throws IOException {
         this.checkLock();
         final Path lockPath = this.levelDirectory.lockFile();
         LevelStorageSource.LOGGER.info("Deleting level {}", this.levelId);

         for(int attempt = 1; attempt <= 5; ++attempt) {
            LevelStorageSource.LOGGER.info("Attempt {}...", attempt);

            try {
               Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
                  {
                     Objects.requireNonNull(LevelStorageAccess.this);
                  }

                  public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                     if (!file.equals(lockPath)) {
                        LevelStorageSource.LOGGER.debug("Deleting {}", file);
                        Files.delete(file);
                     }

                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(final Path dir, final @Nullable IOException exc) throws IOException {
                     if (exc != null) {
                        throw exc;
                     } else {
                        if (dir.equals(LevelStorageAccess.this.levelDirectory.path())) {
                           LevelStorageAccess.this.lock.close();
                           Files.deleteIfExists(lockPath);
                        }

                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                     }
                  }
               });
               break;
            } catch (IOException e) {
               if (attempt >= 5) {
                  throw e;
               }

               LevelStorageSource.LOGGER.warn("Failed to delete {}", this.levelDirectory.path(), e);

               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

      }

      public void renameLevel(final String newName) throws IOException {
         this.modifyLevelDataWithoutDatafix((tag) -> tag.putString("LevelName", newName.trim()));
      }

      public void renameAndDropPlayer(final String newName) throws IOException {
         this.modifyLevelDataWithoutDatafix((tag) -> {
            tag.putString("LevelName", newName.trim());
            tag.remove("Player");
         });
      }

      private void modifyLevelDataWithoutDatafix(final Consumer<CompoundTag> updater) throws IOException {
         this.checkLock();
         CompoundTag root = LevelStorageSource.readLevelDataTagRaw(this.levelDirectory.dataFile());
         updater.accept(root.getCompoundOrEmpty("Data"));
         this.saveLevelData(root);
      }

      public long makeWorldBackup() throws IOException {
         this.checkLock();
         String var10000 = FileNameDateFormatter.FORMATTER.format(ZonedDateTime.now());
         String zipFilePrefix = var10000 + "_" + this.levelId;
         Path root = LevelStorageSource.this.getBackupPath();

         try {
            FileUtil.createDirectoriesSafe(root);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }

         Path zipFilePath = root.resolve(FileUtil.findAvailableName(root, zipFilePrefix, ".zip"));
         final ZipOutputStream stream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFilePath)));

         try {
            final Path rootPath = Paths.get(this.levelId);
            Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
               {
                  Objects.requireNonNull(LevelStorageAccess.this);
               }

               public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
                  if (path.endsWith("session.lock")) {
                     return FileVisitResult.CONTINUE;
                  } else {
                     String entryPath = rootPath.resolve(LevelStorageAccess.this.levelDirectory.path().relativize(path)).toString().replace('\\', '/');
                     ZipEntry entry = new ZipEntry(entryPath);
                     stream.putNextEntry(entry);
                     com.google.common.io.Files.asByteSource(path.toFile()).copyTo(stream);
                     stream.closeEntry();
                     return FileVisitResult.CONTINUE;
                  }
               }
            });
         } catch (Throwable var8) {
            try {
               stream.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         stream.close();
         return Files.size(zipFilePath);
      }

      public boolean hasWorldData() {
         return Files.exists(this.levelDirectory.dataFile(), new LinkOption[0]) || Files.exists(this.levelDirectory.oldDataFile(), new LinkOption[0]);
      }

      public void close() throws IOException {
         this.lock.close();
      }

      public boolean restoreLevelDataFromOld() {
         return Util.safeReplaceOrMoveFile(this.levelDirectory.dataFile(), this.levelDirectory.oldDataFile(), this.levelDirectory.corruptedDataFile(ZonedDateTime.now()), true);
      }

      public @Nullable Instant getFileModificationTime(final boolean fallback) {
         return LevelStorageSource.getFileModificationTime(fallback ? this.levelDirectory.oldDataFile() : this.levelDirectory.dataFile());
      }
   }

   public static record LevelCandidates(List<LevelDirectory> levels) implements Iterable<LevelDirectory> {
      public LevelCandidates {
         super();
      }

      public boolean isEmpty() {
         return this.levels.isEmpty();
      }

      public Iterator<LevelDirectory> iterator() {
         return this.levels.iterator();
      }
   }

   public static record LevelDirectory(Path path) {
      public LevelDirectory {
         super();
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

      public Path corruptedDataFile(final ZonedDateTime time) {
         Path var10000 = this.path;
         String var10001 = LevelResource.LEVEL_DATA_FILE.getId();
         return var10000.resolve(var10001 + "_corrupted_" + time.format(FileNameDateFormatter.FORMATTER));
      }

      public Path rawDataFile(final ZonedDateTime time) {
         Path var10000 = this.path;
         String var10001 = LevelResource.LEVEL_DATA_FILE.getId();
         return var10000.resolve(var10001 + "_raw_" + time.format(FileNameDateFormatter.FORMATTER));
      }

      public Path iconFile() {
         return this.resourcePath(LevelResource.ICON_FILE);
      }

      public Path lockFile() {
         return this.resourcePath(LevelResource.LOCK_FILE);
      }

      public Path resourcePath(final LevelResource resource) {
         return this.path.resolve(resource.getId());
      }
   }
}
