package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger<CuredZombieVillagerTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

   public CuredZombieVillagerTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public CuredZombieVillagerTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ContextAwarePredicate var4 = EntityPredicate.fromJson(var1, "zombie", var3);
      ContextAwarePredicate var5 = EntityPredicate.fromJson(var1, "villager", var3);
      return new CuredZombieVillagerTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      LootContext var5 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, var2x -> var2x.matches(var4, var5));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate zombie;
      private final ContextAwarePredicate villager;

      public TriggerInstance(ContextAwarePredicate var1, ContextAwarePredicate var2, ContextAwarePredicate var3) {
         super(CuredZombieVillagerTrigger.ID, var1);
         this.zombie = var2;
         this.villager = var3;
      }

      public static CuredZombieVillagerTrigger.TriggerInstance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
      }

      public boolean matches(LootContext var1, LootContext var2) {
         if (!this.zombie.matches(var1)) {
            return false;
         } else {
            return this.villager.matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("zombie", this.zombie.toJson(var1));
         var2.add("villager", this.villager.toJson(var1));
         return var2;
      }
   }
}
