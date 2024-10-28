package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;

public interface EnchantmentValueEffect {
   Codec<EnchantmentValueEffect> CODEC = BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentValueEffect::codec, Function.identity());

   static MapCodec<? extends EnchantmentValueEffect> bootstrap(Registry<MapCodec<? extends EnchantmentValueEffect>> var0) {
      Registry.register(var0, (String)"add", AddValue.CODEC);
      Registry.register(var0, (String)"all_of", AllOf.ValueEffects.CODEC);
      Registry.register(var0, (String)"multiply", MultiplyValue.CODEC);
      Registry.register(var0, (String)"remove_binomial", RemoveBinomial.CODEC);
      return (MapCodec)Registry.register(var0, (String)"set", SetValue.CODEC);
   }

   float process(int var1, RandomSource var2, float var3);

   MapCodec<? extends EnchantmentValueEffect> codec();
}
