package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("levitation");

   public LevitationTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      DistancePredicate var4 = DistancePredicate.fromJson(var1.get("distance"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("duration"));
      return new TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(EntityPredicate.Composite var1, DistancePredicate var2, MinMaxBounds.Ints var3) {
         super(LevitationTrigger.ID, var1);
         this.distance = var2;
         this.duration = var3;
      }

      public static TriggerInstance levitated(DistancePredicate var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         if (!this.distance.matches(var2.x, var2.y, var2.z, var1.getX(), var1.getY(), var1.getZ())) {
            return false;
         } else {
            return this.duration.matches(var3);
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("distance", this.distance.serializeToJson());
         var2.add("duration", this.duration.serializeToJson());
         return var2;
      }
   }
}
