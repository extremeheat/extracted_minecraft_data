package net.minecraft.server.packs;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;

public class PathPackResources extends AbstractPackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Joiner PATH_JOINER = Joiner.on("/");
   private final Path root;

   public PathPackResources(String var1, Path var2, boolean var3) {
      super(var1, var3);
      this.root = var2;
   }

   @Nullable
   @Override
   public IoSupplier<InputStream> getRootResource(String... var1) {
      FileUtil.validatePath(var1);
      Path var2 = FileUtil.resolvePath(this.root, List.of(var1));
      return Files.exists(var2) ? IoSupplier.create(var2) : null;
   }

   public static boolean validatePath(Path var0) {
      return true;
   }

   @Nullable
   @Override
   public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
      Path var3 = this.root.resolve(var1.getDirectory()).resolve(var2.getNamespace());
      return getResource(var2, var3);
   }

   public static IoSupplier<InputStream> getResource(ResourceLocation var0, Path var1) {
      return (IoSupplier<InputStream>)FileUtil.decomposePath(var0.getPath()).get().map(var1x -> {
         Path var2 = FileUtil.resolvePath(var1, var1x);
         return returnFileIfExists(var2);
      }, var1x -> {
         LOGGER.error("Invalid path {}: {}", var0, var1x.message());
         return null;
      });
   }

   @Nullable
   private static IoSupplier<InputStream> returnFileIfExists(Path var0) {
      return Files.exists(var0) && validatePath(var0) ? IoSupplier.create(var0) : null;
   }

   @Override
   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      FileUtil.decomposePath(var3).get().ifLeft(var4x -> {
         Path var5 = this.root.resolve(var1.getDirectory()).resolve(var2);
         listPath(var2, var5, var4x, var4);
      }).ifRight(var1x -> LOGGER.error("Invalid path {}: {}", var3, var1x.message()));
   }

   public static void listPath(String var0, Path var1, List<String> var2, PackResources.ResourceOutput var3) {
      Path var4 = FileUtil.resolvePath(var1, var2);

      try (Stream var5 = Files.find(var4, 2147483647, (var0x, var1x) -> var1x.isRegularFile())) {
         var5.forEach(var3x -> {
            String var4x = PATH_JOINER.join(var1.relativize(var3x));
            ResourceLocation var5x = ResourceLocation.tryBuild(var0, var4x);
            if (var5x == null) {
               Util.logAndPauseIfInIde(String.format(Locale.ROOT, "Invalid path in pack: %s:%s, ignoring", var0, var4x));
            } else {
               var3.accept(var5x, IoSupplier.create(var3x));
            }
         });
      } catch (NoSuchFileException var10) {
      } catch (IOException var11) {
         LOGGER.error("Failed to list path {}", var4, var11);
      }
   }

   @Override
   public Set<String> getNamespaces(PackType var1) {
      HashSet var2 = Sets.newHashSet();
      Path var3 = this.root.resolve(var1.getDirectory());

      try (DirectoryStream var4 = Files.newDirectoryStream(var3)) {
         for(Path var6 : var4) {
            String var7 = var6.getFileName().toString();
            if (var7.equals(var7.toLowerCase(Locale.ROOT))) {
               var2.add(var7);
            } else {
               LOGGER.warn("Ignored non-lowercase namespace: {} in {}", var7, this.root);
            }
         }
      } catch (NoSuchFileException var10) {
      } catch (IOException var11) {
         LOGGER.error("Failed to list path {}", var3, var11);
      }

      return var2;
   }

   @Override
   public void close() {
   }
}
