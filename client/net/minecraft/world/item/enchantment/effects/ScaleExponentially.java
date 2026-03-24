package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record ScaleExponentially(LevelBasedValue base, LevelBasedValue exponent) implements EnchantmentValueEffect {
   public static final MapCodec<ScaleExponentially> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("base").forGetter(ScaleExponentially::base), LevelBasedValue.CODEC.fieldOf("exponent").forGetter(ScaleExponentially::exponent)).apply(i, ScaleExponentially::new));

   public ScaleExponentially {
      super();
   }

   public float process(final int level, final RandomSource random, final float inputValue) {
      return (float)((double)inputValue * Math.pow((double)this.base.calculate(level), (double)this.exponent.calculate(level)));
   }

   public MapCodec<ScaleExponentially> codec() {
      return CODEC;
   }
}
