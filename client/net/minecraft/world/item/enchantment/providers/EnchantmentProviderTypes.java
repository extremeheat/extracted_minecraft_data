package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface EnchantmentProviderTypes {
   static MapCodec<? extends EnchantmentProvider> bootstrap(Registry<MapCodec<? extends EnchantmentProvider>> var0) {
      Registry.register(var0, "by_cost", EnchantmentsByCost.CODEC);
      Registry.register(var0, "by_cost_with_difficulty", EnchantmentsByCostWithDifficulty.CODEC);
      return Registry.register(var0, "single", SingleEnchantment.CODEC);
   }
}
