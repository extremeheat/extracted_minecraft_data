package net.minecraft.advancements.predicates;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public record MobEffectsPredicate(Map<Holder<MobEffect>, MobEffectInstancePredicate> effectMap) {
   public static final Codec<MobEffectsPredicate> CODEC;

   public MobEffectsPredicate {
      super();
   }

   public boolean matches(final Entity entity) {
      boolean var10000;
      if (entity instanceof LivingEntity living) {
         if (this.matches(living.getActiveEffectsMap())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean matches(final LivingEntity entity) {
      return this.matches(entity.getActiveEffectsMap());
   }

   public boolean matches(final Map<Holder<MobEffect>, MobEffectInstance> effects) {
      for(Map.Entry<Holder<MobEffect>, MobEffectInstancePredicate> entry : this.effectMap.entrySet()) {
         MobEffectInstance instance = (MobEffectInstance)effects.get(entry.getKey());
         if (!((MobEffectInstancePredicate)entry.getValue()).matches(instance)) {
            return false;
         }
      }

      return true;
   }

   static {
      CODEC = Codec.unboundedMap(MobEffect.CODEC, MobEffectsPredicate.MobEffectInstancePredicate.CODEC).xmap(MobEffectsPredicate::new, MobEffectsPredicate::effectMap);
   }

   public static class Builder {
      private final ImmutableMap.Builder<Holder<MobEffect>, MobEffectInstancePredicate> effectMap = ImmutableMap.builder();

      public Builder() {
         super();
      }

      public static Builder effects() {
         return new Builder();
      }

      public Builder and(final Holder<MobEffect> effect) {
         this.effectMap.put(effect, new MobEffectInstancePredicate());
         return this;
      }

      public Builder and(final Holder<MobEffect> effect, final MobEffectInstancePredicate predicate) {
         this.effectMap.put(effect, predicate);
         return this;
      }

      public MobEffectsPredicate build() {
         return new MobEffectsPredicate(this.effectMap.build());
      }
   }

   public static record MobEffectInstancePredicate(MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, Optional<Boolean> ambient, Optional<Boolean> visible) {
      public static final Codec<MobEffectInstancePredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("amplifier", MinMaxBounds.Ints.ANY).forGetter(MobEffectInstancePredicate::amplifier), MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(MobEffectInstancePredicate::duration), Codec.BOOL.optionalFieldOf("ambient").forGetter(MobEffectInstancePredicate::ambient), Codec.BOOL.optionalFieldOf("visible").forGetter(MobEffectInstancePredicate::visible)).apply(i, MobEffectInstancePredicate::new));

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, Optional.empty(), Optional.empty());
      }

      public MobEffectInstancePredicate {
         super();
      }

      public boolean matches(final @Nullable MobEffectInstance instance) {
         if (instance == null) {
            return false;
         } else if (!this.amplifier.matches(instance.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(instance.getDuration())) {
            return false;
         } else if (this.ambient.isPresent() && (Boolean)this.ambient.get() != instance.isAmbient()) {
            return false;
         } else {
            return !this.visible.isPresent() || (Boolean)this.visible.get() == instance.isVisible();
         }
      }
   }
}
