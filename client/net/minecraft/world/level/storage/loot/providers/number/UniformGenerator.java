package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class UniformGenerator implements NumberProvider {
   final NumberProvider min;
   final NumberProvider max;

   UniformGenerator(NumberProvider var1, NumberProvider var2) {
      super();
      this.min = var1;
      this.max = var2;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.UNIFORM;
   }

   public static UniformGenerator between(float var0, float var1) {
      return new UniformGenerator(ConstantValue.exactly(var0), ConstantValue.exactly(var1));
   }

   @Override
   public int getInt(LootContext var1) {
      return Mth.nextInt(var1.getRandom(), this.min.getInt(var1), this.max.getInt(var1));
   }

   @Override
   public float getFloat(LootContext var1) {
      return Mth.nextFloat(var1.getRandom(), this.min.getFloat(var1), this.max.getFloat(var1));
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.min.getReferencedContextParams(), this.max.getReferencedContextParams());
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<UniformGenerator> {
      public Serializer() {
         super();
      }

      public UniformGenerator deserialize(JsonObject var1, JsonDeserializationContext var2) {
         NumberProvider var3 = GsonHelper.getAsObject(var1, "min", var2, NumberProvider.class);
         NumberProvider var4 = GsonHelper.getAsObject(var1, "max", var2, NumberProvider.class);
         return new UniformGenerator(var3, var4);
      }

      public void serialize(JsonObject var1, UniformGenerator var2, JsonSerializationContext var3) {
         var1.add("min", var3.serialize(var2.min));
         var1.add("max", var3.serialize(var2.max));
      }
   }
}
