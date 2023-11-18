package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class TameAnimalTrigger extends SimpleCriterionTrigger<TameAnimalTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public TameAnimalTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ContextAwarePredicate var4 = EntityPredicate.fromJson(var1, "entity", var3);
      return new TameAnimalTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Animal var2) {
      LootContext var3 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var1x -> var1x.matches(var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate entity;

      public TriggerInstance(ContextAwarePredicate var1, ContextAwarePredicate var2) {
         super(TameAnimalTrigger.ID, var1);
         this.entity = var2;
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal() {
         return new TameAnimalTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal(EntityPredicate var0) {
         return new TameAnimalTrigger.TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(var0));
      }

      public boolean matches(LootContext var1) {
         return this.entity.matches(var1);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("entity", this.entity.toJson(var1));
         return var2;
      }
   }
}
