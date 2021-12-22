package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class StartRidingTrigger extends SimpleCriterionTrigger<StartRidingTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_69 = new ResourceLocation("started_riding");

   public StartRidingTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_69;
   }

   public StartRidingTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return new StartRidingTrigger.TriggerInstance(var2);
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
         super(StartRidingTrigger.field_69, var1);
      }

      public static StartRidingTrigger.TriggerInstance playerStartsRiding(EntityPredicate.Builder var0) {
         return new StartRidingTrigger.TriggerInstance(EntityPredicate.Composite.wrap(var0.build()));
      }
   }
}
