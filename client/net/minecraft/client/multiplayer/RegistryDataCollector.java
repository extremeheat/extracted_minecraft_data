package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagNetworkSerialization;

public class RegistryDataCollector {
   @Nullable
   private RegistryDataCollector.ContentsCollector contentsCollector;
   @Nullable
   private RegistryDataCollector.TagCollector tagCollector;

   public RegistryDataCollector() {
      super();
   }

   public void appendContents(ResourceKey<? extends Registry<?>> var1, List<RegistrySynchronization.PackedRegistryEntry> var2) {
      if (this.contentsCollector == null) {
         this.contentsCollector = new RegistryDataCollector.ContentsCollector();
      }

      this.contentsCollector.append(var1, var2);
   }

   public void appendTags(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> var1) {
      if (this.tagCollector == null) {
         this.tagCollector = new RegistryDataCollector.TagCollector();
      }

      var1.forEach(this.tagCollector::append);
   }

   private static <T> Registry.PendingTags<T> resolveRegistryTags(
      RegistryAccess.Frozen var0, ResourceKey<? extends Registry<? extends T>> var1, TagNetworkSerialization.NetworkPayload var2
   ) {
      Registry var3 = var0.registryOrThrow(var1);
      return var3.prepareTagReload(var2.resolve(var3));
   }

   private RegistryAccess loadNewElementsAndTags(ResourceProvider var1, RegistryDataCollector.ContentsCollector var2, boolean var3) {
      LayeredRegistryAccess var5 = ClientRegistryLayer.createRegistryAccess();
      RegistryAccess.Frozen var6 = var5.getAccessForLoading(ClientRegistryLayer.REMOTE);
      HashMap var7 = new HashMap();
      var2.elements
         .forEach(
            (var1x, var2x) -> var7.put(
                  var1x,
                  new RegistryDataLoader.NetworkedRegistryData(
                     (List<RegistrySynchronization.PackedRegistryEntry>)var2x, TagNetworkSerialization.NetworkPayload.EMPTY
                  )
               )
         );
      ArrayList var8 = new ArrayList();
      if (this.tagCollector != null) {
         this.tagCollector.forEach((var4x, var5x) -> {
            if (!var5x.isEmpty()) {
               if (RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)var4x)) {
                  var7.compute(var4x, (var1xx, var2xx) -> {
                     List var3xx = var2xx != null ? var2xx.elements() : List.of();
                     return new RegistryDataLoader.NetworkedRegistryData(var3xx, var5x);
                  });
               } else if (!var3) {
                  var8.add(resolveRegistryTags(var6, (ResourceKey<? extends Registry<?>>)var4x, var5x));
               }
            }
         });
      }

      List var9 = TagLoader.buildUpdatedLookups(var6, var8);
      RegistryAccess.Frozen var10 = RegistryDataLoader.load(var7, var1, var9, RegistryDataLoader.SYNCHRONIZED_REGISTRIES).freeze();
      RegistryAccess.Frozen var4 = var5.replaceFrom(ClientRegistryLayer.REMOTE, var10).compositeAccess();
      var8.forEach(Registry.PendingTags::apply);
      return var4;
   }

   private void loadOnlyTags(RegistryDataCollector.TagCollector var1, RegistryAccess.Frozen var2, boolean var3) {
      var1.forEach((var2x, var3x) -> {
         if (var3 || RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)var2x)) {
            resolveRegistryTags(var2, (ResourceKey<? extends Registry<?>>)var2x, var3x).apply();
         }
      });
   }

   public RegistryAccess.Frozen collectGameRegistries(ResourceProvider var1, RegistryAccess.Frozen var2, boolean var3) {
      Object var4;
      if (this.contentsCollector != null) {
         var4 = this.loadNewElementsAndTags(var1, this.contentsCollector, var3);
      } else {
         if (this.tagCollector != null) {
            this.loadOnlyTags(this.tagCollector, var2, !var3);
         }

         var4 = var2;
      }

      return ((RegistryAccess)var4).freeze();
   }

   static class ContentsCollector {
      final Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> elements = new HashMap<>();

      ContentsCollector() {
         super();
      }

      public void append(ResourceKey<? extends Registry<?>> var1, List<RegistrySynchronization.PackedRegistryEntry> var2) {
         this.elements.computeIfAbsent(var1, var0 -> new ArrayList<>()).addAll(var2);
      }
   }

   static class TagCollector {
      private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags = new HashMap<>();

      TagCollector() {
         super();
      }

      public void append(ResourceKey<? extends Registry<?>> var1, TagNetworkSerialization.NetworkPayload var2) {
         this.tags.put(var1, var2);
      }

      public void forEach(BiConsumer<? super ResourceKey<? extends Registry<?>>, ? super TagNetworkSerialization.NetworkPayload> var1) {
         this.tags.forEach(var1);
      }
   }
}
