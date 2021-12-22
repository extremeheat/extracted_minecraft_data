package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class DirectAssetIndex extends AssetIndex {
   private final File assetsDirectory;

   public DirectAssetIndex(File var1) {
      super();
      this.assetsDirectory = var1;
   }

   public File getFile(ResourceLocation var1) {
      return new File(this.assetsDirectory, var1.toString().replace(':', '/'));
   }

   public File getRootFile(String var1) {
      return new File(this.assetsDirectory, var1);
   }

   public Collection<ResourceLocation> getFiles(String var1, String var2, int var3, Predicate<String> var4) {
      Path var5 = this.assetsDirectory.toPath().resolve(var2);

      try {
         Stream var6 = Files.walk(var5.resolve(var1), var3, new FileVisitOption[0]);

         Collection var7;
         try {
            var7 = (Collection)var6.filter((var0) -> {
               return Files.isRegularFile(var0, new LinkOption[0]);
            }).filter((var0) -> {
               return !var0.endsWith(".mcmeta");
            }).filter((var1x) -> {
               return var4.test(var1x.getFileName().toString());
            }).map((var2x) -> {
               return new ResourceLocation(var2, var5.relativize(var2x).toString().replaceAll("\\\\", "/"));
            }).collect(Collectors.toList());
         } catch (Throwable var10) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (var6 != null) {
            var6.close();
         }

         return var7;
      } catch (NoSuchFileException var11) {
      } catch (IOException var12) {
         LOGGER.warn("Unable to getFiles on {}", var1, var12);
      }

      return Collections.emptyList();
   }
}
