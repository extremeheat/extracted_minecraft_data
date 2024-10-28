package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EntityHurtPlayerTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EntityHurtPlayerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      this.trigger(var1, (var5x) -> {
         return var5x.matches(var1, var2, var3, var4, var5);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DamagePredicate> damage) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(TriggerInstance::damage)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DamagePredicate> damage) {
         super();
         this.player = player;
         this.damage = damage;
      }

      public static Criterion<TriggerInstance> entityHurtPlayer() {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityHurtPlayer(DamagePredicate var0) {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(var0)));
      }

      public static Criterion<TriggerInstance> entityHurtPlayer(DamagePredicate.Builder var0) {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(var0.build())));
      }

      public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
         return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).matches(var1, var2, var3, var4, var5);
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<DamagePredicate> damage() {
         return this.damage;
      }
   }
}
