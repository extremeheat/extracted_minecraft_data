package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;

public class VanillaPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;
   private final BuiltInMetadata metadata;
   private final Set<String> namespaces;
   private final List<Path> rootPaths;
   private final Map<PackType, List<Path>> pathsForType;

   VanillaPackResources(PackLocationInfo var1, BuiltInMetadata var2, Set<String> var3, List<Path> var4, Map<PackType, List<Path>> var5) {
      super();
      this.location = var1;
      this.metadata = var2;
      this.namespaces = var3;
      this.rootPaths = var4;
      this.pathsForType = var5;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... var1) {
      FileUtil.validatePath(var1);
      List var2 = List.of(var1);
      Iterator var3 = this.rootPaths.iterator();

      Path var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         Path var4 = (Path)var3.next();
         var5 = FileUtil.resolvePath(var4, var2);
      } while(!Files.exists(var5, new LinkOption[0]) || !PathPackResources.validatePath(var5));

      return IoSupplier.create(var5);
   }

   public void listRawPaths(PackType var1, ResourceLocation var2, Consumer<Path> var3) {
      FileUtil.decomposePath(var2.getPath()).ifSuccess((var4) -> {
         String var5 = var2.getNamespace();
         Iterator var6 = ((List)this.pathsForType.get(var1)).iterator();

         while(var6.hasNext()) {
            Path var7 = (Path)var6.next();
            Path var8 = var7.resolve(var5);
            var3.accept(FileUtil.resolvePath(var8, var4));
         }

      }).ifError((var1x) -> {
         LOGGER.error("Invalid path {}: {}", var2, var1x.message());
      });
   }

   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      FileUtil.decomposePath(var3).ifSuccess((var4x) -> {
         List var5 = (List)this.pathsForType.get(var1);
         int var6 = var5.size();
         if (var6 == 1) {
            getResources(var4, var2, (Path)var5.get(0), var4x);
         } else if (var6 > 1) {
            HashMap var7 = new HashMap();

            for(int var8 = 0; var8 < var6 - 1; ++var8) {
               Objects.requireNonNull(var7);
               getResources(var7::putIfAbsent, var2, (Path)var5.get(var8), var4x);
            }

            Path var9 = (Path)var5.get(var6 - 1);
            if (var7.isEmpty()) {
               getResources(var4, var2, var9, var4x);
            } else {
               Objects.requireNonNull(var7);
               getResources(var7::putIfAbsent, var2, var9, var4x);
               var7.forEach(var4);
            }
         }

      }).ifError((var1x) -> {
         LOGGER.error("Invalid path {}: {}", var3, var1x.message());
      });
   }

   private static void getResources(PackResources.ResourceOutput var0, String var1, Path var2, List<String> var3) {
      Path var4 = var2.resolve(var1);
      PathPackResources.listPath(var1, var4, var3, var0);
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
      return (IoSupplier)FileUtil.decomposePath(var2.getPath()).mapOrElse((var3) -> {
         String var4 = var2.getNamespace();
         Iterator var5 = ((List)this.pathsForType.get(var1)).iterator();

         Path var7;
         do {
            if (!var5.hasNext()) {
               return null;
            }

            Path var6 = (Path)var5.next();
            var7 = FileUtil.resolvePath(var6.resolve(var4), var3);
         } while(!Files.exists(var7, new LinkOption[0]) || !PathPackResources.validatePath(var7));

         return IoSupplier.create(var7);
      }, (var1x) -> {
         LOGGER.error("Invalid path {}: {}", var2, var1x.message());
         return null;
      });
   }

   public Set<String> getNamespaces(PackType var1) {
      return this.namespaces;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) {
      IoSupplier var2 = this.getRootResource("pack.mcmeta");
      if (var2 != null) {
         try {
            InputStream var3 = (InputStream)var2.get();

            Object var5;
            label54: {
               try {
                  Object var4 = AbstractPackResources.getMetadataFromStream(var1, var3);
                  if (var4 != null) {
                     var5 = var4;
                     break label54;
                  }
               } catch (Throwable var7) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (var3 != null) {
                  var3.close();
               }

               return this.metadata.get(var1);
            }

            if (var3 != null) {
               var3.close();
            }

            return var5;
         } catch (IOException var8) {
         }
      }

      return this.metadata.get(var1);
   }

   public PackLocationInfo location() {
      return this.location;
   }

   public void close() {
   }

   public ResourceProvider asProvider() {
      return (var1) -> {
         return Optional.ofNullable(this.getResource(PackType.CLIENT_RESOURCES, var1)).map((var1x) -> {
            return new Resource(this, var1x);
         });
      };
   }
}
