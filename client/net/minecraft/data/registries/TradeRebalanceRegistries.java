package net.minecraft.data.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.providers.TradeRebalanceEnchantmentProviders;

public class TradeRebalanceRegistries {
   private static final RegistrySetBuilder BUILDER;

   public TradeRebalanceRegistries() {
      super();
   }

   public static CompletableFuture<RegistrySetBuilder.PatchedRegistries> createLookup(CompletableFuture<HolderLookup.Provider> var0) {
      return RegistryPatchGenerator.createLookup(var0, BUILDER);
   }

   static {
      BUILDER = (new RegistrySetBuilder()).add(Registries.ENCHANTMENT_PROVIDER, TradeRebalanceEnchantmentProviders::bootstrap);
   }
}
