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

public class KilledTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public KilledTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return KilledTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var1, var4, var3);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate, Optional<DamageSourcePredicate> killingBlow) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entityPredicate), DamageSourcePredicate.CODEC.optionalFieldOf("killing_blow").forGetter(TriggerInstance::killingBlow)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate, Optional<DamageSourcePredicate> killingBlow) {
         super();
         this.player = player;
         this.entityPredicate = entityPredicate;
         this.killingBlow = killingBlow;
      }

      public static Criterion<TriggerInstance> playerKilledEntity(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(EntityPredicate.Builder var0) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerKilledEntity() {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(Optional<EntityPredicate> var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), var1));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(EntityPredicate.Builder var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), var1));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(Optional<EntityPredicate> var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.of(var1.build())));
      }

      public static Criterion<TriggerInstance> playerKilledEntity(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.PLAYER_KILLED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.of(var1.build())));
      }

      public static Criterion<TriggerInstance> playerKilledEntityNearSculkCatalyst() {
         return CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(EntityPredicate.Builder var0) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer() {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(Optional<EntityPredicate> var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), var1));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(EntityPredicate.Builder var0, Optional<DamageSourcePredicate> var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), var1));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(Optional<EntityPredicate> var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), Optional.of(var1.build())));
      }

      public static Criterion<TriggerInstance> entityKilledPlayer(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return CriteriaTriggers.ENTITY_KILLED_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0)), Optional.of(var1.build())));
      }

      public boolean matches(ServerPlayer var1, LootContext var2, DamageSource var3) {
         if (this.killingBlow.isPresent() && !((DamageSourcePredicate)this.killingBlow.get()).matches(var1, var3)) {
            return false;
         } else {
            return this.entityPredicate.isEmpty() || ((ContextAwarePredicate)this.entityPredicate.get()).matches(var2);
         }
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.entityPredicate, ".entity");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<ContextAwarePredicate> entityPredicate() {
         return this.entityPredicate;
      }

      public Optional<DamageSourcePredicate> killingBlow() {
         return this.killingBlow;
      }
   }
}
