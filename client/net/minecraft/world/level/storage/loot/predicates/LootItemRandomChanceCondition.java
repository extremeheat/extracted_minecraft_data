package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootItemRandomChanceCondition implements LootItemCondition {
   private final float probability;

   private LootItemRandomChanceCondition(float var1) {
      super();
      this.probability = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE;
   }

   public boolean test(LootContext var1) {
      return var1.getRandom().nextFloat() < this.probability;
   }

   public static LootItemCondition.Builder randomChance(float var0) {
      return () -> {
         return new LootItemRandomChanceCondition(var0);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   LootItemRandomChanceCondition(float var1, Object var2) {
      this(var1);
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

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
