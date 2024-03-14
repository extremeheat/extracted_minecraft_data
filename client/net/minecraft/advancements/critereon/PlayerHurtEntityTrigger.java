package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerHurtEntityTrigger extends SimpleCriterionTrigger<PlayerHurtEntityTrigger.TriggerInstance> {
   public PlayerHurtEntityTrigger() {
      super();
   }

   @Override
   public Codec<PlayerHurtEntityTrigger.TriggerInstance> codec() {
      return PlayerHurtEntityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
      LootContext var7 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var6x -> var6x.matches(var1, var7, var3, var4, var5, var6));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<DamagePredicate> c, Optional<ContextAwarePredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<DamagePredicate> damage;
      private final Optional<ContextAwarePredicate> entity;
      public static final Codec<PlayerHurtEntityTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(PlayerHurtEntityTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(DamagePredicate.CODEC, "damage").forGetter(PlayerHurtEntityTrigger.TriggerInstance::damage),
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "entity").forGetter(PlayerHurtEntityTrigger.TriggerInstance::entity)
               )
               .apply(var0, PlayerHurtEntityTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<DamagePredicate> var2, Optional<ContextAwarePredicate> var3) {
         super();
         this.player = var1;
         this.damage = var2;
         this.entity = var3;
      }

      public static Criterion<PlayerHurtEntityTrigger.TriggerInstance> playerHurtEntity() {
         return CriteriaTriggers.PLAYER_HURT_ENTITY
            .createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<PlayerHurtEntityTrigger.TriggerInstance> playerHurtEntityWithDamage(Optional<DamagePredicate> var0) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY.createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), var0, Optional.empty()));
      }

      public static Criterion<PlayerHurtEntityTrigger.TriggerInstance> playerHurtEntityWithDamage(DamagePredicate.Builder var0) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY
            .createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.build()), Optional.empty()));
      }

      public static Criterion<PlayerHurtEntityTrigger.TriggerInstance> playerHurtEntity(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY
            .createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.empty(), EntityPredicate.wrap(var0)));
      }

      public static Criterion<PlayerHurtEntityTrigger.TriggerInstance> playerHurtEntity(Optional<DamagePredicate> var0, Optional<EntityPredicate> var1) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY
            .createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), var0, EntityPredicate.wrap(var1)));
      }

      public static Criterion<PlayerHurtEntityTrigger.TriggerInstance> playerHurtEntity(DamagePredicate.Builder var0, Optional<EntityPredicate> var1) {
         return CriteriaTriggers.PLAYER_HURT_ENTITY
            .createCriterion(new PlayerHurtEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.build()), EntityPredicate.wrap(var1)));
      }

      public boolean matches(ServerPlayer var1, LootContext var2, DamageSource var3, float var4, float var5, boolean var6) {
         if (this.damage.isPresent() && !((DamagePredicate)this.damage.get()).matches(var1, var3, var4, var5, var6)) {
            return false;
         } else {
            return !this.entity.isPresent() || this.entity.get().matches(var2);
         }
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.entity, ".entity");
      }
   }
}
