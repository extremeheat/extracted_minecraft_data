package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record RemoveBinomial(LevelBasedValue chance) implements EnchantmentValueEffect {
   public static final MapCodec<RemoveBinomial> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(LevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomial::chance)).apply(var0, RemoveBinomial::new)
   );

   public RemoveBinomial(LevelBasedValue chance) {
      super();
      this.chance = chance;
   }

   @Override
   public float process(ItemStack var1, int var2, RandomSource var3, float var4) {
      float var5 = this.chance.calculate(var2);
      int var6 = 0;

      for (int var7 = 0; (float)var7 < var4; var7++) {
         if (var3.nextFloat() < var5) {
            var6++;
         }
      }

      return var4 - (float)var6;
   }

   @Override
   public MapCodec<RemoveBinomial> codec() {
      return CODEC;
   }
}
