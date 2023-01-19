package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

public class FallbackResourceManager implements ResourceManager {
   static final Logger LOGGER = LogUtils.getLogger();
   protected final List<FallbackResourceManager.PackEntry> fallbacks = Lists.newArrayList();
   private final PackType type;
   private final String namespace;

   public FallbackResourceManager(PackType var1, String var2) {
      super();
      this.type = var1;
      this.namespace = var2;
   }

   public void push(PackResources var1) {
      this.pushInternal(var1.packId(), var1, null);
   }

   public void push(PackResources var1, Predicate<ResourceLocation> var2) {
      this.pushInternal(var1.packId(), var1, var2);
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
      for(int var2 = this.fallbacks.size() - 1; var2 >= 0; --var2) {
         FallbackResourceManager.PackEntry var3 = this.fallbacks.get(var2);
         PackResources var4 = var3.resources;
         if (var4 != null) {
            IoSupplier var5 = var4.getResource(this.type, var1);
            if (var5 != null) {
               IoSupplier var6 = this.createStackMetadataFinder(var1, var2);
               return Optional.of(createResource(var4, var1, var5, var6));
            }
         }

         if (var3.isFiltered(var1)) {
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", var1, var3.name);
            return Optional.empty();
         }
      }

      return Optional.empty();
   }

   private static Resource createResource(PackResources var0, ResourceLocation var1, IoSupplier<InputStream> var2, IoSupplier<ResourceMetadata> var3) {
      return new Resource(var0, wrapForDebug(var1, var0, var2), var3);
   }

   private static IoSupplier<InputStream> wrapForDebug(ResourceLocation var0, PackResources var1, IoSupplier<InputStream> var2) {
      return LOGGER.isDebugEnabled() ? () -> new FallbackResourceManager.LeakedResourceWarningInputStream((InputStream)var2.get(), var0, var1.packId()) : var2;
   }

   @Override
   public List<Resource> getResourceStack(ResourceLocation var1) {
      ResourceLocation var2 = getMetadataLocation(var1);
      ArrayList var3 = new ArrayList();
      boolean var4 = false;
      String var5 = null;

      for(int var6 = this.fallbacks.size() - 1; var6 >= 0; --var6) {
         FallbackResourceManager.PackEntry var7 = this.fallbacks.get(var6);
         PackResources var8 = var7.resources;
         if (var8 != null) {
            IoSupplier var9 = var8.getResource(this.type, var1);
            if (var9 != null) {
               IoSupplier var10;
               if (var4) {
                  var10 = ResourceMetadata.EMPTY_SUPPLIER;
               } else {
                  var10 = () -> {
                     IoSupplier var3x = var8.getResource(this.type, var2);
                     return var3x != null ? parseMetadata(var3x) : ResourceMetadata.EMPTY;
                  };
               }

               var3.add(new Resource(var8, var9, var10));
            }
         }

         if (var7.isFiltered(var1)) {
            var5 = var7.name;
            break;
         }

         if (var7.isFiltered(var2)) {
            var4 = true;
         }
      }

      if (var3.isEmpty() && var5 != null) {
         LOGGER.warn("Resource {} not found, but was filtered by pack {}", var1, var5);
      }

      return Lists.reverse(var3);
   }

   private static boolean isMetadata(ResourceLocation var0) {
      return var0.getPath().endsWith(".mcmeta");
   }

   private static ResourceLocation getResourceLocationFromMetadata(ResourceLocation var0) {
      String var1 = var0.getPath().substring(0, var0.getPath().length() - ".mcmeta".length());
      return var0.withPath(var1);
   }

   static ResourceLocation getMetadataLocation(ResourceLocation var0) {
      return var0.withPath(var0.getPath() + ".mcmeta");
   }

   @Override
   public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2) {
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      int var5 = this.fallbacks.size();

      for(int var6 = 0; var6 < var5; ++var6) {
         FallbackResourceManager.PackEntry var7 = this.fallbacks.get(var6);
         var7.filterAll(var3.keySet());
         var7.filterAll(var4.keySet());
         PackResources var8 = var7.resources;
         if (var8 != null) {
            int var9 = var6;
            var8.listResources(this.type, this.namespace, var1, (var5x, var6x) -> {
               if (isMetadata(var5x)) {
                  if (var2.test(getResourceLocationFromMetadata(var5x))) {
                     var4.put(var5x, new 1ResourceWithSourceAndIndex(var8, var6x, var9));
                  }
               } else if (var2.test(var5x)) {
                  var3.put(var5x, new 1ResourceWithSourceAndIndex(var8, var6x, var9));
               }
            });
         }
      }

      TreeMap var10 = Maps.newTreeMap();
      var3.forEach((var2x, var3x) -> {
         ResourceLocation var5x = getMetadataLocation(var2x);
         1ResourceWithSourceAndIndex var6x = (1ResourceWithSourceAndIndex)var4.get(var5x);
         IoSupplier var4x;
         if (var6x != null && var6x.packIndex >= var3x.packIndex) {
            var4x = convertToMetadata(var6x.resource);
         } else {
            var4x = ResourceMetadata.EMPTY_SUPPLIER;
         }

         var10.put(var2x, createResource(var3x.packResources, var2x, var3x.resource, var4x));
      });
      return var10;

      record 1ResourceWithSourceAndIndex(PackResources a, IoSupplier<InputStream> b, int c) {
         final PackResources packResources;
         final IoSupplier<InputStream> resource;
         final int packIndex;

         _ResourceWithSourceAndIndex/* $QF was: 1ResourceWithSourceAndIndex*/(PackResources var1, IoSupplier<InputStream> var2, int var3) {
            super();
            this.packResources = var1;
            this.resource = var2;
            this.packIndex = var3;
         }
      }

   }

   private IoSupplier<ResourceMetadata> createStackMetadataFinder(ResourceLocation var1, int var2) {
      return () -> {
         ResourceLocation var3 = getMetadataLocation(var1);

         for(int var4 = this.fallbacks.size() - 1; var4 >= var2; --var4) {
            FallbackResourceManager.PackEntry var5 = this.fallbacks.get(var4);
            PackResources var6 = var5.resources;
            if (var6 != null) {
               IoSupplier var7 = var6.getResource(this.type, var3);
               if (var7 != null) {
                  return parseMetadata(var7);
               }
            }

            if (var5.isFiltered(var3)) {
               break;
            }
         }

         return ResourceMetadata.EMPTY;
      };
   }

   private static IoSupplier<ResourceMetadata> convertToMetadata(IoSupplier<InputStream> var0) {
      return () -> parseMetadata(var0);
   }

   private static ResourceMetadata parseMetadata(IoSupplier<InputStream> var0) throws IOException {
      ResourceMetadata var2;
      try (InputStream var1 = (InputStream)var0.get()) {
         var2 = ResourceMetadata.fromJsonStream(var1);
      }

      return var2;
   }

   private static void applyPackFiltersToExistingResources(
      FallbackResourceManager.PackEntry var0, Map<ResourceLocation, FallbackResourceManager.EntryStack> var1
   ) {
      for(FallbackResourceManager.EntryStack var3 : var1.values()) {
         if (var0.isFiltered(var3.fileLocation)) {
            var3.fileSources.clear();
         } else if (var0.isFiltered(var3.metadataLocation())) {
            var3.metaSources.clear();
         }
      }
   }

   private void listPackResources(
      FallbackResourceManager.PackEntry var1, String var2, Predicate<ResourceLocation> var3, Map<ResourceLocation, FallbackResourceManager.EntryStack> var4
   ) {
      PackResources var5 = var1.resources;
      if (var5 != null) {
         var5.listResources(
            this.type,
            this.namespace,
            var2,
            (var3x, var4x) -> {
               if (isMetadata(var3x)) {
                  ResourceLocation var5x = getResourceLocationFromMetadata(var3x);
                  if (!var3.test(var5x)) {
                     return;
                  }
   
                  var4.computeIfAbsent(var5x, FallbackResourceManager.EntryStack::new).metaSources.put(var5, var4x);
               } else {
                  if (!var3.test(var3x)) {
                     return;
                  }
   
                  var4.computeIfAbsent(var3x, FallbackResourceManager.EntryStack::new)
                     .fileSources
                     .add(new FallbackResourceManager.ResourceWithSource(var5, var4x));
               }
            }
         );
      }
   }

   @Override
   public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2) {
      HashMap var3 = Maps.newHashMap();

      for(FallbackResourceManager.PackEntry var5 : this.fallbacks) {
         applyPackFiltersToExistingResources(var5, var3);
         this.listPackResources(var5, var1, var2, var3);
      }

      TreeMap var13 = Maps.newTreeMap();

      for(FallbackResourceManager.EntryStack var6 : var3.values()) {
         if (!var6.fileSources.isEmpty()) {
            ArrayList var7 = new ArrayList();

            for(FallbackResourceManager.ResourceWithSource var9 : var6.fileSources) {
               PackResources var10 = var9.source;
               IoSupplier var11 = var6.metaSources.get(var10);
               IoSupplier var12 = var11 != null ? convertToMetadata(var11) : ResourceMetadata.EMPTY_SUPPLIER;
               var7.add(createResource(var10, var6.fileLocation, var9.resource, var12));
            }

            var13.put(var6.fileLocation, var7);
         }
      }

      return var13;
   }

   @Override
   public Stream<PackResources> listPacks() {
      return this.fallbacks.stream().map(var0 -> var0.resources).filter(Objects::nonNull);
   }

   static record EntryStack(
      ResourceLocation a, ResourceLocation b, List<FallbackResourceManager.ResourceWithSource> c, Map<PackResources, IoSupplier<InputStream>> d
   ) {
      final ResourceLocation fileLocation;
      private final ResourceLocation metadataLocation;
      final List<FallbackResourceManager.ResourceWithSource> fileSources;
      final Map<PackResources, IoSupplier<InputStream>> metaSources;

      EntryStack(ResourceLocation var1) {
         this(var1, FallbackResourceManager.getMetadataLocation(var1), new ArrayList<>(), new Object2ObjectArrayMap());
      }

      private EntryStack(
         ResourceLocation var1, ResourceLocation var2, List<FallbackResourceManager.ResourceWithSource> var3, Map<PackResources, IoSupplier<InputStream>> var4
      ) {
         super();
         this.fileLocation = var1;
         this.metadataLocation = var2;
         this.fileSources = var3;
         this.metaSources = var4;
      }
   }

   static class LeakedResourceWarningInputStream extends FilterInputStream {
      private final Supplier<String> message;
      private boolean closed;

      public LeakedResourceWarningInputStream(InputStream var1, ResourceLocation var2, String var3) {
         super(var1);
         Exception var4 = new Exception("Stacktrace");
         this.message = () -> {
            StringWriter var3x = new StringWriter();
            var4.printStackTrace(new PrintWriter(var3x));
            return "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var3x;
         };
      }

      @Override
      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      @Override
      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn("{}", this.message.get());
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

   static record ResourceWithSource(PackResources a, IoSupplier<InputStream> b) {
      final PackResources source;
      final IoSupplier<InputStream> resource;

      ResourceWithSource(PackResources var1, IoSupplier<InputStream> var2) {
         super();
         this.source = var1;
         this.resource = var2;
      }
   }
}
