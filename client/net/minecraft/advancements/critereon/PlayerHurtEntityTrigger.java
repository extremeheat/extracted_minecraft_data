package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerHurtEntityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

   public PlayerHurtEntityTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      DamagePredicate var4 = DamagePredicate.fromJson(var1.get("damage"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "entity", var3);
      return new TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
      LootContext var7 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, (var6x) -> {
         return var6x.matches(var1, var7, var3, var4, var5, var6);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite var1, DamagePredicate var2, EntityPredicate.Composite var3) {
         super(PlayerHurtEntityTrigger.ID, var1);
         this.damage = var2;
         this.entity = var3;
      }

      public static TriggerInstance playerHurtEntity() {
         return new TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance playerHurtEntity(DamagePredicate var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance playerHurtEntity(DamagePredicate.Builder var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0.build(), EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance playerHurtEntity(EntityPredicate var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY, EntityPredicate.Composite.wrap(var0));
      }

      public static TriggerInstance playerHurtEntity(DamagePredicate var0, EntityPredicate var1) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0, EntityPredicate.Composite.wrap(var1));
      }

      public static TriggerInstance playerHurtEntity(DamagePredicate.Builder var0, EntityPredicate var1) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0.build(), EntityPredicate.Composite.wrap(var1));
      }

      public boolean matches(ServerPlayer var1, LootContext var2, DamageSource var3, float var4, float var5, boolean var6) {
         if (!this.damage.matches(var1, var3, var4, var5, var6)) {
            return false;
         } else {
            return this.entity.matches(var2);
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("damage", this.damage.serializeToJson());
         var2.add("entity", this.entity.toJson(var1));
         return var2;
      }
   }
}
