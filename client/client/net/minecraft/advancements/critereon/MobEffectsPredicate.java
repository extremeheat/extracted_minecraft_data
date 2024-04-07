package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record MobEffectsPredicate(Map<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate> effectMap) {
   public static final Codec<MobEffectsPredicate> CODEC = Codec.unboundedMap(
         BuiltInRegistries.MOB_EFFECT.holderByNameCodec(), MobEffectsPredicate.MobEffectInstancePredicate.CODEC
      )
      .xmap(MobEffectsPredicate::new, MobEffectsPredicate::effectMap);

   public MobEffectsPredicate(Map<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate> effectMap) {
      super();
      this.effectMap = effectMap;
   }

   public boolean matches(Entity var1) {
      if (var1 instanceof LivingEntity var2 && this.matches(var2.getActiveEffectsMap())) {
         return true;
      }

      return false;
   }

   public boolean matches(LivingEntity var1) {
      return this.matches(var1.getActiveEffectsMap());
   }

   public boolean matches(Map<Holder<MobEffect>, MobEffectInstance> var1) {
      for (Entry var3 : this.effectMap.entrySet()) {
         MobEffectInstance var4 = (MobEffectInstance)var1.get(var3.getKey());
         if (!((MobEffectsPredicate.MobEffectInstancePredicate)var3.getValue()).matches(var4)) {
            return false;
         }
      }

      return true;
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableMap.Builder<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate> effectMap = ImmutableMap.builder(
         
      );

      public Builder() {
         super();
      }

      public static MobEffectsPredicate.Builder effects() {
         return new MobEffectsPredicate.Builder();
      }

      public MobEffectsPredicate.Builder and(Holder<MobEffect> var1) {
         this.effectMap.put(var1, new MobEffectsPredicate.MobEffectInstancePredicate());
         return this;
      }

      public MobEffectsPredicate.Builder and(Holder<MobEffect> var1, MobEffectsPredicate.MobEffectInstancePredicate var2) {
         this.effectMap.put(var1, var2);
         return this;
      }

      public Optional<MobEffectsPredicate> build() {
         return Optional.of(new MobEffectsPredicate(this.effectMap.build()));
      }
   }

   public static record MobEffectInstancePredicate(
      MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, Optional<Boolean> ambient, Optional<Boolean> visible
   ) {
      public static final Codec<MobEffectsPredicate.MobEffectInstancePredicate> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  MinMaxBounds.Ints.CODEC
                     .optionalFieldOf("amplifier", MinMaxBounds.Ints.ANY)
                     .forGetter(MobEffectsPredicate.MobEffectInstancePredicate::amplifier),
                  MinMaxBounds.Ints.CODEC
                     .optionalFieldOf("duration", MinMaxBounds.Ints.ANY)
                     .forGetter(MobEffectsPredicate.MobEffectInstancePredicate::duration),
                  Codec.BOOL.optionalFieldOf("ambient").forGetter(MobEffectsPredicate.MobEffectInstancePredicate::ambient),
                  Codec.BOOL.optionalFieldOf("visible").forGetter(MobEffectsPredicate.MobEffectInstancePredicate::visible)
               )
               .apply(var0, MobEffectsPredicate.MobEffectInstancePredicate::new)
      );

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, Optional.empty(), Optional.empty());
      }

      public MobEffectInstancePredicate(MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, Optional<Boolean> ambient, Optional<Boolean> visible) {
         super();
         this.amplifier = amplifier;
         this.duration = duration;
         this.ambient = ambient;
         this.visible = visible;
      }

      public boolean matches(@Nullable MobEffectInstance var1) {
         if (var1 == null) {
            return false;
         } else if (!this.amplifier.matches(var1.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(var1.getDuration())) {
            return false;
         } else {
            return this.ambient.isPresent() && this.ambient.get() != var1.isAmbient()
               ? false
               : !this.visible.isPresent() || this.visible.get() == var1.isVisible();
         }
      }
   }
}
