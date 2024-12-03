package net.minecraft.server.packs.resources;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

public class MultiPackResourceManager implements CloseableResourceManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, FallbackResourceManager> namespacedManagers;
   private final List<PackResources> packs;

   public MultiPackResourceManager(PackType var1, List<PackResources> var2) {
      super();
      this.packs = List.copyOf(var2);
      HashMap var3 = new HashMap();
      List var4 = var2.stream().flatMap((var1x) -> var1x.getNamespaces(var1).stream()).distinct().toList();

      for(PackResources var6 : var2) {
         ResourceFilterSection var7 = this.getPackFilterSection(var6);
         Set var8 = var6.getNamespaces(var1);
         Predicate var9 = var7 != null ? (var1x) -> var7.isPathFiltered(var1x.getPath()) : null;

         for(String var11 : var4) {
            boolean var12 = var8.contains(var11);
            boolean var13 = var7 != null && var7.isNamespaceFiltered(var11);
            if (var12 || var13) {
               FallbackResourceManager var14 = (FallbackResourceManager)var3.get(var11);
               if (var14 == null) {
                  var14 = new FallbackResourceManager(var1, var11);
                  var3.put(var11, var14);
               }

               if (var12 && var13) {
                  var14.push(var6, var9);
               } else if (var12) {
                  var14.push(var6);
               } else {
                  var14.pushFilterOnly(var6.packId(), var9);
               }
            }
         }
      }

      this.namespacedManagers = var3;
   }

   @Nullable
   private ResourceFilterSection getPackFilterSection(PackResources var1) {
      try {
         return (ResourceFilterSection)var1.getMetadataSection(ResourceFilterSection.TYPE);
      } catch (IOException var3) {
         LOGGER.error("Failed to get filter section from pack {}", var1.packId());
         return null;
      }
   }

   public Set<String> getNamespaces() {
      return this.namespacedManagers.keySet();
   }

   public Optional<Resource> getResource(ResourceLocation var1) {
      ResourceManager var2 = (ResourceManager)this.namespacedManagers.get(var1.getNamespace());
      return var2 != null ? var2.getResource(var1) : Optional.empty();
   }

   public List<Resource> getResourceStack(ResourceLocation var1) {
      ResourceManager var2 = (ResourceManager)this.namespacedManagers.get(var1.getNamespace());
      return var2 != null ? var2.getResourceStack(var1) : List.of();
   }

   public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2) {
      checkTrailingDirectoryPath(var1);
      TreeMap var3 = new TreeMap();

      for(FallbackResourceManager var5 : this.namespacedManagers.values()) {
         var3.putAll(var5.listResources(var1, var2));
      }

      return var3;
   }

   public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2) {
      checkTrailingDirectoryPath(var1);
      TreeMap var3 = new TreeMap();

      for(FallbackResourceManager var5 : this.namespacedManagers.values()) {
         var3.putAll(var5.listResourceStacks(var1, var2));
      }

      return var3;
   }

   private static void checkTrailingDirectoryPath(String var0) {
      if (var0.endsWith("/")) {
         throw new IllegalArgumentException("Trailing slash in path " + var0);
      }
   }

   public Stream<PackResources> listPacks() {
      return this.packs.stream();
   }

   public void close() {
      this.packs.forEach(PackResources::close);
   }
}
