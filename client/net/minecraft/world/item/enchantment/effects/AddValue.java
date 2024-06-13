package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record AddValue(LevelBasedValue value) implements EnchantmentValueEffect {
   public static final MapCodec<AddValue> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(AddValue::value)).apply(var0, AddValue::new)
   );

   public AddValue(LevelBasedValue value) {
      super();
      this.value = value;
   }

   @Override
   public float process(ItemStack var1, int var2, RandomSource var3, float var4) {
      return var4 + this.value.calculate(var2);
   }

   @Override
   public MapCodec<AddValue> codec() {
      return CODEC;
   }
}
