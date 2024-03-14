package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.slf4j.Logger;

public class FolderRepositorySource implements RepositorySource {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final PackSelectionConfig DISCOVERED_PACK_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
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
         discoverPacks(this.folder, this.validator, (var2, var3x) -> {
            PackLocationInfo var4 = this.createDiscoveredFilePackInfo(var2);
            Pack var5 = Pack.readMetaAndCreate(var4, var3x, this.packType, DISCOVERED_PACK_SELECTION_CONFIG);
            if (var5 != null) {
               var1.accept(var5);
            }
         });
      } catch (IOException var3) {
         LOGGER.warn("Failed to list packs in {}", this.folder, var3);
      }
   }

   private PackLocationInfo createDiscoveredFilePackInfo(Path var1) {
      String var2 = nameFromPath(var1);
      return new PackLocationInfo("file/" + var2, Component.literal(var2), this.packSource, Optional.empty());
   }

   public static void discoverPacks(Path var0, DirectoryValidator var1, BiConsumer<Path, Pack.ResourcesSupplier> var2) throws IOException {
      FolderRepositorySource.FolderPackDetector var3 = new FolderRepositorySource.FolderPackDetector(var1);

      try (DirectoryStream var4 = Files.newDirectoryStream(var0)) {
         for(Path var6 : var4) {
            try {
               ArrayList var7 = new ArrayList();
               Pack.ResourcesSupplier var8 = var3.detectPackResources(var6, var7);
               if (!var7.isEmpty()) {
                  LOGGER.warn("Ignoring potential pack entry: {}", ContentValidationException.getMessage(var6, var7));
               } else if (var8 != null) {
                  var2.accept(var6, var8);
               } else {
                  LOGGER.info("Found non-pack entry '{}', ignoring", var6);
               }
            } catch (IOException var10) {
               LOGGER.warn("Failed to read properties of '{}', ignoring", var6, var10);
            }
         }
      }
   }

   static class FolderPackDetector extends PackDetector<Pack.ResourcesSupplier> {
      protected FolderPackDetector(DirectoryValidator var1) {
         super(var1);
      }

      @Nullable
      protected Pack.ResourcesSupplier createZipPack(Path var1) {
         FileSystem var2 = var1.getFileSystem();
         if (var2 != FileSystems.getDefault() && !(var2 instanceof LinkFileSystem)) {
            FolderRepositorySource.LOGGER.info("Can't open pack archive at {}", var1);
            return null;
         } else {
            return new FilePackResources.FileResourcesSupplier(var1);
         }
      }

      protected Pack.ResourcesSupplier createDirectoryPack(Path var1) {
         return new PathPackResources.PathResourcesSupplier(var1);
      }
   }
}
