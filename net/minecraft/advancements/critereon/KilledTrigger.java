package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class KilledTrigger extends SimpleCriterionTrigger {
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation var1) {
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public KilledTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new KilledTrigger.TriggerInstance(this.id, EntityPredicate.fromJson(var1.get("entity")), DamageSourcePredicate.fromJson(var1.get("killing_blow")));
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3) {
      this.trigger(var1.getAdvancements(), (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public TriggerInstance(ResourceLocation var1, EntityPredicate var2, DamageSourcePredicate var3) {
         super(var1);
         this.entityPredicate = var2;
         this.killingBlow = var3;
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, var0.build(), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, var0.build(), var1.build());
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Entity var2, DamageSource var3) {
         return !this.killingBlow.matches(var1, var3) ? false : this.entityPredicate.matches(var1, var2);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entityPredicate.serializeToJson());
         var1.add("killing_blow", this.killingBlow.serializeToJson());
         return var1;
      }
   }
}
