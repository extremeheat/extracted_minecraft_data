package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public interface EnchantmentProviderTypes {
   static void bootstrap(final WritableRegistry<MapCodec<? extends EnchantmentProvider>> registry) {
      Registry.register(registry, (String)"by_cost", EnchantmentsByCost.CODEC);
      Registry.register(registry, (String)"by_cost_with_difficulty", EnchantmentsByCostWithDifficulty.CODEC);
      Registry.register(registry, (String)"single", SingleEnchantment.CODEC);
   }
}
