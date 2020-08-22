package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConditionReference implements LootItemCondition {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation name;

   public ConditionReference(ResourceLocation var1) {
      this.name = var1;
   }

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
      if (var1.addVisitedCondition(var2)) {
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

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("reference"), ConditionReference.class);
      }

      public void serialize(JsonObject var1, ConditionReference var2, JsonSerializationContext var3) {
         var1.addProperty("name", var2.name.toString());
      }

      public ConditionReference deserialize(JsonObject var1, JsonDeserializationContext var2) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         return new ConditionReference(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
