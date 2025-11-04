package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.attribute.LerpFunction;

public class KeyframeTrackSampler<T> {
   private final Optional<Integer> periodTicks;
   private final LerpFunction<T> lerp;
   private final List<Segment<T>> segments;

   KeyframeTrackSampler(KeyframeTrack<T> var1, Optional<Integer> var2, LerpFunction<T> var3) {
      super();
      this.periodTicks = var2;
      this.lerp = var3;
      this.segments = bakeSegments(var1, var2);
   }

   private static <T> List<Segment<T>> bakeSegments(KeyframeTrack<T> var0, Optional<Integer> var1) {
      List var2 = var0.keyframes();
      if (var2.size() == 1) {
         Object var6 = ((Keyframe)var2.getFirst()).value();
         return List.of(new Segment(EasingType.CONSTANT, var6, 0, var6, 0));
      } else {
         ArrayList var3 = new ArrayList();
         if (var1.isPresent()) {
            Keyframe var4 = (Keyframe)var2.getFirst();
            Keyframe var5 = (Keyframe)var2.getLast();
            var3.add(new Segment(var0, var5, var5.ticks() - (Integer)var1.get(), var4, var4.ticks()));
            addSegmentsFromKeyframes(var0, var2, var3);
            var3.add(new Segment(var0, var5, var5.ticks(), var4, var4.ticks() + (Integer)var1.get()));
         } else {
            addSegmentsFromKeyframes(var0, var2, var3);
         }

         return List.copyOf(var3);
      }
   }

   private static <T> void addSegmentsFromKeyframes(KeyframeTrack<T> var0, List<Keyframe<T>> var1, List<Segment<T>> var2) {
      for(int var3 = 0; var3 < var1.size() - 1; ++var3) {
         Keyframe var4 = (Keyframe)var1.get(var3);
         Keyframe var5 = (Keyframe)var1.get(var3 + 1);
         var2.add(new Segment(var0, var4, var4.ticks(), var5, var5.ticks()));
      }

   }

   public T sample(long var1) {
      long var3 = this.loopTicks(var1);
      Segment var5 = this.getSegmentAt(var3);
      if (var3 <= (long)var5.fromTicks) {
         return var5.fromValue;
      } else if (var3 >= (long)var5.toTicks) {
         return var5.toValue;
      } else {
         float var6 = (float)(var3 - (long)var5.fromTicks) / (float)(var5.toTicks - var5.fromTicks);
         float var7 = var5.easing.apply(var6);
         return this.lerp.apply(var7, var5.fromValue, var5.toValue);
      }
   }

   private Segment<T> getSegmentAt(long var1) {
      for(Segment var4 : this.segments) {
         if (var1 < (long)var4.toTicks) {
            return var4;
         }
      }

      return (Segment)this.segments.getLast();
   }

   private long loopTicks(long var1) {
      return this.periodTicks.isPresent() ? (long)Math.floorMod(var1, (Integer)this.periodTicks.get()) : var1;
   }

   static record Segment<T>(EasingType easing, T fromValue, int fromTicks, T toValue, int toTicks) {
      final EasingType easing;
      final T fromValue;
      final int fromTicks;
      final T toValue;
      final int toTicks;

      public Segment(KeyframeTrack<T> var1, Keyframe<T> var2, int var3, Keyframe<T> var4, int var5) {
         this(var1.easingType(), var2.value(), var3, var4.value(), var5);
      }

      Segment(EasingType var1, T var2, int var3, T var4, int var5) {
         super();
         this.easing = var1;
         this.fromValue = var2;
         this.fromTicks = var3;
         this.toValue = var4;
         this.toTicks = var5;
      }
   }
}
