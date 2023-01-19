package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

public class FallbackResourceManager implements ResourceManager {
   static final Logger LOGGER = LogUtils.getLogger();
   protected final List<FallbackResourceManager.PackEntry> fallbacks = Lists.newArrayList();
   final PackType type;
   private final String namespace;

   public FallbackResourceManager(PackType var1, String var2) {
      super();
      this.type = var1;
      this.namespace = var2;
   }

   public void push(PackResources var1) {
      this.pushInternal(var1.getName(), var1, null);
   }

   public void push(PackResources var1, Predicate<ResourceLocation> var2) {
      this.pushInternal(var1.getName(), var1, var2);
   }

   public void pushFilterOnly(String var1, Predicate<ResourceLocation> var2) {
      this.pushInternal(var1, null, var2);
   }

   private void pushInternal(String var1, @Nullable PackResources var2, @Nullable Predicate<ResourceLocation> var3) {
      this.fallbacks.add(new FallbackResourceManager.PackEntry(var1, var2, var3));
   }

   @Override
   public Set<String> getNamespaces() {
      return ImmutableSet.of(this.namespace);
   }

   @Override
   public Optional<Resource> getResource(ResourceLocation var1) {
      if (!this.isValidLocation(var1)) {
         return Optional.empty();
      } else {
         for(int var2 = this.fallbacks.size() - 1; var2 >= 0; --var2) {
            FallbackResourceManager.PackEntry var3 = this.fallbacks.get(var2);
            PackResources var4 = var3.resources;
            if (var4 != null && var4.hasResource(this.type, var1)) {
               return Optional.of(new Resource(var4.getName(), this.createResourceGetter(var1, var4), this.createStackMetadataFinder(var1, var2)));
            }

            if (var3.isFiltered(var1)) {
               LOGGER.warn("Resource {} not found, but was filtered by pack {}", var1, var3.name);
               return Optional.empty();
            }
         }

         return Optional.empty();
      }
   }

   Resource.IoSupplier<InputStream> createResourceGetter(ResourceLocation var1, PackResources var2) {
      return LOGGER.isDebugEnabled() ? () -> {
         InputStream var3 = var2.getResource(this.type, var1);
         return new FallbackResourceManager.LeakedResourceWarningInputStream(var3, var1, var2.getName());
      } : () -> var2.getResource(this.type, var1);
   }

   private boolean isValidLocation(ResourceLocation var1) {
      return !var1.getPath().contains("..");
   }

   @Override
   public List<Resource> getResourceStack(ResourceLocation var1) {
      if (!this.isValidLocation(var1)) {
         return List.of();
      } else {
         ArrayList var2 = Lists.newArrayList();
         ResourceLocation var3 = getMetadataLocation(var1);
         String var4 = null;

         for(FallbackResourceManager.PackEntry var6 : this.fallbacks) {
            if (var6.isFiltered(var1)) {
               if (!var2.isEmpty()) {
                  var4 = var6.name;
               }

               var2.clear();
            } else if (var6.isFiltered(var3)) {
               var2.forEach(FallbackResourceManager.SinglePackResourceThunkSupplier::ignoreMeta);
            }

            PackResources var7 = var6.resources;
            if (var7 != null && var7.hasResource(this.type, var1)) {
               var2.add(new FallbackResourceManager.SinglePackResourceThunkSupplier(var1, var3, var7));
            }
         }

         if (var2.isEmpty() && var4 != null) {
            LOGGER.info("Resource {} was filtered by pack {}", var1, var4);
         }

         return var2.stream().map(FallbackResourceManager.SinglePackResourceThunkSupplier::create).toList();
      }
   }

   @Override
   public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      int var4 = this.fallbacks.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         FallbackResourceManager.PackEntry var6 = this.fallbacks.get(var5);
         var6.filterAll(var3.keySet());
         if (var6.resources != null) {
            for(ResourceLocation var8 : var6.resources.getResources(this.type, this.namespace, var1, var2)) {
               var3.put(var8, var5);
            }
         }
      }

      TreeMap var11 = Maps.newTreeMap();
      ObjectIterator var12 = Object2IntMaps.fastIterable(var3).iterator();

      while(var12.hasNext()) {
         Entry var13 = (Entry)var12.next();
         int var14 = var13.getIntValue();
         ResourceLocation var9 = (ResourceLocation)var13.getKey();
         PackResources var10 = this.fallbacks.get(var14).resources;
         var11.put(var9, new Resource(var10.getName(), this.createResourceGetter(var9, var10), this.createStackMetadataFinder(var9, var14)));
      }

      return var11;
   }

   private Resource.IoSupplier<ResourceMetadata> createStackMetadataFinder(ResourceLocation var1, int var2) {
      return () -> {
         ResourceLocation var3 = getMetadataLocation(var1);

         for(int var4 = this.fallbacks.size() - 1; var4 >= var2; --var4) {
            FallbackResourceManager.PackEntry var5 = this.fallbacks.get(var4);
            PackResources var6 = var5.resources;
            if (var6 != null && var6.hasResource(this.type, var3)) {
               ResourceMetadata var8;
               try (InputStream var7 = var6.getResource(this.type, var3)) {
                  var8 = ResourceMetadata.fromJsonStream(var7);
               }

               return var8;
            }

            if (var5.isFiltered(var3)) {
               break;
            }
         }

         return ResourceMetadata.EMPTY;
      };
   }

   private static void applyPackFiltersToExistingResources(
      FallbackResourceManager.PackEntry var0, Map<ResourceLocation, FallbackResourceManager.EntryStack> var1
   ) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         java.util.Map.Entry var3 = (java.util.Map.Entry)var2.next();
         ResourceLocation var4 = (ResourceLocation)var3.getKey();
         FallbackResourceManager.EntryStack var5 = (FallbackResourceManager.EntryStack)var3.getValue();
         if (var0.isFiltered(var4)) {
            var2.remove();
         } else if (var0.isFiltered(var5.metadataLocation())) {
            var5.entries.forEach(FallbackResourceManager.SinglePackResourceThunkSupplier::ignoreMeta);
         }
      }
   }

   private void listPackResources(
      FallbackResourceManager.PackEntry var1, String var2, Predicate<ResourceLocation> var3, Map<ResourceLocation, FallbackResourceManager.EntryStack> var4
   ) {
      PackResources var5 = var1.resources;
      if (var5 != null) {
         for(ResourceLocation var7 : var5.getResources(this.type, this.namespace, var2, var3)) {
            ResourceLocation var8 = getMetadataLocation(var7);
            var4.computeIfAbsent(var7, var1x -> new FallbackResourceManager.EntryStack(var8, Lists.newArrayList()))
               .entries()
               .add(new FallbackResourceManager.SinglePackResourceThunkSupplier(var7, var8, var5));
         }
      }
   }

   @Override
   public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2) {
      HashMap var3 = Maps.newHashMap();

      for(FallbackResourceManager.PackEntry var5 : this.fallbacks) {
         applyPackFiltersToExistingResources(var5, var3);
         this.listPackResources(var5, var1, var2, var3);
      }

      TreeMap var6 = Maps.newTreeMap();
      var3.forEach((var1x, var2x) -> var6.put(var1x, var2x.createThunks()));
      return var6;
   }

   @Override
   public Stream<PackResources> listPacks() {
      return this.fallbacks.stream().map(var0 -> var0.resources).filter(Objects::nonNull);
   }

   static ResourceLocation getMetadataLocation(ResourceLocation var0) {
      return new ResourceLocation(var0.getNamespace(), var0.getPath() + ".mcmeta");
   }

   static record EntryStack(ResourceLocation a, List<FallbackResourceManager.SinglePackResourceThunkSupplier> b) {
      private final ResourceLocation metadataLocation;
      final List<FallbackResourceManager.SinglePackResourceThunkSupplier> entries;

      EntryStack(ResourceLocation var1, List<FallbackResourceManager.SinglePackResourceThunkSupplier> var2) {
         super();
         this.metadataLocation = var1;
         this.entries = var2;
      }

      List<Resource> createThunks() {
         return this.entries().stream().map(FallbackResourceManager.SinglePackResourceThunkSupplier::create).toList();
      }
   }

   static class LeakedResourceWarningInputStream extends FilterInputStream {
      private final String message;
      private boolean closed;

      public LeakedResourceWarningInputStream(InputStream var1, ResourceLocation var2, String var3) {
         super(var1);
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         new Exception().printStackTrace(new PrintStream(var4));
         this.message = "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var4;
      }

      @Override
      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      @Override
      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }
   }

   static record PackEntry(String a, @Nullable PackResources b, @Nullable Predicate<ResourceLocation> c) {
      final String name;
      @Nullable
      final PackResources resources;
      @Nullable
      private final Predicate<ResourceLocation> filter;

      PackEntry(String var1, @Nullable PackResources var2, @Nullable Predicate<ResourceLocation> var3) {
         super();
         this.name = var1;
         this.resources = var2;
         this.filter = var3;
      }

      public void filterAll(Collection<ResourceLocation> var1) {
         if (this.filter != null) {
            var1.removeIf(this.filter);
         }
      }

      public boolean isFiltered(ResourceLocation var1) {
         return this.filter != null && this.filter.test(var1);
      }
   }

   class SinglePackResourceThunkSupplier {
      private final ResourceLocation location;
      private final ResourceLocation metadataLocation;
      private final PackResources source;
      private boolean shouldGetMeta = true;

      SinglePackResourceThunkSupplier(ResourceLocation var2, ResourceLocation var3, PackResources var4) {
         super();
         this.source = var4;
         this.location = var2;
         this.metadataLocation = var3;
      }

      public void ignoreMeta() {
         this.shouldGetMeta = false;
      }

      public Resource create() {
         String var1 = this.source.getName();
         return this.shouldGetMeta ? new Resource(var1, FallbackResourceManager.this.createResourceGetter(this.location, this.source), () -> {
            if (this.source.hasResource(FallbackResourceManager.this.type, this.metadataLocation)) {
               ResourceMetadata var2;
               try (InputStream var1x = this.source.getResource(FallbackResourceManager.this.type, this.metadataLocation)) {
                  var2 = ResourceMetadata.fromJsonStream(var1x);
               }

               return var2;
            } else {
               return ResourceMetadata.EMPTY;
            }
         }) : new Resource(var1, FallbackResourceManager.this.createResourceGetter(this.location, this.source));
      }
   }
}
