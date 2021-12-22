package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger<EntityHurtPlayerTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_95 = new ResourceLocation("entity_hurt_player");

   public EntityHurtPlayerTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_95;
   }

   public EntityHurtPlayerTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      DamagePredicate var4 = DamagePredicate.fromJson(var1.get("damage"));
      return new EntityHurtPlayerTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      this.trigger(var1, (var5x) -> {
         return var5x.matches(var1, var2, var3, var4, var5);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;

      public TriggerInstance(EntityPredicate.Composite var1, DamagePredicate var2) {
         super(EntityHurtPlayerTrigger.field_95, var1);
         this.damage = var2;
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer() {
         return new EntityHurtPlayerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY);
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate var0) {
         return new EntityHurtPlayerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0);
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate.Builder var0) {
         return new EntityHurtPlayerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0.build());
      }

      public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
         return this.damage.matches(var1, var2, var3, var4, var5);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("damage", this.damage.serializeToJson());
         return var2;
      }
   }
}
