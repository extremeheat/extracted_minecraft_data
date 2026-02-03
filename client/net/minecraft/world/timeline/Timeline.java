package net.minecraft.world.timeline;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;

public class Timeline {
   public static final Codec<Holder<Timeline>> CODEC;
   private static final Codec<Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>>> TRACKS_CODEC;
   public static final Codec<Timeline> DIRECT_CODEC;
   public static final Codec<Timeline> NETWORK_CODEC;
   private final Holder<WorldClock> clock;
   private final Optional<Integer> periodTicks;
   private final Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks;
   private final Map<ResourceKey<ClockTimeMarker>, TimeMarkerInfo> timeMarkers;

   private static Timeline filterSyncableTracks(final Timeline timeline) {
      Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> syncableTracks = Map.copyOf(Maps.filterKeys(timeline.tracks, EnvironmentAttribute::isSyncable));
      return new Timeline(timeline.clock, timeline.periodTicks, syncableTracks, timeline.timeMarkers);
   }

   private Timeline(final Holder<WorldClock> clock, final Optional<Integer> periodTicks, final Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks, final Map<ResourceKey<ClockTimeMarker>, TimeMarkerInfo> timeMarkers) {
      super();
      this.clock = clock;
      this.periodTicks = periodTicks;
      this.tracks = tracks;
      this.timeMarkers = timeMarkers;
   }

   public static void validateRegistry(final Registry<Timeline> timelines, final Map<ResourceKey<?>, Exception> loadingErrors) {
      Multimap<Holder<WorldClock>, ResourceKey<ClockTimeMarker>> timeMarkersByClock = HashMultimap.create();
      timelines.listElements().forEach((timeline) -> {
         Holder<WorldClock> clock = ((Timeline)timeline.value()).clock();

         for(ResourceKey<ClockTimeMarker> timeMarker : ((Timeline)timeline.value()).timeMarkers.keySet()) {
            if (!timeMarkersByClock.put(clock, timeMarker)) {
               ResourceKey var10001 = timeline.key();
               String var10004 = String.valueOf(timeMarker);
               loadingErrors.put(var10001, new IllegalStateException(var10004 + " was defined multiple times in " + clock.getRegisteredName()));
            }
         }

      });
   }

   private static DataResult<Timeline> validateInternal(final Timeline timeline) {
      if (timeline.periodTicks.isEmpty()) {
         return DataResult.success(timeline);
      } else {
         int periodTicks = (Integer)timeline.periodTicks.get();

         for(Map.Entry<ResourceKey<ClockTimeMarker>, TimeMarkerInfo> entry : timeline.timeMarkers.entrySet()) {
            int ticks = ((TimeMarkerInfo)entry.getValue()).ticks();
            if (ticks < 0 || ticks >= periodTicks) {
               return DataResult.error(() -> {
                  String var10000 = String.valueOf(entry.getKey());
                  return "Time Marker " + var10000 + " must be in range [0; " + periodTicks + ")";
               });
            }
         }

         DataResult<Timeline> result = DataResult.success(timeline);

         for(AttributeTrack<?, ?> track : timeline.tracks.values()) {
            result = result.apply2stable((t, $) -> t, AttributeTrack.validatePeriod(track, periodTicks));
         }

         return result;
      }
   }

   public static Builder builder(final Holder<WorldClock> clock) {
      return new Builder(clock);
   }

   public int getPeriodCount(final ClockManager clockManager) {
      if (this.periodTicks.isEmpty()) {
         return 0;
      } else {
         long totalTicks = this.getTotalTicks(clockManager);
         return (int)(totalTicks / (long)(Integer)this.periodTicks.get());
      }
   }

   public long getCurrentTicks(final ClockManager clockManager) {
      long totalTicks = this.getTotalTicks(clockManager);
      return this.periodTicks.isEmpty() ? totalTicks : totalTicks % (long)(Integer)this.periodTicks.get();
   }

   public long getTotalTicks(final ClockManager clockManager) {
      return clockManager.getTotalTicks(this.clock);
   }

   public Holder<WorldClock> clock() {
      return this.clock;
   }

   public Optional<Integer> periodTicks() {
      return this.periodTicks;
   }

   public void registerTimeMarkers(final BiConsumer<ResourceKey<ClockTimeMarker>, ClockTimeMarker> output) {
      for(Map.Entry<ResourceKey<ClockTimeMarker>, TimeMarkerInfo> entry : this.timeMarkers.entrySet()) {
         TimeMarkerInfo info = (TimeMarkerInfo)entry.getValue();
         output.accept((ResourceKey)entry.getKey(), new ClockTimeMarker(this.clock, info.ticks, this.periodTicks, info.showInCommands));
      }

   }

   public Set<EnvironmentAttribute<?>> attributes() {
      return this.tracks.keySet();
   }

   public <Value> AttributeTrackSampler<Value, ?> createTrackSampler(final EnvironmentAttribute<Value> attribute, final ClockManager clockManager) {
      AttributeTrack<Value, ?> track = (AttributeTrack)this.tracks.get(attribute);
      if (track == null) {
         throw new IllegalStateException("Timeline has no track for " + String.valueOf(attribute));
      } else {
         return track.bakeSampler(attribute, this.clock, this.periodTicks, clockManager);
      }
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<Timeline>>create(Registries.TIMELINE);
      TRACKS_CODEC = Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(AttributeTrack::createCodec));
      DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(WorldClock.CODEC.fieldOf("clock").forGetter((t) -> t.clock), ExtraCodecs.POSITIVE_INT.optionalFieldOf("period_ticks").forGetter((t) -> t.periodTicks), TRACKS_CODEC.optionalFieldOf("tracks", Map.of()).forGetter((t) -> t.tracks), Codec.unboundedMap(ClockTimeMarker.KEY_CODEC, Timeline.TimeMarkerInfo.CODEC).optionalFieldOf("time_markers", Map.of()).forGetter((t) -> t.timeMarkers)).apply(i, Timeline::new)).validate(Timeline::validateInternal);
      NETWORK_CODEC = DIRECT_CODEC.xmap(Timeline::filterSyncableTracks, Timeline::filterSyncableTracks);
   }

   private static record TimeMarkerInfo(int ticks, boolean showInCommands) {
      private static final Codec<TimeMarkerInfo> FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks").forGetter(TimeMarkerInfo::ticks), Codec.BOOL.optionalFieldOf("show_in_commands", false).forGetter(TimeMarkerInfo::showInCommands)).apply(i, TimeMarkerInfo::new));
      public static final Codec<TimeMarkerInfo> CODEC;

      private TimeMarkerInfo {
         super();
      }

      static {
         CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, FULL_CODEC).xmap((either) -> (TimeMarkerInfo)either.map((t) -> new TimeMarkerInfo(t, false), (t) -> t), (timeMarker) -> timeMarker.showInCommands ? Either.right(timeMarker) : Either.left(timeMarker.ticks));
      }
   }

   public static class Builder {
      private final Holder<WorldClock> clock;
      private Optional<Integer> periodTicks = Optional.empty();
      private final ImmutableMap.Builder<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks = ImmutableMap.builder();
      private final ImmutableMap.Builder<ResourceKey<ClockTimeMarker>, TimeMarkerInfo> timeMarkers = ImmutableMap.builder();

      private Builder(final Holder<WorldClock> clock) {
         super();
         this.clock = clock;
      }

      public Builder setPeriodTicks(final int periodTicks) {
         this.periodTicks = Optional.of(periodTicks);
         return this;
      }

      public <Value, Argument> Builder addModifierTrack(final EnvironmentAttribute<Value> attribute, final AttributeModifier<Value, Argument> modifier, final Consumer<KeyframeTrack.Builder<Argument>> builder) {
         attribute.type().checkAllowedModifier(modifier);
         KeyframeTrack.Builder<Argument> argumentTrack = new KeyframeTrack.Builder<Argument>();
         builder.accept(argumentTrack);
         this.tracks.put(attribute, new AttributeTrack(modifier, argumentTrack.build()));
         return this;
      }

      public <Value> Builder addTrack(final EnvironmentAttribute<Value> attribute, final Consumer<KeyframeTrack.Builder<Value>> builder) {
         return this.addModifierTrack(attribute, AttributeModifier.override(), builder);
      }

      public Builder addTimeMarker(final ResourceKey<ClockTimeMarker> id, final int ticks) {
         return this.addTimeMarker(id, ticks, false);
      }

      public Builder addTimeMarker(final ResourceKey<ClockTimeMarker> id, final int ticks, final boolean showInCommands) {
         this.timeMarkers.put(id, new TimeMarkerInfo(ticks, showInCommands));
         return this;
      }

      public Timeline build() {
         return new Timeline(this.clock, this.periodTicks, this.tracks.build(), this.timeMarkers.build());
      }
   }
}
