package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class NetherTravelTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");

   public ResourceLocation getId() {
      return ID;
   }

   public NetherTravelTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      LocationPredicate var3 = LocationPredicate.fromJson(var1.get("entered"));
      LocationPredicate var4 = LocationPredicate.fromJson(var1.get("exited"));
      DistancePredicate var5 = DistancePredicate.fromJson(var1.get("distance"));
      return new NetherTravelTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, Vec3 var2) {
      this.trigger(var1.getAdvancements(), (var2x) -> {
         return var2x.matches(var1.getLevel(), var2, var1.getX(), var1.getY(), var1.getZ());
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public TriggerInstance(LocationPredicate var1, LocationPredicate var2, DistancePredicate var3) {
         super(NetherTravelTrigger.ID);
         this.entered = var1;
         this.exited = var2;
         this.distance = var3;
      }

      public static NetherTravelTrigger.TriggerInstance travelledThroughNether(DistancePredicate var0) {
         return new NetherTravelTrigger.TriggerInstance(LocationPredicate.ANY, LocationPredicate.ANY, var0);
      }

      public boolean matches(ServerLevel var1, Vec3 var2, double var3, double var5, double var7) {
         if (!this.entered.matches(var1, var2.x, var2.y, var2.z)) {
            return false;
         } else if (!this.exited.matches(var1, var3, var5, var7)) {
            return false;
         } else {
            return this.distance.matches(var2.x, var2.y, var2.z, var3, var5, var7);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entered", this.entered.serializeToJson());
         var1.add("exited", this.exited.serializeToJson());
         var1.add("distance", this.distance.serializeToJson());
         return var1;
      }
   }
}
