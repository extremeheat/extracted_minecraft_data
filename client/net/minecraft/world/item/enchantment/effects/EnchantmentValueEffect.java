package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public interface EnchantmentValueEffect {
   Codec<EnchantmentValueEffect> CODEC = BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE
      .byNameCodec()
      .dispatch(EnchantmentValueEffect::codec, Function.identity());

   static MapCodec<? extends EnchantmentValueEffect> bootstrap(Registry<MapCodec<? extends EnchantmentValueEffect>> var0) {
      Registry.register(var0, "add", AddValue.CODEC);
      Registry.register(var0, "all_of", AllOf.ValueEffects.CODEC);
      Registry.register(var0, "multiply", MultiplyValue.CODEC);
      Registry.register(var0, "remove_binomial", RemoveBinomial.CODEC);
      return Registry.register(var0, "set", SetValue.CODEC);
   }

   float process(ItemStack var1, int var2, RandomSource var3, float var4);

   MapCodec<? extends EnchantmentValueEffect> codec();
}
