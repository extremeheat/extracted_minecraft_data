package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class PlayerHurtEntityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public PlayerHurtEntityTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return PlayerHurtEntityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Entity victim, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
      LootContext victimContext = EntityPredicate.createContext(player, victim);
      this.trigger(player, (t) -> t.matches(player, victimContext, source, originalDamage, actualDamage, blocked));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DamagePredicate> damage, Optional<ContextAwarePredicate> entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(TriggerInstance::damage), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> playerHurtEntity() {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerHurtEntityWithDamage(final Optional<DamagePredicate> damage) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), damage, Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerHurtEntityWithDamage(final DamagePredicate.Builder damage) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(damage.build()), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerHurtEntity(final Optional<EntityPredicate> entity) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), EntityPredicate.wrap(entity)));
      }

      public static Criterion<TriggerInstance> playerHurtEntity(final Optional<DamagePredicate> damage, final Optional<EntityPredicate> entity) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), damage, EntityPredicate.wrap(entity)));
      }

      public static Criterion<TriggerInstance> playerHurtEntity(final DamagePredicate.Builder damage, final Optional<EntityPredicate> entity) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(damage.build()), EntityPredicate.wrap(entity)));
      }

      public boolean matches(final ServerPlayer player, final LootContext victim, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
         if (this.damage.isPresent() && !((DamagePredicate)this.damage.get()).matches(player, source, originalDamage, actualDamage, blocked)) {
            return false;
         } else {
            return !this.entity.isPresent() || ((ContextAwarePredicate)this.entity.get()).matches(victim);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "entity", this.entity);
      }
   }
}
