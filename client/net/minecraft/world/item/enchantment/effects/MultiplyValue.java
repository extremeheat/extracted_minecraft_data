package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record MultiplyValue(LevelBasedValue factor) implements EnchantmentValueEffect {
   public static final MapCodec<MultiplyValue> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("factor").forGetter(MultiplyValue::factor)).apply(var0, MultiplyValue::new);
   });

   public MultiplyValue(LevelBasedValue factor) {
      super();
      this.factor = factor;
   }

   public float process(ItemStack var1, int var2, RandomSource var3, float var4) {
      return var4 * this.factor.calculate(var2);
   }

   public MapCodec<MultiplyValue> codec() {
      return CODEC;
   }

   public LevelBasedValue factor() {
      return this.factor;
   }
}
