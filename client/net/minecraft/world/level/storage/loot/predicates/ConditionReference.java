package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public class ConditionReference implements LootItemCondition {
   private static final Logger LOGGER = LogUtils.getLogger();
   final ResourceLocation name;

   ConditionReference(ResourceLocation var1) {
      super();
      this.name = var1;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.REFERENCE;
   }

   @Override
   public void validate(ValidationContext var1) {
      if (var1.hasVisitedCondition(this.name)) {
         var1.reportProblem("Condition " + this.name + " is recursively called");
      } else {
         LootItemCondition.super.validate(var1);
         LootItemCondition var2 = var1.resolveCondition(this.name);
         if (var2 == null) {
            var1.reportProblem("Unknown condition table called " + this.name);
         } else {
            var2.validate(var1.enterTable(".{" + this.name + "}", this.name));
         }
      }
   }

   public boolean test(LootContext var1) {
      LootItemCondition var2 = var1.getCondition(this.name);
      if (var2 == null) {
         LOGGER.warn("Tried using unknown condition table called {}", this.name);
         return false;
      } else if (var1.addVisitedCondition(var2)) {
         boolean var3;
         try {
            var3 = var2.test(var1);
         } finally {
            var1.removeVisitedCondition(var2);
         }

         return var3;
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
         return false;
      }
   }

   public static LootItemCondition.Builder conditionReference(ResourceLocation var0) {
      return () -> new ConditionReference(var0);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ConditionReference> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ConditionReference var2, JsonSerializationContext var3) {
         var1.addProperty("name", var2.name.toString());
      }

      public ConditionReference deserialize(JsonObject var1, JsonDeserializationContext var2) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         return new ConditionReference(var3);
      }
   }
}
