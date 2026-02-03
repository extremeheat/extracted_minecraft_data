package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record BinomialDistributionGenerator(NumberProvider n, NumberProvider p) implements NumberProvider {
   public static final MapCodec<BinomialDistributionGenerator> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(NumberProviders.CODEC.fieldOf("n").forGetter(BinomialDistributionGenerator::n), NumberProviders.CODEC.fieldOf("p").forGetter(BinomialDistributionGenerator::p)).apply(i, BinomialDistributionGenerator::new));

   public BinomialDistributionGenerator {
      super();
   }

   public MapCodec<BinomialDistributionGenerator> codec() {
      return MAP_CODEC;
   }

   public int getInt(final LootContext context) {
      int n = this.n.getInt(context);
      float p = this.p.getFloat(context);
      RandomSource random = context.getRandom();
      int result = 0;

      for(int i = 0; i < n; ++i) {
         if (random.nextFloat() < p) {
            ++result;
         }
      }

      return result;
   }

   public float getFloat(final LootContext context) {
      return (float)this.getInt(context);
   }

   public static BinomialDistributionGenerator binomial(final int n, final float p) {
      return new BinomialDistributionGenerator(ConstantValue.exactly((float)n), ConstantValue.exactly(p));
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validate(context, "n", this.n);
      Validatable.validate(context, "p", this.p);
   }
}
