package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TickTrigger extends SimpleCriterionTrigger<TickTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   public static final ResourceLocation field_97 = new ResourceLocation("tick");

   public TickTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_97;
   }

   public TickTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return new TickTrigger.TriggerInstance(var2);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, (var0) -> {
         return true;
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance(EntityPredicate.Composite var1) {
         super(TickTrigger.field_97, var1);
      }
   }
}
