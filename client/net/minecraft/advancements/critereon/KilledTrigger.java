package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledTrigger extends SimpleCriterionTrigger<KilledTrigger.TriggerInstance> {
   final ResourceLocation id;

   public KilledTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   public KilledTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      return new KilledTrigger.TriggerInstance(
         this.id, var2, EntityPredicate.fromJson(var1, "entity", var3), DamageSourcePredicate.fromJson(var1.get("killing_blow"))
      );
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var3x -> var3x.matches(var1, var4, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public TriggerInstance(ResourceLocation var1, ContextAwarePredicate var2, ContextAwarePredicate var3, DamageSourcePredicate var4) {
         super(var1, var2);
         this.entityPredicate = var3;
         this.killingBlow = var4;
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate var0) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build()), DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity() {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate var0, DamageSourcePredicate var1) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), var1);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0, DamageSourcePredicate var1) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build()), var1
         );
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), var1.build()
         );
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build()), var1.build()
         );
      }

      public static KilledTrigger.TriggerInstance playerKilledEntityNearSculkCatalyst() {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate var0) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder var0) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build()), DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer() {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, DamageSourcePredicate.ANY
         );
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate var0, DamageSourcePredicate var1) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), var1);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder var0, DamageSourcePredicate var1) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build()), var1
         );
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), var1.build()
         );
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.TriggerInstance(
            CriteriaTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build()), var1.build()
         );
      }

      public boolean matches(ServerPlayer var1, LootContext var2, DamageSource var3) {
         return !this.killingBlow.matches(var1, var3) ? false : this.entityPredicate.matches(var2);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("entity", this.entityPredicate.toJson(var1));
         var2.add("killing_blow", this.killingBlow.serializeToJson());
         return var2;
      }
   }
}
