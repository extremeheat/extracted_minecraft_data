package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootItemRandomChanceCondition implements LootItemCondition {
   final float probability;

   LootItemRandomChanceCondition(float var1) {
      super();
      this.probability = var1;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE;
   }

   public boolean test(LootContext var1) {
      return var1.getRandom().nextFloat() < this.probability;
   }

   public static LootItemCondition.Builder randomChance(float var0) {
      return () -> new LootItemRandomChanceCondition(var0);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LootItemRandomChanceCondition var2, JsonSerializationContext var3) {
         var1.addProperty("chance", var2.probability);
      }

      public LootItemRandomChanceCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return new LootItemRandomChanceCondition(GsonHelper.getAsFloat(var1, "chance"));
      }
   }
}
