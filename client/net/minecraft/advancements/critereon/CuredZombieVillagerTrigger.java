package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger<CuredZombieVillagerTrigger.TriggerInstance> {
   public CuredZombieVillagerTrigger() {
      super();
   }

   public CuredZombieVillagerTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = EntityPredicate.fromJson(var1, "zombie", var3);
      Optional var5 = EntityPredicate.fromJson(var1, "villager", var3);
      return new CuredZombieVillagerTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      LootContext var5 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, var2x -> var2x.matches(var4, var5));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ContextAwarePredicate> zombie;
      private final Optional<ContextAwarePredicate> villager;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ContextAwarePredicate> var3) {
         super(var1);
         this.zombie = var2;
         this.villager = var3;
      }

      public static Criterion<CuredZombieVillagerTrigger.TriggerInstance> curedZombieVillager() {
         return CriteriaTriggers.CURED_ZOMBIE_VILLAGER
            .createCriterion(new CuredZombieVillagerTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public boolean matches(LootContext var1, LootContext var2) {
         if (this.zombie.isPresent() && !this.zombie.get().matches(var1)) {
            return false;
         } else {
            return !this.villager.isPresent() || this.villager.get().matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.zombie.ifPresent(var1x -> var1.add("zombie", var1x.toJson()));
         this.villager.ifPresent(var1x -> var1.add("villager", var1x.toJson()));
         return var1;
      }
   }
}
