package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

   public ResourceLocation getId() {
      return ID;
   }

   public EntityHurtPlayerTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DamagePredicate var3 = DamagePredicate.fromJson(var1.get("damage"));
      return new EntityHurtPlayerTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      this.trigger(var1.getAdvancements(), (var5x) -> {
         return var5x.matches(var1, var2, var3, var4, var5);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;

      public TriggerInstance(DamagePredicate var1) {
         super(EntityHurtPlayerTrigger.ID);
         this.damage = var1;
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate.Builder var0) {
         return new EntityHurtPlayerTrigger.TriggerInstance(var0.build());
      }

      public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
         return this.damage.matches(var1, var2, var3, var4, var5);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.damage.serializeToJson());
         return var1;
      }
   }
}
