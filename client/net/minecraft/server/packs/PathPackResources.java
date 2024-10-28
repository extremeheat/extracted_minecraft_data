package net.minecraft.server.packs;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;

public class PathPackResources extends AbstractPackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Joiner PATH_JOINER = Joiner.on("/");
   private final Path root;

   public PathPackResources(PackLocationInfo var1, Path var2) {
      super(var1);
      this.root = var2;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... var1) {
      FileUtil.validatePath(var1);
      Path var2 = FileUtil.resolvePath(this.root, List.of(var1));
      return Files.exists(var2, new LinkOption[0]) ? IoSupplier.create(var2) : null;
   }

   public static boolean validatePath(Path var0) {
      return true;
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
      Path var3 = this.root.resolve(var1.getDirectory()).resolve(var2.getNamespace());
      return getResource(var2, var3);
   }

   @Nullable
   public static IoSupplier<InputStream> getResource(ResourceLocation var0, Path var1) {
      return (IoSupplier)FileUtil.decomposePath(var0.getPath()).mapOrElse((var1x) -> {
         Path var2 = FileUtil.resolvePath(var1, var1x);
         return returnFileIfExists(var2);
      }, (var1x) -> {
         LOGGER.error("Invalid path {}: {}", var0, var1x.message());
         return null;
      });
   }

   @Nullable
   private static IoSupplier<InputStream> returnFileIfExists(Path var0) {
      return Files.exists(var0, new LinkOption[0]) && validatePath(var0) ? IoSupplier.create(var0) : null;
   }

   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      FileUtil.decomposePath(var3).ifSuccess((var4x) -> {
         Path var5 = this.root.resolve(var1.getDirectory()).resolve(var2);
         listPath(var2, var5, var4x, var4);
      }).ifError((var1x) -> {
         LOGGER.error("Invalid path {}: {}", var3, var1x.message());
      });
   }

   public static void listPath(String var0, Path var1, List<String> var2, PackResources.ResourceOutput var3) {
      Path var4 = FileUtil.resolvePath(var1, var2);

      try {
         Stream var5 = Files.find(var4, 2147483647, (var0x, var1x) -> {
            return var1x.isRegularFile();
         }, new FileVisitOption[0]);

         try {
            var5.forEach((var3x) -> {
               String var4 = PATH_JOINER.join(var1.relativize(var3x));
               ResourceLocation var5 = ResourceLocation.tryBuild(var0, var4);
               if (var5 == null) {
                  Util.logAndPauseIfInIde(String.format(Locale.ROOT, "Invalid path in pack: %s:%s, ignoring", var0, var4));
               } else {
                  var3.accept(var5, IoSupplier.create(var3x));
               }

            });
         } catch (Throwable var9) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (NotDirectoryException | NoSuchFileException var10) {
      } catch (IOException var11) {
         LOGGER.error("Failed to list path {}", var4, var11);
      }

   }

   public Set<String> getNamespaces(PackType var1) {
      HashSet var2 = Sets.newHashSet();
      Path var3 = this.root.resolve(var1.getDirectory());

      try {
         DirectoryStream var4 = Files.newDirectoryStream(var3);

         try {
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               Path var6 = (Path)var5.next();
               String var7 = var6.getFileName().toString();
               if (ResourceLocation.isValidNamespace(var7)) {
                  var2.add(var7);
               } else {
                  LOGGER.warn("Non [a-z0-9_.-] character in namespace {} in pack {}, ignoring", var7, this.root);
               }
            }
         } catch (Throwable var9) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (NotDirectoryException | NoSuchFileException var10) {
      } catch (IOException var11) {
         LOGGER.error("Failed to list path {}", var3, var11);
      }

      return var2;
   }

   public void close() {
   }

   public static class PathResourcesSupplier implements Pack.ResourcesSupplier {
      private final Path content;

      public PathResourcesSupplier(Path var1) {
         super();
         this.content = var1;
      }

      public PackResources openPrimary(PackLocationInfo var1) {
         return new PathPackResources(var1, this.content);
      }

      public PackResources openFull(PackLocationInfo var1, Pack.Metadata var2) {
         PackResources var3 = this.openPrimary(var1);
         List var4 = var2.overlays();
         if (var4.isEmpty()) {
            return var3;
         } else {
            ArrayList var5 = new ArrayList(var4.size());
            Iterator var6 = var4.iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               Path var8 = this.content.resolve(var7);
               var5.add(new PathPackResources(var1, var8));
            }

            return new CompositePackResources(var3, var5);
         }
      }
   }
}
