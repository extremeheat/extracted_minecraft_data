package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledTrigger extends SimpleCriterionTrigger<KilledTrigger.TriggerInstance> {
   public KilledTrigger() {
      super();
   }

   @Override
   public Codec<KilledTrigger.TriggerInstance> codec() {
      return KilledTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var3x -> var3x.matches(var1, var4, var3));
   }

   public static record TriggerInstance(
      Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate, Optional<DamageSourcePredicate> killingBlow
   ) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<KilledTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(KilledTrigger.TriggerInstance::player),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(KilledTrigger.TriggerInstance::entityPredicate),
                  DamageSourcePredicate.CODEC.optionalFieldOf("killing_blow").forGetter(KilledTrigger.TriggerInstance::killingBlow)
               )
               .apply(var0, KilledTrigger.TriggerInstance::new)
      );

      public TriggerInstance(
         Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate, Optional<DamageSourcePredicate> killingBlow
      ) {
         super();
         this.player = player;
         this.entityPredicate = entityPredicate;
         this.killingBlow = killingBlow;
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity(EntityPredicate.Builder var0) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity() {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity(Optional<EntityPredicate> var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), var1));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity(EntityPredicate.Builder var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), var1));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity(Optional<EntityPredicate> var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.of(var1.build())));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntity(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.of(var1.build())));
      }

      public static Criterion<KilledTrigger.TriggerInstance> playerKilledEntityNearSculkCatalyst() {
         return CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer(EntityPredicate.Builder var0) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer() {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer(Optional<EntityPredicate> var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), var1));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer(EntityPredicate.Builder var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), var1));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer(Optional<EntityPredicate> var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.of(var1.build())));
      }

      public static Criterion<KilledTrigger.TriggerInstance> entityKilledPlayer(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER
            .createCriterion(new KilledTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.of(var1.build())));
      }

      public boolean matches(ServerPlayer var1, LootContext var2, DamageSource var3) {
         return this.killingBlow.isPresent() && !this.killingBlow.get().matches(var1, var3)
            ? false
            : this.entityPredicate.isEmpty() || this.entityPredicate.get().matches(var2);
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.entityPredicate, ".entity");
      }
   }
}
