package net.minecraft.data.tags;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class EnchantmentTagsProvider extends IntrinsicHolderTagsProvider<Enchantment> {
   private final FeatureFlagSet enabledFeatures;

   public EnchantmentTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, FeatureFlagSet var3) {
      super(var1, Registries.ENCHANTMENT, var2, var0 -> var0.builtInRegistryHolder().key());
      this.enabledFeatures = var3;
   }

   protected void tooltipOrder(HolderLookup.Provider var1, Enchantment... var2) {
      this.tag(EnchantmentTags.TOOLTIP_ORDER).add(var2);
      Set var3 = Set.of(var2);
      List var4 = var1.lookupOrThrow(Registries.ENCHANTMENT)
         .listElements()
         .filter(var1x -> var1x.value().requiredFeatures().isSubsetOf(this.enabledFeatures))
         .filter(var1x -> !var3.contains(var1x.value()))
         .map(Holder::getRegisteredName)
         .collect(Collectors.toList());
      if (!var4.isEmpty()) {
         throw new IllegalStateException("Not all enchantments were registered for tooltip ordering. Missing: " + String.join(", ", var4));
      }
   }
}
