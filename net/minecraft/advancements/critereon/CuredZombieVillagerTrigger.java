package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

   public ResourceLocation getId() {
      return ID;
   }

   public CuredZombieVillagerTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("zombie"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("villager"));
      return new CuredZombieVillagerTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
      this.trigger(var1.getAdvancements(), (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate zombie;
      private final EntityPredicate villager;

      public TriggerInstance(EntityPredicate var1, EntityPredicate var2) {
         super(CuredZombieVillagerTrigger.ID);
         this.zombie = var1;
         this.villager = var2;
      }

      public static CuredZombieVillagerTrigger.TriggerInstance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Zombie var2, Villager var3) {
         if (!this.zombie.matches(var1, var2)) {
            return false;
         } else {
            return this.villager.matches(var1, var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("zombie", this.zombie.serializeToJson());
         var1.add("villager", this.villager.serializeToJson());
         return var1;
      }
   }
}
