package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;

public class TameAnimalTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("entity"));
      return new TameAnimalTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Animal var2) {
      this.trigger(var1.getAdvancements(), (var2x) -> {
         return var2x.matches(var1, var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate entity;

      public TriggerInstance(EntityPredicate var1) {
         super(TameAnimalTrigger.ID);
         this.entity = var1;
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal() {
         return new TameAnimalTrigger.TriggerInstance(EntityPredicate.ANY);
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal(EntityPredicate var0) {
         return new TameAnimalTrigger.TriggerInstance(var0);
      }

      public boolean matches(ServerPlayer var1, Animal var2) {
         return this.entity.matches(var1, var2);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
