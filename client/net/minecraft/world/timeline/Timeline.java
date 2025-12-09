package net.minecraft.world.timeline;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.level.Level;

public class Timeline {
   public static final Codec<Holder<Timeline>> CODEC;
   private static final Codec<Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>>> TRACKS_CODEC;
   public static final Codec<Timeline> DIRECT_CODEC;
   public static final Codec<Timeline> NETWORK_CODEC;
   private final Optional<Integer> periodTicks;
   private final Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks;

   private static Timeline filterSyncableTracks(Timeline var0) {
      Map var1 = Map.copyOf(Maps.filterKeys(var0.tracks, EnvironmentAttribute::isSyncable));
      return new Timeline(var0.periodTicks, var1);
   }

   Timeline(Optional<Integer> var1, Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> var2) {
      super();
      this.periodTicks = var1;
      this.tracks = var2;
   }

   private static DataResult<Timeline> validateInternal(Timeline var0) {
      if (var0.periodTicks.isEmpty()) {
         return DataResult.success(var0);
      } else {
         int var1 = (Integer)var0.periodTicks.get();
         DataResult var2 = DataResult.success(var0);

         for(AttributeTrack var4 : var0.tracks.values()) {
            var2 = var2.apply2stable((var0x, var1x) -> var0x, AttributeTrack.validatePeriod(var4, var1));
         }

         return var2;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public long getCurrentTicks(Level var1) {
      long var2 = this.getTotalTicks(var1);
      return this.periodTicks.isEmpty() ? var2 : var2 % (long)(Integer)this.periodTicks.get();
   }

   public long getTotalTicks(Level var1) {
      return var1.getDayTime();
   }

   public Optional<Integer> periodTicks() {
      return this.periodTicks;
   }

   public Set<EnvironmentAttribute<?>> attributes() {
      return this.tracks.keySet();
   }

   public <Value> AttributeTrackSampler<Value, ?> createTrackSampler(EnvironmentAttribute<Value> var1, LongSupplier var2) {
      AttributeTrack var3 = (AttributeTrack)this.tracks.get(var1);
      if (var3 == null) {
         throw new IllegalStateException("Timeline has no track for " + String.valueOf(var1));
      } else {
         return var3.bakeSampler(var1, this.periodTicks, var2);
      }
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<Timeline>>create(Registries.TIMELINE);
      TRACKS_CODEC = Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(AttributeTrack::createCodec));
      DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("period_ticks").forGetter((var0x) -> var0x.periodTicks), TRACKS_CODEC.optionalFieldOf("tracks", Map.of()).forGetter((var0x) -> var0x.tracks)).apply(var0, Timeline::new)).validate(Timeline::validateInternal);
      NETWORK_CODEC = DIRECT_CODEC.xmap(Timeline::filterSyncableTracks, Timeline::filterSyncableTracks);
   }

   public static class Builder {
      private Optional<Integer> periodTicks = Optional.empty();
      private final ImmutableMap.Builder<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks = ImmutableMap.builder();

      Builder() {
         super();
      }

      public Builder setPeriodTicks(int var1) {
         this.periodTicks = Optional.of(var1);
         return this;
      }

      public <Value, Argument> Builder addModifierTrack(EnvironmentAttribute<Value> var1, AttributeModifier<Value, Argument> var2, Consumer<KeyframeTrack.Builder<Argument>> var3) {
         var1.type().checkAllowedModifier(var2);
         KeyframeTrack.Builder var4 = new KeyframeTrack.Builder();
         var3.accept(var4);
         this.tracks.put(var1, new AttributeTrack(var2, var4.build()));
         return this;
      }

      public <Value> Builder addTrack(EnvironmentAttribute<Value> var1, Consumer<KeyframeTrack.Builder<Value>> var2) {
         return this.addModifierTrack(var1, AttributeModifier.override(), var2);
      }

      public Timeline build() {
         return new Timeline(this.periodTicks, this.tracks.build());
      }
   }
}
