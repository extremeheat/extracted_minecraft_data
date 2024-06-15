package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record BinomialDistributionGenerator(NumberProvider n, NumberProvider p) implements NumberProvider {
   public static final MapCodec<BinomialDistributionGenerator> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               NumberProviders.CODEC.fieldOf("n").forGetter(BinomialDistributionGenerator::n),
               NumberProviders.CODEC.fieldOf("p").forGetter(BinomialDistributionGenerator::p)
            )
            .apply(var0, BinomialDistributionGenerator::new)
   );

   public BinomialDistributionGenerator(NumberProvider n, NumberProvider p) {
      super();
      this.n = n;
      this.p = p;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.BINOMIAL;
   }

   @Override
   public int getInt(LootContext var1) {
      int var2 = this.n.getInt(var1);
      float var3 = this.p.getFloat(var1);
      RandomSource var4 = var1.getRandom();
      int var5 = 0;

      for (int var6 = 0; var6 < var2; var6++) {
         if (var4.nextFloat() < var3) {
            var5++;
         }
      }

      return var5;
   }

   @Override
   public float getFloat(LootContext var1) {
      return (float)this.getInt(var1);
   }

   public static BinomialDistributionGenerator binomial(int var0, float var1) {
      return new BinomialDistributionGenerator(ConstantValue.exactly((float)var0), ConstantValue.exactly(var1));
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.n.getReferencedContextParams(), this.p.getReferencedContextParams());
   }
}
