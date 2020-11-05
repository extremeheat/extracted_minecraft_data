package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class NetherTravelTrigger extends SimpleCriterionTrigger<NetherTravelTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");

   public NetherTravelTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public NetherTravelTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      LocationPredicate var4 = LocationPredicate.fromJson(var1.get("entered"));
      LocationPredicate var5 = LocationPredicate.fromJson(var1.get("exited"));
      DistancePredicate var6 = DistancePredicate.fromJson(var1.get("distance"));
      return new NetherTravelTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, Vec3 var2) {
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var1.getLevel(), var2, var1.getX(), var1.getY(), var1.getZ());
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public TriggerInstance(EntityPredicate.Composite var1, LocationPredicate var2, LocationPredicate var3, DistancePredicate var4) {
         super(NetherTravelTrigger.ID, var1);
         this.entered = var2;
         this.exited = var3;
         this.distance = var4;
      }

      public static NetherTravelTrigger.TriggerInstance travelledThroughNether(DistancePredicate var0) {
         return new NetherTravelTrigger.TriggerInstance(EntityPredicate.Composite.ANY, LocationPredicate.ANY, LocationPredicate.ANY, var0);
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

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("entered", this.entered.serializeToJson());
         var2.add("exited", this.exited.serializeToJson());
         var2.add("distance", this.distance.serializeToJson());
         return var2;
      }
   }
}
