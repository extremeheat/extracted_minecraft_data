package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record ScaleExponentially(LevelBasedValue base, LevelBasedValue exponent) implements EnchantmentValueEffect {
   public static final MapCodec<ScaleExponentially> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LevelBasedValue.CODEC.fieldOf("base").forGetter(ScaleExponentially::base), LevelBasedValue.CODEC.fieldOf("exponent").forGetter(ScaleExponentially::exponent)).apply(var0, ScaleExponentially::new));

   public ScaleExponentially(LevelBasedValue var1, LevelBasedValue var2) {
      super();
      this.base = var1;
      this.exponent = var2;
   }

   public float process(int var1, RandomSource var2, float var3) {
      return (float)((double)var3 * Math.pow((double)this.base.calculate(var1), (double)this.exponent.calculate(var1)));
   }

   public MapCodec<ScaleExponentially> codec() {
      return CODEC;
   }
}
