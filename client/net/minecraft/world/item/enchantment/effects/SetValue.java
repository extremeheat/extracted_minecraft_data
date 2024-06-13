package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record SetValue(LevelBasedValue value) implements EnchantmentValueEffect {
   public static final MapCodec<SetValue> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(SetValue::value)).apply(var0, SetValue::new)
   );

   public SetValue(LevelBasedValue value) {
      super();
      this.value = value;
   }

   @Override
   public float process(ItemStack var1, int var2, RandomSource var3, float var4) {
      return this.value.calculate(var2);
   }

   @Override
   public MapCodec<SetValue> codec() {
      return CODEC;
   }
}
