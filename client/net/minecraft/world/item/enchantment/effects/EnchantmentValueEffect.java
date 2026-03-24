package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;

public interface EnchantmentValueEffect {
   Codec<EnchantmentValueEffect> CODEC = BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentValueEffect::codec, Function.identity());

   static MapCodec<? extends EnchantmentValueEffect> bootstrap(final Registry<MapCodec<? extends EnchantmentValueEffect>> registry) {
      Registry.register(registry, (String)"add", AddValue.CODEC);
      Registry.register(registry, (String)"all_of", AllOf.ValueEffects.CODEC);
      Registry.register(registry, (String)"multiply", MultiplyValue.CODEC);
      Registry.register(registry, (String)"remove_binomial", RemoveBinomial.CODEC);
      Registry.register(registry, (String)"exponential", ScaleExponentially.CODEC);
      return (MapCodec)Registry.register(registry, (String)"set", SetValue.CODEC);
   }

   float process(int enchantmentLevel, RandomSource random, float inputValue);

   MapCodec<? extends EnchantmentValueEffect> codec();
}
