package net.minecraft.util.filefix;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiFunction;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.filefix.virtualfilesystem.CopyOnWriteFileSystem;
import net.minecraft.util.worldupdate.UpgradeProgress;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;

public class FileFixerUpper {
   public static final int FILE_FIXER_INTRODUCTION_VERSION = 4772;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataFixerBuilder.Result dataFixer;
   private final List<FileFix> fileFixes;
   private final int latestFileFixerVersion;

   public FileFixerUpper(final DataFixerBuilder.Result dataFixer, final List<FileFix> fileFixes, final int latestFileFixerVersion) {
      super();
      this.dataFixer = dataFixer;
      this.fileFixes = List.copyOf(fileFixes);
      this.latestFileFixerVersion = latestFileFixerVersion;
   }

   public static int worldVersionToFileFixerVersion(final int levelDataVersion) {
      return levelDataVersion < 4772 ? 0 : levelDataVersion;
   }

   public boolean requiresFileFixing(final int levelDataVersion) {
      return worldVersionToFileFixerVersion(levelDataVersion) < this.latestFileFixerVersion;
   }

   public Dynamic<?> fix(final LevelStorageSource.LevelStorageAccess worldAccess, final Dynamic<?> levelDataTag, final UpgradeProgress upgradeProgress) throws IOException {
      return this.fix(worldAccess, levelDataTag, upgradeProgress, SharedConstants.getCurrentVersion().dataVersion().version());
   }

   @VisibleForTesting
   public Dynamic<?> fix(final LevelStorageSource.LevelStorageAccess worldAccess, final Dynamic<?> levelDataTag, final UpgradeProgress upgradeProgress, final int toVersion) throws IOException {
      int loadedVersion = NbtUtils.getDataVersion(levelDataTag);
      if (this.requiresFileFixing(loadedVersion)) {
         Path worldFolder = worldAccess.getLevelDirectory().path();
         CopyOnWriteFileSystem fs = CopyOnWriteFileSystem.create(worldFolder.getFileName().toString(), worldFolder);

         Dynamic<?> fixedLevelDataTag;
         try {
            fixedLevelDataTag = this.applyFileFixers(upgradeProgress, loadedVersion, toVersion, fs.rootPath());
            swapInFixedWorld(worldAccess, fs);
         } catch (Throwable var12) {
            if (fs != null) {
               try {
                  fs.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (fs != null) {
            fs.close();
         }

         return fixedLevelDataTag;
      } else {
         Dynamic<?> fixedLevelDataTag = DataFixTypes.LEVEL.updateToCurrentVersion(this.dataFixer.fixer(), levelDataTag, loadedVersion);
         return addVersionsToLevelData(fixedLevelDataTag, toVersion);
      }
   }

   @VisibleForTesting
   public Dynamic<?> applyFileFixers(final UpgradeProgress upgradeProgress, final int loadedVersion, final int toVersion, final Path basePath) throws IOException {
      List<FileFix> applicableFixers = this.getApplicableFixers(loadedVersion, toVersion);
      upgradeProgress.setType(UpgradeProgress.Type.FILES);
      this.countFiles(applicableFixers, upgradeProgress);
      upgradeProgress.setStatus(UpgradeProgress.Status.UPGRADING);
      upgradeProgress.setApplicableFixerAmount(applicableFixers.size());

      for(FileFix fileFix : applicableFixers) {
         upgradeProgress.incrementRunningFileFixer();
         fileFix.runFixOperations(basePath, upgradeProgress);
      }

      return this.writeUpdatedLevelData(basePath, toVersion);
   }

   private List<FileFix> getApplicableFixers(final int fromVersion, final int toVersion) {
      int fileFixerFromVersion = worldVersionToFileFixerVersion(fromVersion);
      return this.fileFixes.stream().filter((fileFix) -> fileFix.getVersion() > fileFixerFromVersion && fileFix.getVersion() <= toVersion).toList();
   }

   private void countFiles(final List<FileFix> applicableFixers, final UpgradeProgress upgradeProgress) {
      upgradeProgress.setStatus(UpgradeProgress.Status.COUNTING);
      int totalFiles = 0;

      for(FileFix fileFix : applicableFixers) {
         totalFiles += fileFix.countFileOperations();
      }

      upgradeProgress.addTotalFileFixOperations(totalFiles);
   }

   private Dynamic<?> writeUpdatedLevelData(final Path worldFolder, final int toVersion) throws IOException {
      Path levelDatPath = worldFolder.resolve(LevelResource.LEVEL_DATA_FILE.id());
      CompoundTag unfixedLevelDat = NbtIo.readCompressed(levelDatPath, NbtAccounter.defaultQuota());
      CompoundTag unfixedDataTag = unfixedLevelDat.getCompoundOrEmpty("Data");
      int dataVersion = NbtUtils.getDataVersion(unfixedDataTag);
      Dynamic<?> fixed = DataFixTypes.LEVEL.update(this.dataFixer.fixer(), new Dynamic(NbtOps.INSTANCE, unfixedDataTag), dataVersion, toVersion);
      fixed = addVersionsToLevelData(fixed, toVersion);
      Dynamic<?> dynamic = fixed.emptyMap().set("Data", fixed);
      NbtIo.writeCompressed((CompoundTag)dynamic.convert(NbtOps.INSTANCE).getValue(), levelDatPath);
      return fixed;
   }

   private static Dynamic<?> addVersionsToLevelData(Dynamic<?> fixed, final int toVersion) {
      fixed = NbtUtils.addDataVersion(fixed, toVersion);
      fixed = PrimaryLevelData.writeLastPlayed(fixed);
      fixed = PrimaryLevelData.writeVersionTag(fixed);
      return fixed;
   }

   @VisibleForTesting
   protected static void swapInFixedWorld(final LevelStorageSource.LevelStorageAccess worldAccess, final CopyOnWriteFileSystem fs) throws IOException {
      Path worldFolder = worldAccess.getLevelDirectory().path();
      FileSystemCapabilities fileSystemCapabilities = detectFileSystemCapabilities(worldFolder);

      try {
         LOGGER.info("Swapping in the fixed world. File system capabilities: {}", fileSystemCapabilities);
         Path tmpWorld = worldFolder.resolve("file_fixed_world");
         Path var10000 = worldFolder.getParent();
         String var10001 = worldFolder.getFileName().toString();
         Path oldWorldFolder = var10000.resolve(var10001 + "_file_fix_" + String.valueOf(UUID.randomUUID()));
         Path tmpWorldAfterMove = oldWorldFolder.resolve(worldFolder.relativize(tmpWorld));
         Files.createDirectory(tmpWorld);
         if (fileSystemCapabilities.hardLinks) {
            fs.makeHardLinkCopy(tmpWorld);
         }

         CopyOption[] copyOptions;
         if (fileSystemCapabilities.atomicMove) {
            copyOptions = new CopyOption[]{StandardCopyOption.ATOMIC_MOVE};
         } else {
            copyOptions = new CopyOption[0];
         }

         LOGGER.info("Applying file structure changes for world \"{}\"", worldAccess.getLevelId());
         if (!fileSystemCapabilities.hardLinks) {
            fs.moveFilesToNewFolder(tmpWorld, copyOptions);
         }

         worldAccess.releaseTemporarilyAndRun(() -> {
            LOGGER.info("Swapping world folder for new one");
            Files.move(worldFolder, oldWorldFolder, copyOptions);
            Files.move(tmpWorldAfterMove, worldFolder, copyOptions);
         });
         LOGGER.info("Done applying file structure changes for world \"{}\"", worldAccess.getLevelId());
         PathUtils.deleteDirectory(oldWorldFolder);
      } catch (Exception e) {
         CrashReport crashReport = CrashReport.forThrowable(e, "Failed to swap in file fixed world");
         CrashReportCategory fsCapabilities = crashReport.addCategory("File system capabilities");
         fsCapabilities.setDetail("hardLinks", fileSystemCapabilities.hardLinks);
         fsCapabilities.setDetail("atomicMove", fileSystemCapabilities.atomicMove);
         throw new ReportedException(crashReport);
      }
   }

   public static FileSystemCapabilities detectFileSystemCapabilities(final Path dir) throws IOException {
      return new FileSystemCapabilities(supportsAtomicMove(dir), supportsHardLinks(dir));
   }

   private static boolean supportsAtomicMove(final Path dir) throws IOException {
      Path sourceFile = dir.resolve(UUID.randomUUID().toString());
      Path targetFile = dir.resolve(UUID.randomUUID().toString());

      boolean var4;
      try {
         Files.createFile(sourceFile);

         try {
            Files.move(sourceFile, targetFile, StandardCopyOption.ATOMIC_MOVE);
            boolean var3 = true;
            return var3;
         } catch (AtomicMoveNotSupportedException var8) {
            var4 = false;
         }
      } finally {
         Files.deleteIfExists(sourceFile);
         Files.deleteIfExists(targetFile);
      }

      return var4;
   }

   private static boolean supportsHardLinks(final Path dir) throws IOException {
      Path sourceFile = dir.resolve(UUID.randomUUID().toString());
      Path targetFile = dir.resolve(UUID.randomUUID().toString());

      boolean var4;
      try {
         Files.createFile(sourceFile);

         try {
            Files.createLink(targetFile, sourceFile);
            boolean var3 = true;
            return var3;
         } catch (Exception var8) {
            var4 = false;
         }
      } finally {
         Files.deleteIfExists(sourceFile);
         Files.deleteIfExists(targetFile);
      }

      return var4;
   }

   public static class Builder {
      public final List<FileFix> fileFixes = new ArrayList();
      private final int currentVersion;
      private int latestFileFixerVersion;
      private final List<Schema> knownSchemas = new ArrayList();

      public Builder(final int currentVersion) {
         super();
         this.currentVersion = currentVersion;
      }

      public void addFixer(final FileFix fileFix) {
         if (!this.knownSchemas.contains(fileFix.getSchema())) {
            throw new IllegalArgumentException("Tried to add file fixer with unknown schema. Add it through FileFixerUpper#addSchema instead");
         } else {
            int fileFixVersion = fileFix.getVersion();
            if (fileFix.getVersion() > this.currentVersion) {
               throw new IllegalArgumentException(String.format(Locale.ROOT, "Tried to add too recent file fix for version: %s. The data version of the game is: %s", fileFixVersion, this.currentVersion));
            } else {
               if (!this.fileFixes.isEmpty()) {
                  FileFix last = (FileFix)this.fileFixes.getLast();
                  if (last.getVersion() > fileFixVersion) {
                     throw new IllegalArgumentException(String.format(Locale.ROOT, "Tried to add too recent file fix for version: %s. The most recent file fix version is %s", fileFixVersion, last.getVersion()));
                  }
               }

               this.fileFixes.add(fileFix);
            }
         }
      }

      public Schema addSchema(final DataFixerBuilder fixerUpper, final int version, final BiFunction<Integer, Schema, Schema> factory) {
         this.latestFileFixerVersion = Math.max(version, this.latestFileFixerVersion);
         Schema schema = fixerUpper.addSchema(version, factory);
         this.knownSchemas.add(schema);
         return schema;
      }

      public FileFixerUpper build(final DataFixerBuilder.Result dataFixer) {
         return new FileFixerUpper(dataFixer, this.fileFixes, this.latestFileFixerVersion);
      }
   }

   public static record FileSystemCapabilities(boolean atomicMove, boolean hardLinks) {
      public FileSystemCapabilities {
         super();
      }
   }
}
