package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   public LevitationTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DistancePredicate var3 = DistancePredicate.fromJson(var1.get("distance"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("duration"));
      return new LevitationTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
      this.trigger(var1.getAdvancements(), (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(DistancePredicate var1, MinMaxBounds.Ints var2) {
         super(LevitationTrigger.ID);
         this.distance = var1;
         this.duration = var2;
      }

      public static LevitationTrigger.TriggerInstance levitated(DistancePredicate var0) {
         return new LevitationTrigger.TriggerInstance(var0, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         if (!this.distance.matches(var2.x, var2.y, var2.z, var1.getX(), var1.getY(), var1.getZ())) {
            return false;
         } else {
            return this.duration.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("distance", this.distance.serializeToJson());
         var1.add("duration", this.duration.serializeToJson());
         return var1;
      }
   }
}
