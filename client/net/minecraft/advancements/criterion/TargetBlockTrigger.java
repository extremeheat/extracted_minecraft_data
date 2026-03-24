package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public TargetBlockTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return TargetBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Entity projectile, final Vec3 hitPosition, final int signalStrength) {
      LootContext projectileContext = EntityPredicate.createContext(player, projectile);
      this.trigger(player, (t) -> t.matches(projectileContext, hitPosition, signalStrength));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints signalStrength, Optional<ContextAwarePredicate> projectile) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MinMaxBounds.Ints.CODEC.optionalFieldOf("signal_strength", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::signalStrength), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("projectile").forGetter(TriggerInstance::projectile)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> targetHit(final MinMaxBounds.Ints redstoneSignalStrength, final Optional<ContextAwarePredicate> projectile) {
         return CriteriaTriggers.TARGET_BLOCK_HIT.createCriterion(new TriggerInstance(Optional.empty(), redstoneSignalStrength, projectile));
      }

      public boolean matches(final LootContext projectile, final Vec3 hitPosition, final int signalStrength) {
         if (!this.signalStrength.matches(signalStrength)) {
            return false;
         } else {
            return !this.projectile.isPresent() || ((ContextAwarePredicate)this.projectile.get()).matches(projectile);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "projectile", this.projectile);
      }
   }
}
