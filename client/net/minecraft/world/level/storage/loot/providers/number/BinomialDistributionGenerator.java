package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public final class BinomialDistributionGenerator implements NumberProvider {
   // $FF: renamed from: n net.minecraft.world.level.storage.loot.providers.number.NumberProvider
   final NumberProvider field_410;
   // $FF: renamed from: p net.minecraft.world.level.storage.loot.providers.number.NumberProvider
   final NumberProvider field_411;

   BinomialDistributionGenerator(NumberProvider var1, NumberProvider var2) {
      super();
      this.field_410 = var1;
      this.field_411 = var2;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.BINOMIAL;
   }

   public int getInt(LootContext var1) {
      int var2 = this.field_410.getInt(var1);
      float var3 = this.field_411.getFloat(var1);
      Random var4 = var1.getRandom();
      int var5 = 0;

      for(int var6 = 0; var6 < var2; ++var6) {
         if (var4.nextFloat() < var3) {
            ++var5;
         }
      }

      return var5;
   }

   public float getFloat(LootContext var1) {
      return (float)this.getInt(var1);
   }

   public static BinomialDistributionGenerator binomial(int var0, float var1) {
      return new BinomialDistributionGenerator(ConstantValue.exactly((float)var0), ConstantValue.exactly(var1));
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.field_410.getReferencedContextParams(), this.field_411.getReferencedContextParams());
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BinomialDistributionGenerator> {
      public Serializer() {
         super();
      }

      public BinomialDistributionGenerator deserialize(JsonObject var1, JsonDeserializationContext var2) {
         NumberProvider var3 = (NumberProvider)GsonHelper.getAsObject(var1, "n", var2, NumberProvider.class);
         NumberProvider var4 = (NumberProvider)GsonHelper.getAsObject(var1, "p", var2, NumberProvider.class);
         return new BinomialDistributionGenerator(var3, var4);
      }

      public void serialize(JsonObject var1, BinomialDistributionGenerator var2, JsonSerializationContext var3) {
         var1.add("n", var3.serialize(var2.field_410));
         var1.add("p", var3.serialize(var2.field_411));
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
