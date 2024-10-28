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
import java.util.Iterator;
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
   protected final List<PackEntry> fallbacks = Lists.newArrayList();
   private final PackType type;
   private final String namespace;

   public FallbackResourceManager(PackType var1, String var2) {
      super();
      this.type = var1;
      this.namespace = var2;
   }

   public void push(PackResources var1) {
      this.pushInternal(var1.packId(), var1, (Predicate)null);
   }

   public void push(PackResources var1, Predicate<ResourceLocation> var2) {
      this.pushInternal(var1.packId(), var1, var2);
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
      for(int var2 = this.fallbacks.size() - 1; var2 >= 0; --var2) {
         PackEntry var3 = (PackEntry)this.fallbacks.get(var2);
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
      return LOGGER.isDebugEnabled() ? () -> {
         return new LeakedResourceWarningInputStream((InputStream)var2.get(), var0, var1.packId());
      } : var2;
   }

   public List<Resource> getResourceStack(ResourceLocation var1) {
      ResourceLocation var2 = getMetadataLocation(var1);
      ArrayList var3 = new ArrayList();
      boolean var4 = false;
      String var5 = null;

      for(int var6 = this.fallbacks.size() - 1; var6 >= 0; --var6) {
         PackEntry var7 = (PackEntry)this.fallbacks.get(var6);
         PackResources var8 = var7.resources;
         if (var8 != null) {
            IoSupplier var9 = var8.getResource(this.type, var1);
            if (var9 != null) {
               IoSupplier var10;
               if (var4) {
                  var10 = ResourceMetadata.EMPTY_SUPPLIER;
               } else {
                  var10 = () -> {
                     IoSupplier var3 = var8.getResource(this.type, var2);
                     return var3 != null ? parseMetadata(var3) : ResourceMetadata.EMPTY;
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

   public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2) {
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      int var5 = this.fallbacks.size();

      for(int var6 = 0; var6 < var5; ++var6) {
         PackEntry var7 = (PackEntry)this.fallbacks.get(var6);
         var7.filterAll(var3.keySet());
         var7.filterAll(var4.keySet());
         PackResources var8 = var7.resources;
         if (var8 != null) {
            var8.listResources(this.type, this.namespace, var1, (var5x, var6x) -> {
               record 1ResourceWithSourceAndIndex(PackResources packResources, IoSupplier<InputStream> resource, int packIndex) {
                  final PackResources packResources;
                  final IoSupplier<InputStream> resource;
                  final int packIndex;

                  _ResourceWithSourceAndIndex/* $FF was: 1ResourceWithSourceAndIndex*/(PackResources packResources, IoSupplier<InputStream> resource, int packIndex) {
                     super();
                     this.packResources = packResources;
                     this.resource = resource;
                     this.packIndex = packIndex;
                  }

                  public PackResources packResources() {
                     return this.packResources;
                  }

                  public IoSupplier<InputStream> resource() {
                     return this.resource;
                  }

                  public int packIndex() {
                     return this.packIndex;
                  }
               }

               if (isMetadata(var5x)) {
                  if (var2.test(getResourceLocationFromMetadata(var5x))) {
                     var4.put(var5x, new 1ResourceWithSourceAndIndex(var8, var6x, var6));
                  }
               } else if (var2.test(var5x)) {
                  var3.put(var5x, new 1ResourceWithSourceAndIndex(var8, var6x, var6));
               }

            });
         }
      }

      TreeMap var10 = Maps.newTreeMap();
      var3.forEach((var2x, var3x) -> {
         ResourceLocation var5 = getMetadataLocation(var2x);
         1ResourceWithSourceAndIndex var6 = (1ResourceWithSourceAndIndex)var4.get(var5);
         IoSupplier var4x;
         if (var6 != null && var6.packIndex >= var3x.packIndex) {
            var4x = convertToMetadata(var6.resource);
         } else {
            var4x = ResourceMetadata.EMPTY_SUPPLIER;
         }

         var10.put(var2x, createResource(var3x.packResources, var2x, var3x.resource, var4x));
      });
      return var10;
   }

   private IoSupplier<ResourceMetadata> createStackMetadataFinder(ResourceLocation var1, int var2) {
      return () -> {
         ResourceLocation var3 = getMetadataLocation(var1);

         for(int var4 = this.fallbacks.size() - 1; var4 >= var2; --var4) {
            PackEntry var5 = (PackEntry)this.fallbacks.get(var4);
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
      return () -> {
         return parseMetadata(var0);
      };
   }

   private static ResourceMetadata parseMetadata(IoSupplier<InputStream> var0) throws IOException {
      InputStream var1 = (InputStream)var0.get();

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
   }

   private static void applyPackFiltersToExistingResources(PackEntry var0, Map<ResourceLocation, EntryStack> var1) {
      Iterator var2 = var1.values().iterator();

      while(var2.hasNext()) {
         EntryStack var3 = (EntryStack)var2.next();
         if (var0.isFiltered(var3.fileLocation)) {
            var3.fileSources.clear();
         } else if (var0.isFiltered(var3.metadataLocation())) {
            var3.metaSources.clear();
         }
      }

   }

   private void listPackResources(PackEntry var1, String var2, Predicate<ResourceLocation> var3, Map<ResourceLocation, EntryStack> var4) {
      PackResources var5 = var1.resources;
      if (var5 != null) {
         var5.listResources(this.type, this.namespace, var2, (var3x, var4x) -> {
            if (isMetadata(var3x)) {
               ResourceLocation var5x = getResourceLocationFromMetadata(var3x);
               if (!var3.test(var5x)) {
                  return;
               }

               ((EntryStack)var4.computeIfAbsent(var5x, EntryStack::new)).metaSources.put(var5, var4x);
            } else {
               if (!var3.test(var3x)) {
                  return;
               }

               ((EntryStack)var4.computeIfAbsent(var3x, EntryStack::new)).fileSources.add(new ResourceWithSource(var5, var4x));
            }

         });
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

      TreeMap var13 = Maps.newTreeMap();
      Iterator var14 = var3.values().iterator();

      while(true) {
         EntryStack var6;
         do {
            if (!var14.hasNext()) {
               return var13;
            }

            var6 = (EntryStack)var14.next();
         } while(var6.fileSources.isEmpty());

         ArrayList var7 = new ArrayList();
         Iterator var8 = var6.fileSources.iterator();

         while(var8.hasNext()) {
            ResourceWithSource var9 = (ResourceWithSource)var8.next();
            PackResources var10 = var9.source;
            IoSupplier var11 = (IoSupplier)var6.metaSources.get(var10);
            IoSupplier var12 = var11 != null ? convertToMetadata(var11) : ResourceMetadata.EMPTY_SUPPLIER;
            var7.add(createResource(var10, var6.fileLocation, var9.resource, var12));
         }

         var13.put(var6.fileLocation, var7);
      }
   }

   public Stream<PackResources> listPacks() {
      return this.fallbacks.stream().map((var0) -> {
         return var0.resources;
      }).filter(Objects::nonNull);
   }

   static record PackEntry(String name, @Nullable PackResources resources, @Nullable Predicate<ResourceLocation> filter) {
      final String name;
      @Nullable
      final PackResources resources;

      PackEntry(String name, @Nullable PackResources resources, @Nullable Predicate<ResourceLocation> filter) {
         super();
         this.name = name;
         this.resources = resources;
         this.filter = filter;
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

   static record EntryStack(ResourceLocation fileLocation, ResourceLocation metadataLocation, List<ResourceWithSource> fileSources, Map<PackResources, IoSupplier<InputStream>> metaSources) {
      final ResourceLocation fileLocation;
      final List<ResourceWithSource> fileSources;
      final Map<PackResources, IoSupplier<InputStream>> metaSources;

      EntryStack(ResourceLocation var1) {
         this(var1, FallbackResourceManager.getMetadataLocation(var1), new ArrayList(), new Object2ObjectArrayMap());
      }

      private EntryStack(ResourceLocation fileLocation, ResourceLocation metadataLocation, List<ResourceWithSource> fileSources, Map<PackResources, IoSupplier<InputStream>> metaSources) {
         super();
         this.fileLocation = fileLocation;
         this.metadataLocation = metadataLocation;
         this.fileSources = fileSources;
         this.metaSources = metaSources;
      }

      public ResourceLocation fileLocation() {
         return this.fileLocation;
      }

      public ResourceLocation metadataLocation() {
         return this.metadataLocation;
      }

      public List<ResourceWithSource> fileSources() {
         return this.fileSources;
      }

      public Map<PackResources, IoSupplier<InputStream>> metaSources() {
         return this.metaSources;
      }
   }

   private static record ResourceWithSource(PackResources source, IoSupplier<InputStream> resource) {
      final PackResources source;
      final IoSupplier<InputStream> resource;

      ResourceWithSource(PackResources source, IoSupplier<InputStream> resource) {
         super();
         this.source = source;
         this.resource = resource;
      }

      public PackResources source() {
         return this.source;
      }

      public IoSupplier<InputStream> resource() {
         return this.resource;
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
            return "Leaked resource: '" + String.valueOf(var2) + "' loaded from pack: '" + var3 + "'\n" + String.valueOf(var3x);
         };
      }

      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn("{}", this.message.get());
         }

         super.finalize();
      }
   }
}
