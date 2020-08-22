package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class PlayerHurtEntityTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public PlayerHurtEntityTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DamagePredicate var3 = DamagePredicate.fromJson(var1.get("damage"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("entity"));
      return new PlayerHurtEntityTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
      this.trigger(var1.getAdvancements(), (var6x) -> {
         return var6x.matches(var1, var2, var3, var4, var5, var6);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;
      private final EntityPredicate entity;

      public TriggerInstance(DamagePredicate var1, EntityPredicate var2) {
         super(PlayerHurtEntityTrigger.ID);
         this.damage = var1;
         this.entity = var2;
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate.Builder var0) {
         return new PlayerHurtEntityTrigger.TriggerInstance(var0.build(), EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
         if (!this.damage.matches(var1, var3, var4, var5, var6)) {
            return false;
         } else {
            return this.entity.matches(var1, var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.damage.serializeToJson());
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
