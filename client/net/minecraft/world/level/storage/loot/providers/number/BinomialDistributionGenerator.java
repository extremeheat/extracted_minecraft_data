package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public final class BinomialDistributionGenerator implements NumberProvider {
   final NumberProvider n;
   final NumberProvider p;

   BinomialDistributionGenerator(NumberProvider var1, NumberProvider var2) {
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

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BinomialDistributionGenerator> {
      public Serializer() {
         super();
      }

      public BinomialDistributionGenerator deserialize(JsonObject var1, JsonDeserializationContext var2) {
         NumberProvider var3 = GsonHelper.getAsObject(var1, "n", var2, NumberProvider.class);
         NumberProvider var4 = GsonHelper.getAsObject(var1, "p", var2, NumberProvider.class);
         return new BinomialDistributionGenerator(var3, var4);
      }

      public void serialize(JsonObject var1, BinomialDistributionGenerator var2, JsonSerializationContext var3) {
         var1.add("n", var3.serialize(var2.n));
         var1.add("p", var3.serialize(var2.p));
      }
   }
}
