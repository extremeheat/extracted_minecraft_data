package net.minecraft.client.resources;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class DirectAssetIndex extends AssetIndex {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final File assetsDirectory;

   public DirectAssetIndex(File var1) {
      super();
      this.assetsDirectory = var1;
   }

   @Override
   public File getFile(ResourceLocation var1) {
      return new File(this.assetsDirectory, var1.toString().replace(':', '/'));
   }

   @Override
   public File getRootFile(String var1) {
      return new File(this.assetsDirectory, var1);
   }

   @Override
   public Collection<ResourceLocation> getFiles(String var1, String var2, Predicate<ResourceLocation> var3) {
      Path var4 = this.assetsDirectory.toPath().resolve(var2);

      try {
         Collection var6;
         try (Stream var5 = Files.walk(var4.resolve(var1))) {
            var6 = var5.filter(var0 -> Files.isRegularFile(var0))
               .filter(var0 -> !var0.endsWith(".mcmeta"))
               .map(var2x -> new ResourceLocation(var2, var4.relativize(var2x).toString().replaceAll("\\\\", "/")))
               .filter(var3)
               .collect(Collectors.toList());
         }

         return var6;
      } catch (NoSuchFileException var10) {
      } catch (IOException var11) {
         LOGGER.warn("Unable to getFiles on {}", var1, var11);
      }

      return Collections.emptyList();
   }
}
