package net.minecraft.client.multiplayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class TagCollector {
   private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags = new HashMap();

   public TagCollector() {
      super();
   }

   public void append(ResourceKey<? extends Registry<?>> var1, TagNetworkSerialization.NetworkPayload var2) {
      this.tags.put(var1, var2);
   }

   private static void refreshBuiltInTagDependentData() {
      AbstractFurnaceBlockEntity.invalidateCache();
      Blocks.rebuildCache();
   }

   private void applyTags(RegistryAccess var1, Predicate<ResourceKey<? extends Registry<?>>> var2) {
      this.tags.forEach((var2x, var3) -> {
         if (var2.test(var2x)) {
            var3.applyToRegistry(var1.registryOrThrow(var2x));
         }

      });
   }

   public void updateTags(RegistryAccess var1, boolean var2) {
      if (var2) {
         Set var10002 = RegistrySynchronization.NETWORKABLE_REGISTRIES;
         Objects.requireNonNull(var10002);
         this.applyTags(var1, var10002::contains);
      } else {
         var1.registries().filter((var0) -> {
            return !RegistrySynchronization.NETWORKABLE_REGISTRIES.contains(var0.key());
         }).forEach((var0) -> {
            var0.value().resetTags();
         });
         this.applyTags(var1, (var0) -> {
            return true;
         });
         refreshBuiltInTagDependentData();
      }

   }
}
