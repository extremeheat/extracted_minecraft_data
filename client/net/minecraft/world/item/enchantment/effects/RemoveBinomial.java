package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record RemoveBinomial(LevelBasedValue chance) implements EnchantmentValueEffect {
   public static final MapCodec<RemoveBinomial> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomial::chance)).apply(var0, RemoveBinomial::new);
   });

   public RemoveBinomial(LevelBasedValue var1) {
      super();
      this.chance = var1;
   }

   public float process(int var1, RandomSource var2, float var3) {
      float var4 = this.chance.calculate(var1);
      int var5 = 0;

      for(int var6 = 0; (float)var6 < var3; ++var6) {
         if (var2.nextFloat() < var4) {
            ++var5;
         }
      }

      return var3 - (float)var5;
   }

   public MapCodec<RemoveBinomial> codec() {
      return CODEC;
   }

   public LevelBasedValue chance() {
      return this.chance;
   }
}
