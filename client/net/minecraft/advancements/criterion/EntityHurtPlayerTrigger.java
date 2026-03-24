package net.minecraft.advancements.criterion;

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

   public void trigger(final ServerPlayer player, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
      this.trigger(player, (t) -> t.matches(player, source, originalDamage, actualDamage, blocked));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DamagePredicate> damage) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(TriggerInstance::damage)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> entityHurtPlayer() {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> entityHurtPlayer(final DamagePredicate damage) {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(damage)));
      }

      public static Criterion<TriggerInstance> entityHurtPlayer(final DamagePredicate.Builder damage) {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(damage.build())));
      }

      public boolean matches(final ServerPlayer player, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
         return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).matches(player, source, originalDamage, actualDamage, blocked);
      }
   }
}
