package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.datafix.DataFixTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorageSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateTimeFormatter FORMATTER;
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

   public String getName() {
      return "Anvil";
   }

   public List<LevelSummary> getLevelList() throws LevelStorageException {
      if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
         throw new LevelStorageException((new TranslatableComponent("selectWorld.load_folder_access", new Object[0])).getString());
      } else {
         ArrayList var1 = Lists.newArrayList();
         File[] var2 = this.baseDir.toFile().listFiles();
         File[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            if (var6.isDirectory()) {
               String var7 = var6.getName();
               LevelData var8 = this.getDataTagFor(var7);
               if (var8 != null && (var8.getVersion() == 19132 || var8.getVersion() == 19133)) {
                  boolean var9 = var8.getVersion() != this.getStorageVersion();
                  String var10 = var8.getLevelName();
                  if (StringUtils.isEmpty(var10)) {
                     var10 = var7;
                  }

                  long var11 = 0L;
                  var1.add(new LevelSummary(var8, var7, var10, 0L, var9));
               }
            }
         }

         return var1;
      }
   }

   private int getStorageVersion() {
      return 19133;
   }

   public LevelStorage selectLevel(String var1, @Nullable MinecraftServer var2) {
      return selectLevel(this.baseDir, this.fixerUpper, var1, var2);
   }

   protected static LevelStorage selectLevel(Path var0, DataFixer var1, String var2, @Nullable MinecraftServer var3) {
      return new LevelStorage(var0.toFile(), var2, var3, var1);
   }

   public boolean requiresConversion(String var1) {
      LevelData var2 = this.getDataTagFor(var1);
      return var2 != null && var2.getVersion() != this.getStorageVersion();
   }

   public boolean convertLevel(String var1, ProgressListener var2) {
      return McRegionUpgrader.convertLevel(this.baseDir, this.fixerUpper, var1, var2);
   }

   @Nullable
   public LevelData getDataTagFor(String var1) {
      return getDataTagFor(this.baseDir, this.fixerUpper, var1);
   }

   @Nullable
   protected static LevelData getDataTagFor(Path var0, DataFixer var1, String var2) {
      File var3 = new File(var0.toFile(), var2);
      if (!var3.exists()) {
         return null;
      } else {
         File var4 = new File(var3, "level.dat");
         if (var4.exists()) {
            LevelData var5 = getLevelData(var4, var1);
            if (var5 != null) {
               return var5;
            }
         }

         var4 = new File(var3, "level.dat_old");
         return var4.exists() ? getLevelData(var4, var1) : null;
      }
   }

   @Nullable
   public static LevelData getLevelData(File var0, DataFixer var1) {
      try {
         CompoundTag var2 = NbtIo.readCompressed(new FileInputStream(var0));
         CompoundTag var3 = var2.getCompound("Data");
         CompoundTag var4 = var3.contains("Player", 10) ? var3.getCompound("Player") : null;
         var3.remove("Player");
         int var5 = var3.contains("DataVersion", 99) ? var3.getInt("DataVersion") : -1;
         return new LevelData(NbtUtils.update(var1, DataFixTypes.LEVEL, var3, var5), var1, var5, var4);
      } catch (Exception var6) {
         LOGGER.error("Exception reading {}", var0, var6);
         return null;
      }
   }

   public void renameLevel(String var1, String var2) {
      File var3 = new File(this.baseDir.toFile(), var1);
      if (var3.exists()) {
         File var4 = new File(var3, "level.dat");
         if (var4.exists()) {
            try {
               CompoundTag var5 = NbtIo.readCompressed(new FileInputStream(var4));
               CompoundTag var6 = var5.getCompound("Data");
               var6.putString("LevelName", var2);
               NbtIo.writeCompressed(var5, new FileOutputStream(var4));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }

      }
   }

   public boolean isNewLevelIdAcceptable(String var1) {
      try {
         Path var2 = this.baseDir.resolve(var1);
         Files.createDirectory(var2);
         Files.deleteIfExists(var2);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public boolean deleteLevel(String var1) {
      File var2 = new File(this.baseDir.toFile(), var1);
      if (!var2.exists()) {
         return true;
      } else {
         LOGGER.info("Deleting level {}", var1);

         for(int var3 = 1; var3 <= 5; ++var3) {
            LOGGER.info("Attempt {}...", var3);
            if (deleteRecursive(var2.listFiles())) {
               break;
            }

            LOGGER.warn("Unsuccessful in deleting contents.");
            if (var3 < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return var2.delete();
      }
   }

   private static boolean deleteRecursive(File[] var0) {
      File[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         File var4 = var1[var3];
         LOGGER.debug("Deleting {}", var4);
         if (var4.isDirectory() && !deleteRecursive(var4.listFiles())) {
            LOGGER.warn("Couldn't delete directory {}", var4);
            return false;
         }

         if (!var4.delete()) {
            LOGGER.warn("Couldn't delete file {}", var4);
            return false;
         }
      }

      return true;
   }

   public boolean levelExists(String var1) {
      return Files.isDirectory(this.baseDir.resolve(var1), new LinkOption[0]);
   }

   public Path getBaseDir() {
      return this.baseDir;
   }

   public File getFile(String var1, String var2) {
      return this.baseDir.resolve(var1).resolve(var2).toFile();
   }

   private Path getLevelPath(String var1) {
      return this.baseDir.resolve(var1);
   }

   public Path getBackupPath() {
      return this.backupDir;
   }

   public long makeWorldBackup(String var1) throws IOException {
      final Path var2 = this.getLevelPath(var1);
      String var3 = LocalDateTime.now().format(FORMATTER) + "_" + var1;
      Path var4 = this.getBackupPath();

      try {
         Files.createDirectories(Files.exists(var4, new LinkOption[0]) ? var4.toRealPath() : var4);
      } catch (IOException var18) {
         throw new RuntimeException(var18);
      }

      Path var5 = var4.resolve(FileUtil.findAvailableName(var4, var3, ".zip"));
      final ZipOutputStream var6 = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(var5)));
      Throwable var7 = null;

      try {
         final Path var8 = Paths.get(var1);
         Files.walkFileTree(var2, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path var1, BasicFileAttributes var2x) throws IOException {
               String var3 = var8.resolve(var2.relativize(var1)).toString().replace('\\', '/');
               ZipEntry var4 = new ZipEntry(var3);
               var6.putNextEntry(var4);
               com.google.common.io.Files.asByteSource(var1.toFile()).copyTo(var6);
               var6.closeEntry();
               return FileVisitResult.CONTINUE;
            }

            // $FF: synthetic method
            public FileVisitResult visitFile(Object var1, BasicFileAttributes var2x) throws IOException {
               return this.visitFile((Path)var1, var2x);
            }
         });
      } catch (Throwable var17) {
         var7 = var17;
         throw var17;
      } finally {
         if (var6 != null) {
            if (var7 != null) {
               try {
                  var6.close();
               } catch (Throwable var16) {
                  var7.addSuppressed(var16);
               }
            } else {
               var6.close();
            }
         }

      }

      return Files.size(var5);
   }

   static {
      FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   }
}
