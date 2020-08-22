package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TickTrigger extends SimpleCriterionTrigger {
   public static final ResourceLocation ID = new ResourceLocation("tick");

   public ResourceLocation getId() {
      return ID;
   }

   public TickTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new TickTrigger.TriggerInstance();
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1.getAdvancements());
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance() {
         super(TickTrigger.ID);
      }
   }
}
