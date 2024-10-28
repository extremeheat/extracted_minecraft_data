package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record MobEffectsPredicate(Map<Holder<MobEffect>, MobEffectInstancePredicate> effectMap) {
   public static final Codec<MobEffectsPredicate> CODEC;

   public MobEffectsPredicate(Map<Holder<MobEffect>, MobEffectInstancePredicate> var1) {
      super();
      this.effectMap = var1;
   }

   public boolean matches(Entity var1) {
      boolean var10000;
      if (var1 instanceof LivingEntity var2) {
         if (this.matches(var2.getActiveEffectsMap())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean matches(LivingEntity var1) {
      return this.matches(var1.getActiveEffectsMap());
   }

   public boolean matches(Map<Holder<MobEffect>, MobEffectInstance> var1) {
      Iterator var2 = this.effectMap.entrySet().iterator();

      Map.Entry var3;
      MobEffectInstance var4;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Map.Entry)var2.next();
         var4 = (MobEffectInstance)var1.get(var3.getKey());
      } while(((MobEffectInstancePredicate)var3.getValue()).matches(var4));

      return false;
   }

   public Map<Holder<MobEffect>, MobEffectInstancePredicate> effectMap() {
      return this.effectMap;
   }

   static {
      CODEC = Codec.unboundedMap(BuiltInRegistries.MOB_EFFECT.holderByNameCodec(), MobEffectsPredicate.MobEffectInstancePredicate.CODEC).xmap(MobEffectsPredicate::new, MobEffectsPredicate::effectMap);
   }

   public static record MobEffectInstancePredicate(MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, Optional<Boolean> ambient, Optional<Boolean> visible) {
      public static final Codec<MobEffectInstancePredicate> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("amplifier", MinMaxBounds.Ints.ANY).forGetter(MobEffectInstancePredicate::amplifier), MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(MobEffectInstancePredicate::duration), Codec.BOOL.optionalFieldOf("ambient").forGetter(MobEffectInstancePredicate::ambient), Codec.BOOL.optionalFieldOf("visible").forGetter(MobEffectInstancePredicate::visible)).apply(var0, MobEffectInstancePredicate::new);
      });

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, Optional.empty(), Optional.empty());
      }

      public MobEffectInstancePredicate(MinMaxBounds.Ints var1, MinMaxBounds.Ints var2, Optional<Boolean> var3, Optional<Boolean> var4) {
         super();
         this.amplifier = var1;
         this.duration = var2;
         this.ambient = var3;
         this.visible = var4;
      }

      public boolean matches(@Nullable MobEffectInstance var1) {
         if (var1 == null) {
            return false;
         } else if (!this.amplifier.matches(var1.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(var1.getDuration())) {
            return false;
         } else if (this.ambient.isPresent() && (Boolean)this.ambient.get() != var1.isAmbient()) {
            return false;
         } else {
            return !this.visible.isPresent() || (Boolean)this.visible.get() == var1.isVisible();
         }
      }

      public MinMaxBounds.Ints amplifier() {
         return this.amplifier;
      }

      public MinMaxBounds.Ints duration() {
         return this.duration;
      }

      public Optional<Boolean> ambient() {
         return this.ambient;
      }

      public Optional<Boolean> visible() {
         return this.visible;
      }
   }

   public static class Builder {
      private final ImmutableMap.Builder<Holder<MobEffect>, MobEffectInstancePredicate> effectMap = ImmutableMap.builder();

      public Builder() {
         super();
      }

      public static Builder effects() {
         return new Builder();
      }

      public Builder and(Holder<MobEffect> var1) {
         this.effectMap.put(var1, new MobEffectInstancePredicate());
         return this;
      }

      public Builder and(Holder<MobEffect> var1, MobEffectInstancePredicate var2) {
         this.effectMap.put(var1, var2);
         return this;
      }

      public Optional<MobEffectsPredicate> build() {
         return Optional.of(new MobEffectsPredicate(this.effectMap.build()));
      }
   }
}
