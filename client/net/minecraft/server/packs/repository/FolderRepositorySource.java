package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.slf4j.Logger;

public class FolderRepositorySource implements RepositorySource {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Path folder;
   private final PackType packType;
   private final PackSource packSource;
   private final DirectoryValidator validator;

   public FolderRepositorySource(Path var1, PackType var2, PackSource var3, DirectoryValidator var4) {
      super();
      this.folder = var1;
      this.packType = var2;
      this.packSource = var3;
      this.validator = var4;
   }

   private static String nameFromPath(Path var0) {
      return var0.getFileName().toString();
   }

   @Override
   public void loadPacks(Consumer<Pack> var1) {
      try {
         FileUtil.createDirectoriesSafe(this.folder);
         discoverPacks(this.folder, this.validator, false, (var2, var3x) -> {
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

   public static void discoverPacks(Path var0, DirectoryValidator var1, boolean var2, BiConsumer<Path, Pack.ResourcesSupplier> var3) throws IOException {
      FolderRepositorySource.FolderPackDetector var4 = new FolderRepositorySource.FolderPackDetector(var1, var2);

      try (DirectoryStream var5 = Files.newDirectoryStream(var0)) {
         for(Path var7 : var5) {
            try {
               ArrayList var8 = new ArrayList();
               Pack.ResourcesSupplier var9 = var4.detectPackResources(var7, var8);
               if (!var8.isEmpty()) {
                  LOGGER.warn("Ignoring potential pack entry: {}", ContentValidationException.getMessage(var7, var8));
               } else if (var9 != null) {
                  var3.accept(var7, var9);
               } else {
                  LOGGER.info("Found non-pack entry '{}', ignoring", var7);
               }
            } catch (IOException var11) {
               LOGGER.warn("Failed to read properties of '{}', ignoring", var7, var11);
            }
         }
      }
   }

   static class FolderPackDetector extends PackDetector<Pack.ResourcesSupplier> {
      private final boolean isBuiltin;

      protected FolderPackDetector(DirectoryValidator var1, boolean var2) {
         super(var1);
         this.isBuiltin = var2;
      }

      @Nullable
      protected Pack.ResourcesSupplier createZipPack(Path var1) {
         FileSystem var2 = var1.getFileSystem();
         if (var2 != FileSystems.getDefault() && !(var2 instanceof LinkFileSystem)) {
            FolderRepositorySource.LOGGER.info("Can't open pack archive at {}", var1);
            return null;
         } else {
            return new FilePackResources.FileResourcesSupplier(var1, this.isBuiltin);
         }
      }

      protected Pack.ResourcesSupplier createDirectoryPack(Path var1) {
         return new PathPackResources.PathResourcesSupplier(var1, this.isBuiltin);
      }
   }
}
