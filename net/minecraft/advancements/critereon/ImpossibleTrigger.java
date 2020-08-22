package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public class ImpossibleTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("impossible");

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener var2) {
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener var2) {
   }

   public void removePlayerListeners(PlayerAdvancements var1) {
   }

   public ImpossibleTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new ImpossibleTrigger.TriggerInstance();
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance() {
         super(ImpossibleTrigger.ID);
      }
   }
}
