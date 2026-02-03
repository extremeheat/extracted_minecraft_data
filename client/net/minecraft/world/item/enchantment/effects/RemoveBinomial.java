package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record RemoveBinomial(LevelBasedValue chance) implements EnchantmentValueEffect {
   public static final MapCodec<RemoveBinomial> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomial::chance)).apply(i, RemoveBinomial::new));

   public RemoveBinomial {
      super();
   }

   public float process(final int level, final RandomSource random, final float n) {
      float p = this.chance.calculate(level);
      int drop = 0;
      if (!(n <= 128.0F) && !(n * p < 20.0F) && !(n * (1.0F - p) < 20.0F)) {
         double miu = Math.floor((double)(n * p));
         double sigma = Math.sqrt((double)(n * p * (1.0F - p)));
         drop = (int)Math.round(miu + random.nextGaussian() * sigma);
         drop = Math.clamp((long)drop, 0, (int)n);
      } else {
         for(int y = 0; (float)y < n; ++y) {
            if (random.nextFloat() < p) {
               ++drop;
            }
         }
      }

      return n - (float)drop;
   }

   public MapCodec<RemoveBinomial> codec() {
      return CODEC;
   }
}
