package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
   protected final List<PackEntry> fallbacks = Lists.newArrayList();
   final PackType type;
   private final String namespace;

   public FallbackResourceManager(PackType var1, String var2) {
      super();
      this.type = var1;
      this.namespace = var2;
   }

   public void push(PackResources var1) {
      this.pushInternal(var1.getName(), var1, (Predicate)null);
   }

   public void push(PackResources var1, Predicate<ResourceLocation> var2) {
      this.pushInternal(var1.getName(), var1, var2);
   }

   public void pushFilterOnly(String var1, Predicate<ResourceLocation> var2) {
      this.pushInternal(var1, (PackResources)null, var2);
   }

   private void pushInternal(String var1, @Nullable PackResources var2, @Nullable Predicate<ResourceLocation> var3) {
      this.fallbacks.add(new PackEntry(var1, var2, var3));
   }

   public Set<String> getNamespaces() {
      return ImmutableSet.of(this.namespace);
   }

   public Optional<Resource> getResource(ResourceLocation var1) {
      if (!this.isValidLocation(var1)) {
         return Optional.empty();
      } else {
         for(int var2 = this.fallbacks.size() - 1; var2 >= 0; --var2) {
            PackEntry var3 = (PackEntry)this.fallbacks.get(var2);
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
         return new LeakedResourceWarningInputStream(var3, var1, var2.getName());
      } : () -> {
         return var2.getResource(this.type, var1);
      };
   }

   private boolean isValidLocation(ResourceLocation var1) {
      return !var1.getPath().contains("..");
   }

   public List<Resource> getResourceStack(ResourceLocation var1) {
      if (!this.isValidLocation(var1)) {
         return List.of();
      } else {
         ArrayList var2 = Lists.newArrayList();
         ResourceLocation var3 = getMetadataLocation(var1);
         String var4 = null;
         Iterator var5 = this.fallbacks.iterator();

         while(var5.hasNext()) {
            PackEntry var6 = (PackEntry)var5.next();
            if (var6.isFiltered(var1)) {
               if (!var2.isEmpty()) {
                  var4 = var6.name;
               }

               var2.clear();
            } else if (var6.isFiltered(var3)) {
               var2.forEach(SinglePackResourceThunkSupplier::ignoreMeta);
            }

            PackResources var7 = var6.resources;
            if (var7 != null && var7.hasResource(this.type, var1)) {
               var2.add(new SinglePackResourceThunkSupplier(var1, var3, var7));
            }
         }

         if (var2.isEmpty() && var4 != null) {
            LOGGER.info("Resource {} was filtered by pack {}", var1, var4);
         }

         return var2.stream().map(SinglePackResourceThunkSupplier::create).toList();
      }
   }

   public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      int var4 = this.fallbacks.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         PackEntry var6 = (PackEntry)this.fallbacks.get(var5);
         var6.filterAll(var3.keySet());
         if (var6.resources != null) {
            Iterator var7 = var6.resources.getResources(this.type, this.namespace, var1, var2).iterator();

            while(var7.hasNext()) {
               ResourceLocation var8 = (ResourceLocation)var7.next();
               var3.put(var8, var5);
            }
         }
      }

      TreeMap var11 = Maps.newTreeMap();
      ObjectIterator var12 = Object2IntMaps.fastIterable(var3).iterator();

      while(var12.hasNext()) {
         Object2IntMap.Entry var13 = (Object2IntMap.Entry)var12.next();
         int var14 = var13.getIntValue();
         ResourceLocation var9 = (ResourceLocation)var13.getKey();
         PackResources var10 = ((PackEntry)this.fallbacks.get(var14)).resources;
         var11.put(var9, new Resource(var10.getName(), this.createResourceGetter(var9, var10), this.createStackMetadataFinder(var9, var14)));
      }

      return var11;
   }

   private Resource.IoSupplier<ResourceMetadata> createStackMetadataFinder(ResourceLocation var1, int var2) {
      return () -> {
         ResourceLocation var3 = getMetadataLocation(var1);

         for(int var4 = this.fallbacks.size() - 1; var4 >= var2; --var4) {
            PackEntry var5 = (PackEntry)this.fallbacks.get(var4);
            PackResources var6 = var5.resources;
            if (var6 != null && var6.hasResource(this.type, var3)) {
               InputStream var7 = var6.getResource(this.type, var3);

               ResourceMetadata var8;
               try {
                  var8 = ResourceMetadata.fromJsonStream(var7);
               } catch (Throwable var11) {
                  if (var7 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (var7 != null) {
                  var7.close();
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

   private static void applyPackFiltersToExistingResources(PackEntry var0, Map<ResourceLocation, EntryStack> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         ResourceLocation var4 = (ResourceLocation)var3.getKey();
         EntryStack var5 = (EntryStack)var3.getValue();
         if (var0.isFiltered(var4)) {
            var2.remove();
         } else if (var0.isFiltered(var5.metadataLocation())) {
            var5.entries.forEach(SinglePackResourceThunkSupplier::ignoreMeta);
         }
      }

   }

   private void listPackResources(PackEntry var1, String var2, Predicate<ResourceLocation> var3, Map<ResourceLocation, EntryStack> var4) {
      PackResources var5 = var1.resources;
      if (var5 != null) {
         Iterator var6 = var5.getResources(this.type, this.namespace, var2, var3).iterator();

         while(var6.hasNext()) {
            ResourceLocation var7 = (ResourceLocation)var6.next();
            ResourceLocation var8 = getMetadataLocation(var7);
            ((EntryStack)var4.computeIfAbsent(var7, (var1x) -> {
               return new EntryStack(var8, Lists.newArrayList());
            })).entries().add(new SinglePackResourceThunkSupplier(var7, var8, var5));
         }

      }
   }

   public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2) {
      HashMap var3 = Maps.newHashMap();
      Iterator var4 = this.fallbacks.iterator();

      while(var4.hasNext()) {
         PackEntry var5 = (PackEntry)var4.next();
         applyPackFiltersToExistingResources(var5, var3);
         this.listPackResources(var5, var1, var2, var3);
      }

      TreeMap var6 = Maps.newTreeMap();
      var3.forEach((var1x, var2x) -> {
         var6.put(var1x, var2x.createThunks());
      });
      return var6;
   }

   public Stream<PackResources> listPacks() {
      return this.fallbacks.stream().map((var0) -> {
         return var0.resources;
      }).filter(Objects::nonNull);
   }

   static ResourceLocation getMetadataLocation(ResourceLocation var0) {
      return new ResourceLocation(var0.getNamespace(), var0.getPath() + ".mcmeta");
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

      public String name() {
         return this.name;
      }

      @Nullable
      public PackResources resources() {
         return this.resources;
      }

      @Nullable
      public Predicate<ResourceLocation> filter() {
         return this.filter;
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
               InputStream var1 = this.source.getResource(FallbackResourceManager.this.type, this.metadataLocation);

               ResourceMetadata var2;
               try {
                  var2 = ResourceMetadata.fromJsonStream(var1);
               } catch (Throwable var5) {
                  if (var1 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                     }
                  }

                  throw var5;
               }

               if (var1 != null) {
                  var1.close();
               }

               return var2;
            } else {
               return ResourceMetadata.EMPTY;
            }
         }) : new Resource(var1, FallbackResourceManager.this.createResourceGetter(this.location, this.source));
      }
   }

   static record EntryStack(ResourceLocation a, List<SinglePackResourceThunkSupplier> b) {
      private final ResourceLocation metadataLocation;
      final List<SinglePackResourceThunkSupplier> entries;

      EntryStack(ResourceLocation var1, List<SinglePackResourceThunkSupplier> var2) {
         super();
         this.metadataLocation = var1;
         this.entries = var2;
      }

      List<Resource> createThunks() {
         return this.entries().stream().map(SinglePackResourceThunkSupplier::create).toList();
      }

      public ResourceLocation metadataLocation() {
         return this.metadataLocation;
      }

      public List<SinglePackResourceThunkSupplier> entries() {
         return this.entries;
      }
   }

   static class LeakedResourceWarningInputStream extends FilterInputStream {
      private final String message;
      private boolean closed;

      public LeakedResourceWarningInputStream(InputStream var1, ResourceLocation var2, String var3) {
         super(var1);
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(var4));
         this.message = "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var4;
      }

      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }
   }
}
