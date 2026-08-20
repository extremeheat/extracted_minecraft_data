package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record BinomialDistributionGenerator(Holder<NumberProvider> n, Holder<NumberProvider> p) implements NumberProvider {
   public static final MapCodec<BinomialDistributionGenerator> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(NumberProviders.CODEC.fieldOf("n").forGetter(BinomialDistributionGenerator::n), NumberProviders.CODEC.fieldOf("p").forGetter(BinomialDistributionGenerator::p)).apply(i, BinomialDistributionGenerator::new));

   public BinomialDistributionGenerator {
      super();
   }

   public MapCodec<BinomialDistributionGenerator> codec() {
      return MAP_CODEC;
   }

   public int getInt(final LootContext context) {
      int n = ((NumberProvider)this.n.value()).getInt(context);
      float p = ((NumberProvider)this.p.value()).getFloat(context);
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

   public static Holder<NumberProvider> binomial(final int n, final float p) {
      return Holder.<NumberProvider>direct(new BinomialDistributionGenerator(ConstantValue.exactly((float)n), ConstantValue.exactly(p)));
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validateHolder(context, "n", this.n);
      Validatable.validateHolder(context, "p", this.p);
   }
}
