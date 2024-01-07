package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record BinomialDistributionGenerator(NumberProvider b, NumberProvider c) implements NumberProvider {
   private final NumberProvider n;
   private final NumberProvider p;
   public static final Codec<BinomialDistributionGenerator> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               NumberProviders.CODEC.fieldOf("n").forGetter(BinomialDistributionGenerator::n),
               NumberProviders.CODEC.fieldOf("p").forGetter(BinomialDistributionGenerator::p)
            )
            .apply(var0, BinomialDistributionGenerator::new)
   );

   public BinomialDistributionGenerator(NumberProvider var1, NumberProvider var2) {
      super();
      this.n = var1;
      this.p = var2;
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

      for(int var6 = 0; var6 < var2; ++var6) {
         if (var4.nextFloat() < var3) {
            ++var5;
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
