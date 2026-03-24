package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.attribute.LerpFunction;

public class KeyframeTrackSampler<T> {
   private final Optional<Integer> periodTicks;
   private final LerpFunction<T> lerp;
   private final List<Segment<T>> segments;

   KeyframeTrackSampler(final KeyframeTrack<T> track, final Optional<Integer> periodTicks, final LerpFunction<T> lerp) {
      super();
      this.periodTicks = periodTicks;
      this.lerp = lerp;
      this.segments = bakeSegments(track, periodTicks);
   }

   private static <T> List<Segment<T>> bakeSegments(final KeyframeTrack<T> track, final Optional<Integer> periodTicks) {
      List<Keyframe<T>> keyframes = track.keyframes();
      if (keyframes.size() == 1) {
         T value = (T)((Keyframe)keyframes.getFirst()).value();
         return List.of(new Segment(EasingType.CONSTANT, value, 0, value, 0));
      } else {
         List<Segment<T>> segments = new ArrayList();
         if (periodTicks.isPresent()) {
            Keyframe<T> firstKeyframe = (Keyframe)keyframes.getFirst();
            Keyframe<T> lastKeyframe = (Keyframe)keyframes.getLast();
            segments.add(new Segment(track, lastKeyframe, lastKeyframe.ticks() - (Integer)periodTicks.get(), firstKeyframe, firstKeyframe.ticks()));
            addSegmentsFromKeyframes(track, keyframes, segments);
            segments.add(new Segment(track, lastKeyframe, lastKeyframe.ticks(), firstKeyframe, firstKeyframe.ticks() + (Integer)periodTicks.get()));
         } else {
            addSegmentsFromKeyframes(track, keyframes, segments);
         }

         return List.copyOf(segments);
      }
   }

   private static <T> void addSegmentsFromKeyframes(final KeyframeTrack<T> track, final List<Keyframe<T>> keyframes, final List<Segment<T>> output) {
      for(int i = 0; i < keyframes.size() - 1; ++i) {
         Keyframe<T> keyframe = (Keyframe)keyframes.get(i);
         Keyframe<T> nextKeyframe = (Keyframe)keyframes.get(i + 1);
         output.add(new Segment(track, keyframe, keyframe.ticks(), nextKeyframe, nextKeyframe.ticks()));
      }

   }

   public T sample(final long ticks) {
      long sampleTicks = this.loopTicks(ticks);
      Segment<T> segment = this.getSegmentAt(sampleTicks);
      if (sampleTicks <= (long)segment.fromTicks) {
         return segment.fromValue;
      } else if (sampleTicks >= (long)segment.toTicks) {
         return segment.toValue;
      } else {
         float alpha = (float)(sampleTicks - (long)segment.fromTicks) / (float)(segment.toTicks - segment.fromTicks);
         float easedAlpha = segment.easing.apply(alpha);
         return this.lerp.apply(easedAlpha, segment.fromValue, segment.toValue);
      }
   }

   private Segment<T> getSegmentAt(final long currentTicks) {
      for(Segment<T> segment : this.segments) {
         if (currentTicks < (long)segment.toTicks) {
            return segment;
         }
      }

      return (Segment)this.segments.getLast();
   }

   private long loopTicks(final long ticks) {
      return this.periodTicks.isPresent() ? (long)Math.floorMod(ticks, (Integer)this.periodTicks.get()) : ticks;
   }

   private static record Segment<T>(EasingType easing, T fromValue, int fromTicks, T toValue, int toTicks) {
      public Segment(final KeyframeTrack<T> track, final Keyframe<T> from, final int fromTicks, final Keyframe<T> to, final int toTicks) {
         this(track.easingType(), from.value(), fromTicks, to.value(), toTicks);
      }

      private Segment {
         super();
      }
   }
}
