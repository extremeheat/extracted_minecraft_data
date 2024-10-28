package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record MultiplyValue(LevelBasedValue factor) implements EnchantmentValueEffect {
   public static final MapCodec<MultiplyValue> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("factor").forGetter(MultiplyValue::factor)).apply(var0, MultiplyValue::new);
   });

   public MultiplyValue(LevelBasedValue var1) {
      super();
      this.factor = var1;
   }

   public float process(int var1, RandomSource var2, float var3) {
      return var3 * this.factor.calculate(var1);
   }

   public MapCodec<MultiplyValue> codec() {
      return CODEC;
   }

   public LevelBasedValue factor() {
      return this.factor;
   }
}
