package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagNetworkSerialization;

public class RegistryDataCollector {
   @Nullable
   private RegistryDataCollector.ContentsCollector contentsCollector;
   @Nullable
   private TagCollector tagCollector;

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
         this.tagCollector = new TagCollector();
      }

      var1.forEach(this.tagCollector::append);
   }

   public RegistryAccess.Frozen collectGameRegistries(ResourceProvider var1, RegistryAccess var2, boolean var3) {
      LayeredRegistryAccess var5 = ClientRegistryLayer.createRegistryAccess();
      Object var4;
      if (this.contentsCollector != null) {
         RegistryAccess.Frozen var6 = var5.getAccessForLoading(ClientRegistryLayer.REMOTE);
         RegistryAccess.Frozen var7 = this.contentsCollector.loadRegistries(var1, var6).freeze();
         var4 = var5.replaceFrom(ClientRegistryLayer.REMOTE, var7).compositeAccess();
      } else {
         var4 = var2;
      }

      if (this.tagCollector != null) {
         this.tagCollector.updateTags((RegistryAccess)var4, var3);
      }

      return ((RegistryAccess)var4).freeze();
   }

   static class ContentsCollector {
      private final Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> elements = new HashMap<>();

      ContentsCollector() {
         super();
      }

      public void append(ResourceKey<? extends Registry<?>> var1, List<RegistrySynchronization.PackedRegistryEntry> var2) {
         this.elements.computeIfAbsent(var1, var0 -> new ArrayList<>()).addAll(var2);
      }

      public RegistryAccess loadRegistries(ResourceProvider var1, RegistryAccess var2) {
         return RegistryDataLoader.load(this.elements, var1, var2, RegistryDataLoader.SYNCHRONIZED_REGISTRIES);
      }
   }
}
