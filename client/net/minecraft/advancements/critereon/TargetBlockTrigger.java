package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger extends SimpleCriterionTrigger<TargetBlockTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_87 = new ResourceLocation("target_hit");

   public TargetBlockTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_87;
   }

   public TargetBlockTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("signal_strength"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "projectile", var3);
      return new TargetBlockTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Entity var2, Vec3 var3, int var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var5, var3, var4);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints signalStrength;
      private final EntityPredicate.Composite projectile;

      public TriggerInstance(EntityPredicate.Composite var1, MinMaxBounds.Ints var2, EntityPredicate.Composite var3) {
         super(TargetBlockTrigger.field_87, var1);
         this.signalStrength = var2;
         this.projectile = var3;
      }

      public static TargetBlockTrigger.TriggerInstance targetHit(MinMaxBounds.Ints var0, EntityPredicate.Composite var1) {
         return new TargetBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, var1);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("signal_strength", this.signalStrength.serializeToJson());
         var2.add("projectile", this.projectile.toJson(var1));
         return var2;
      }

      public boolean matches(LootContext var1, Vec3 var2, int var3) {
         if (!this.signalStrength.matches(var3)) {
            return false;
         } else {
            return this.projectile.matches(var1);
         }
      }
   }
}
