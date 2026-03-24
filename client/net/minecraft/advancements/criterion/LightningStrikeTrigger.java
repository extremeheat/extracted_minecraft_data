package net.minecraft.advancements.criterion;

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
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public LightningStrikeTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return LightningStrikeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final LightningBolt lightning, final List<Entity> entitiesAround) {
      List<LootContext> entitiesAroundContexts = (List)entitiesAround.stream().map((v) -> EntityPredicate.createContext(player, v)).collect(Collectors.toList());
      LootContext lightningContext = EntityPredicate.createContext(player, lightning);
      this.trigger(player, (t) -> t.matches(lightningContext, entitiesAroundContexts));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> lightning, Optional<ContextAwarePredicate> bystander) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("lightning").forGetter(TriggerInstance::lightning), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("bystander").forGetter(TriggerInstance::bystander)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> lightningStrike(final Optional<EntityPredicate> lightning, final Optional<EntityPredicate> bystander) {
         return CriteriaTriggers.LIGHTNING_STRIKE.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(lightning), EntityPredicate.wrap(bystander)));
      }

      public boolean matches(final LootContext bolt, final List<LootContext> entitiesAround) {
         if (this.lightning.isPresent() && !((ContextAwarePredicate)this.lightning.get()).matches(bolt)) {
            return false;
         } else {
            if (this.bystander.isPresent()) {
               Stream var10000 = entitiesAround.stream();
               ContextAwarePredicate var10001 = (ContextAwarePredicate)this.bystander.get();
               Objects.requireNonNull(var10001);
               if (var10000.noneMatch(var10001::matches)) {
                  return false;
               }
            }

            return true;
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "lightning", this.lightning);
         Validatable.validate(validator.entityContext(), "bystander", this.bystander);
      }
   }
}
