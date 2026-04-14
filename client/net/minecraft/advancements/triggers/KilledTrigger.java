package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class KilledTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public KilledTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return KilledTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Entity entity, final DamageSource killingBlow) {
      LootContext entityContext = EntityPredicate.createContext(player, entity);
      this.trigger(player, (t) -> t.matches(player, entityContext, killingBlow));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entity, Optional<DamageSourcePredicate> killingBlow) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity), DamageSourcePredicate.CODEC.optionalFieldOf("killing_blow").forGetter(TriggerInstance::killingBlow)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> playerKilledEntity(final Optional<EntityPredicate> entity) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(entity), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(final EntityPredicate.Builder entity) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity)), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerKilledEntity() {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(final Optional<EntityPredicate> entity, final Optional<DamageSourcePredicate> killingBlow) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(entity), killingBlow));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(final EntityPredicate.Builder entity, final Optional<DamageSourcePredicate> killingBlow) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity)), killingBlow));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(final Optional<EntityPredicate> entity, final DamageSourcePredicate.Builder killingBlow) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(entity), Optional.of(killingBlow.build())));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(final EntityPredicate.Builder entity, final DamageSourcePredicate.Builder killingBlow) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity)), Optional.of(killingBlow.build())));
      }

      public static Criterion<TriggerInstance> playerKilledEntityNearSculkCatalyst() {
         return CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(final Optional<EntityPredicate> entity) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(entity), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(final EntityPredicate.Builder entity) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity)), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer() {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(final Optional<EntityPredicate> entity, final Optional<DamageSourcePredicate> killingBlow) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(entity), killingBlow));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(final EntityPredicate.Builder entity, final Optional<DamageSourcePredicate> killingBlow) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity)), killingBlow));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(final Optional<EntityPredicate> entity, final DamageSourcePredicate.Builder killingBlow) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(entity), Optional.of(killingBlow.build())));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(final EntityPredicate.Builder entity, final DamageSourcePredicate.Builder killingBlow) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity)), Optional.of(killingBlow.build())));
      }

      public boolean matches(final ServerPlayer player, final LootContext entity, final DamageSource killingBlow) {
         if (this.killingBlow.isPresent() && !((DamageSourcePredicate)this.killingBlow.get()).matches(player, killingBlow)) {
            return false;
         } else {
            return this.entity.isEmpty() || ((ContextAwarePredicate)this.entity.get()).matches(entity);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "entity", this.entity);
      }
   }
}
