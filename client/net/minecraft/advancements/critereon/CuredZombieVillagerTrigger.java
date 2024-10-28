package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public CuredZombieVillagerTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return CuredZombieVillagerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      LootContext var5 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var4, var5);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> zombie, Optional<ContextAwarePredicate> villager) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("zombie").forGetter(TriggerInstance::zombie), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("villager").forGetter(TriggerInstance::villager)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> zombie, Optional<ContextAwarePredicate> villager) {
         super();
         this.player = player;
         this.zombie = zombie;
         this.villager = villager;
      }

      public static Criterion<TriggerInstance> curedZombieVillager() {
         return CriteriaTriggers.CURED_ZOMBIE_VILLAGER.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public boolean matches(LootContext var1, LootContext var2) {
         if (this.zombie.isPresent() && !((ContextAwarePredicate)this.zombie.get()).matches(var1)) {
            return false;
         } else {
            return !this.villager.isPresent() || ((ContextAwarePredicate)this.villager.get()).matches(var2);
         }
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.zombie, ".zombie");
         var1.validateEntity(this.villager, ".villager");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<ContextAwarePredicate> zombie() {
         return this.zombie;
      }

      public Optional<ContextAwarePredicate> villager() {
         return this.villager;
      }
   }
}
