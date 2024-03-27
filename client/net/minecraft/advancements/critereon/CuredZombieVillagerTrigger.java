package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
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

   @Override
   public Codec<CuredZombieVillagerTrigger.TriggerInstance> codec() {
      return CuredZombieVillagerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      LootContext var5 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, var2x -> var2x.matches(var4, var5));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ContextAwarePredicate> c, Optional<ContextAwarePredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ContextAwarePredicate> zombie;
      private final Optional<ContextAwarePredicate> villager;
      public static final Codec<CuredZombieVillagerTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(CuredZombieVillagerTrigger.TriggerInstance::player),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("zombie").forGetter(CuredZombieVillagerTrigger.TriggerInstance::zombie),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("villager").forGetter(CuredZombieVillagerTrigger.TriggerInstance::villager)
               )
               .apply(var0, CuredZombieVillagerTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ContextAwarePredicate> var3) {
         super();
         this.player = var1;
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
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.zombie, ".zombie");
         var1.validateEntity(this.villager, ".villager");
      }
   }
}
