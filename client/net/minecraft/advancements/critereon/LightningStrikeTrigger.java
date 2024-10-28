package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public LightningStrikeTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return LightningStrikeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, LightningBolt var2, List<Entity> var3) {
      List var4 = (List)var3.stream().map((var1x) -> {
         return EntityPredicate.createContext(var1, var1x);
      }).collect(Collectors.toList());
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var5, var4);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> lightning, Optional<ContextAwarePredicate> bystander) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("lightning").forGetter(TriggerInstance::lightning), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("bystander").forGetter(TriggerInstance::bystander)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> lightning, Optional<ContextAwarePredicate> bystander) {
         super();
         this.player = player;
         this.lightning = lightning;
         this.bystander = bystander;
      }

      public static Criterion<TriggerInstance> lightningStrike(Optional<EntityPredicate> var0, Optional<EntityPredicate> var1) {
         return CriteriaTriggers.LIGHTNING_STRIKE.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), EntityPredicate.wrap(var1)));
      }

      public boolean matches(LootContext var1, List<LootContext> var2) {
         if (this.lightning.isPresent() && !((ContextAwarePredicate)this.lightning.get()).matches(var1)) {
            return false;
         } else {
            if (this.bystander.isPresent()) {
               Stream var10000 = var2.stream();
               ContextAwarePredicate var10001 = (ContextAwarePredicate)this.bystander.get();
               Objects.requireNonNull(var10001);
               if (var10000.noneMatch(var10001::matches)) {
                  return false;
               }
            }

            return true;
         }
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.lightning, ".lightning");
         var1.validateEntity(this.bystander, ".bystander");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<ContextAwarePredicate> lightning() {
         return this.lightning;
      }

      public Optional<ContextAwarePredicate> bystander() {
         return this.bystander;
      }
   }
}
