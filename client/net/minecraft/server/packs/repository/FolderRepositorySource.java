package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import org.slf4j.Logger;

public class FolderRepositorySource implements RepositorySource {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path folder;
   private final PackType packType;
   private final PackSource packSource;

   public FolderRepositorySource(Path var1, PackType var2, PackSource var3) {
      super();
      this.folder = var1;
      this.packType = var2;
      this.packSource = var3;
   }

   private static String nameFromPath(Path var0) {
      return var0.getFileName().toString();
   }

   @Override
   public void loadPacks(Consumer<Pack> var1) {
      try {
         FileUtil.createDirectoriesSafe(this.folder);
         discoverPacks(this.folder, false, (var2, var3x) -> {
            String var4 = nameFromPath(var2);
            Pack var5 = Pack.readMetaAndCreate("file/" + var4, Component.literal(var4), false, var3x, this.packType, Pack.Position.TOP, this.packSource);
            if (var5 != null) {
               var1.accept(var5);
            }
         });
      } catch (IOException var3) {
         LOGGER.warn("Failed to list packs in {}", this.folder, var3);
      }
   }

   public static void discoverPacks(Path var0, boolean var1, BiConsumer<Path, Pack.ResourcesSupplier> var2) throws IOException {
      try (DirectoryStream var3 = Files.newDirectoryStream(var0)) {
         for(Path var5 : var3) {
            Pack.ResourcesSupplier var6 = detectPackResources(var5, var1);
            if (var6 != null) {
               var2.accept(var5, var6);
            }
         }
      }
   }

   @Nullable
   public static Pack.ResourcesSupplier detectPackResources(Path var0, boolean var1) {
      BasicFileAttributes var2;
      try {
         var2 = Files.readAttributes(var0, BasicFileAttributes.class);
      } catch (NoSuchFileException var5) {
         return null;
      } catch (IOException var6) {
         LOGGER.warn("Failed to read properties of '{}', ignoring", var0, var6);
         return null;
      }

      if (var2.isDirectory() && Files.isRegularFile(var0.resolve("pack.mcmeta"))) {
         return var2x -> new PathPackResources(var2x, var0, var1);
      } else {
         if (var2.isRegularFile() && var0.getFileName().toString().endsWith(".zip")) {
            FileSystem var3 = var0.getFileSystem();
            if (var3 == FileSystems.getDefault() || var3 instanceof LinkFileSystem) {
               File var4 = var0.toFile();
               return var2x -> new FilePackResources(var2x, var4, var1);
            }
         }

         LOGGER.info("Found non-pack entry '{}', ignoring", var0);
         return null;
      }
   }
}
