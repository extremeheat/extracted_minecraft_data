package net.minecraft.util;

import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.attribute.LerpFunction;

public record KeyframeTrack<T>(List<Keyframe<T>> keyframes, EasingType easingType) {
   public KeyframeTrack(List<Keyframe<T>> var1, EasingType var2) {
      super();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Track has no keyframes");
      } else {
         this.keyframes = var1;
         this.easingType = var2;
      }
   }

   public static <T> MapCodec<KeyframeTrack<T>> mapCodec(Codec<T> var0) {
      Codec var1 = Keyframe.codec(var0).listOf().validate(KeyframeTrack::validateKeyframes);
      return RecordCodecBuilder.mapCodec((var1x) -> var1x.group(var1.fieldOf("keyframes").forGetter(KeyframeTrack::keyframes), EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(KeyframeTrack::easingType)).apply(var1x, KeyframeTrack::new));
   }

   static <T> DataResult<List<Keyframe<T>>> validateKeyframes(List<Keyframe<T>> var0) {
      if (var0.isEmpty()) {
         return DataResult.error(() -> "Keyframes must not be empty");
      } else if (!Comparators.isInOrder(var0, Comparator.comparingInt(Keyframe::ticks))) {
         return DataResult.error(() -> "Keyframes must be ordered by ticks field");
      } else {
         if (var0.size() > 1) {
            int var1 = 0;
            int var2 = ((Keyframe)var0.getLast()).ticks();

            for(Keyframe var4 : var0) {
               if (var4.ticks() == var2) {
                  ++var1;
                  if (var1 > 2) {
                     return DataResult.error(() -> "More than 2 keyframes on same tick: " + var4.ticks());
                  }
               } else {
                  var1 = 0;
               }

               var2 = var4.ticks();
            }
         }

         return DataResult.success(var0);
      }
   }

   public static DataResult<KeyframeTrack<?>> validatePeriod(KeyframeTrack<?> var0, int var1) {
      for(Keyframe var3 : var0.keyframes()) {
         int var4 = var3.ticks();
         if (var4 < 0 || var4 > var1) {
            return DataResult.error(() -> {
               int var10000 = var3.ticks();
               return "Keyframe at tick " + var10000 + " must be in range [0; " + var1 + "]";
            });
         }
      }

      return DataResult.success(var0);
   }

   public KeyframeTrackSampler<T> bakeSampler(Optional<Integer> var1, LerpFunction<T> var2) {
      return new KeyframeTrackSampler<T>(this, var1, var2);
   }

   public static class Builder<T> {
      private final ImmutableList.Builder<Keyframe<T>> keyframes = ImmutableList.builder();
      private EasingType easing;

      public Builder() {
         super();
         this.easing = EasingType.LINEAR;
      }

      public Builder<T> addKeyframe(int var1, T var2) {
         this.keyframes.add(new Keyframe(var1, var2));
         return this;
      }

      public Builder<T> setEasing(EasingType var1) {
         this.easing = var1;
         return this;
      }

      public KeyframeTrack<T> build() {
         List var1 = (List)KeyframeTrack.validateKeyframes(this.keyframes.build()).getOrThrow();
         return new KeyframeTrack<T>(var1, this.easing);
      }
   }
}
