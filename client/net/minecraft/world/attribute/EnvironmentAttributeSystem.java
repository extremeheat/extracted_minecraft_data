package net.minecraft.world.attribute;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.timeline.Timeline;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeSystem implements EnvironmentAttributeReader {
   private final Map<EnvironmentAttribute<?>, ValueSampler<?>> attributeSamplers = new Reference2ObjectOpenHashMap();

   private EnvironmentAttributeSystem(final Map<EnvironmentAttribute<?>, List<EnvironmentAttributeLayer<?>>> layersByAttribute) {
      super();
      layersByAttribute.forEach((attribute, layers) -> this.attributeSamplers.put(attribute, this.bakeLayerSampler(attribute, layers)));
   }

   private <Value> ValueSampler<Value> bakeLayerSampler(final EnvironmentAttribute<Value> attribute, final List<? extends EnvironmentAttributeLayer<?>> untypedLayers) {
      List<EnvironmentAttributeLayer<Value>> layers = new ArrayList(untypedLayers);
      Value constantBaseValue = attribute.defaultValue();

      while(!layers.isEmpty()) {
         Object var6 = layers.getFirst();
         if (!(var6 instanceof EnvironmentAttributeLayer.Constant)) {
            break;
         }

         EnvironmentAttributeLayer.Constant<Value> constantLayer = (EnvironmentAttributeLayer.Constant)var6;
         constantBaseValue = constantLayer.applyConstant(constantBaseValue);
         layers.removeFirst();
      }

      boolean isAffectedByPosition = layers.stream().anyMatch((layer) -> layer instanceof EnvironmentAttributeLayer.Positional);
      return new ValueSampler<Value>(attribute, constantBaseValue, List.copyOf(layers), isAffectedByPosition);
   }

   public static Builder builder() {
      return new Builder();
   }

   private static void addStaticLayers(final Builder builder, final LevelAccessor level) {
      RegistryAccess registries = level.registryAccess();
      BiomeManager biomeManager = level.getBiomeManager();
      addDimensionLayer(builder, level.dimensionType());
      addBiomeLayer(builder, registries.lookupOrThrow(Registries.BIOME), biomeManager);
   }

   private static void addDynamicLayers(final Builder builder, final Level level) {
      ClockManager clockManager = level.clockManager();
      level.dimensionType().timelines().forEach((timeline) -> builder.addTimelineLayer(timeline, clockManager));
      if (level.canHaveWeather()) {
         WeatherAttributes.addBuiltinLayers(builder, WeatherAttributes.WeatherAccess.from(level));
      }

   }

   private static void addDimensionLayer(final Builder builder, final DimensionType dimensionType) {
      builder.addConstantLayer(dimensionType.attributes());
   }

   private static void addBiomeLayer(final Builder builder, final HolderLookup<Biome> biomes, final BiomeManager biomeManager) {
      Stream<EnvironmentAttribute<?>> attributesProvidedByBiomes = biomes.listElements().flatMap((biome) -> ((Biome)biome.value()).getAttributes().keySet().stream()).distinct();
      attributesProvidedByBiomes.forEach((attribute) -> addBiomeLayerForAttribute(builder, attribute, biomeManager));
   }

   private static <Value> void addBiomeLayerForAttribute(final Builder builder, final EnvironmentAttribute<Value> attribute, final BiomeManager biomeManager) {
      builder.addPositionalLayer(attribute, (baseValue, pos, biomeWeights) -> {
         if (biomeWeights != null && attribute.isSpatiallyInterpolated()) {
            return biomeWeights.applyAttributeLayer(attribute, baseValue);
         } else {
            Holder<Biome> biome = attribute.isFullResolutionBiomes() ? biomeManager.getBiome(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z)) : biomeManager.getNoiseBiomeAtPosition(pos.x, pos.y, pos.z);
            return ((Biome)biome.value()).getAttributes().applyModifier(attribute, baseValue);
         }
      });
   }

   public void invalidateTickCache() {
      this.attributeSamplers.values().forEach(ValueSampler::invalidateTickCache);
   }

   private <Value> @Nullable ValueSampler<Value> getValueSampler(final EnvironmentAttribute<Value> attribute) {
      return (ValueSampler)this.attributeSamplers.get(attribute);
   }

   public <Value> Value getDimensionValue(final EnvironmentAttribute<Value> attribute) {
      if (SharedConstants.IS_RUNNING_IN_IDE && attribute.isPositional()) {
         throw new IllegalStateException("Position must always be provided for positional attribute " + String.valueOf(attribute));
      } else {
         ValueSampler<Value> sampler = this.<Value>getValueSampler(attribute);
         return (Value)(sampler == null ? attribute.defaultValue() : sampler.getDimensionValue());
      }
   }

   public <Value> Value getValue(final EnvironmentAttribute<Value> attribute, final Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
      ValueSampler<Value> sampler = this.<Value>getValueSampler(attribute);
      return (Value)(sampler == null ? attribute.defaultValue() : sampler.getValue(pos, biomeInterpolator));
   }

   @VisibleForTesting
   <Value> Value getConstantBaseValue(final EnvironmentAttribute<Value> attribute) {
      ValueSampler<Value> sampler = this.<Value>getValueSampler(attribute);
      return (Value)(sampler != null ? sampler.baseValue : attribute.defaultValue());
   }

   @VisibleForTesting
   boolean isAffectedByPosition(final EnvironmentAttribute<?> attribute) {
      ValueSampler<?> sampler = this.getValueSampler(attribute);
      return sampler != null && sampler.isAffectedByPosition;
   }

   public static class Builder {
      private final Map<EnvironmentAttribute<?>, List<EnvironmentAttributeLayer<?>>> layersByAttribute = new HashMap();

      private Builder() {
         super();
      }

      public Builder addDefaultLayers(final Level level) {
         this.addStaticLayers(level);
         this.addDynamicLayers(level);
         return this;
      }

      public Builder addStaticLayers(final LevelAccessor level) {
         EnvironmentAttributeSystem.addStaticLayers(this, level);
         return this;
      }

      public Builder addDynamicLayers(final Level level) {
         EnvironmentAttributeSystem.addDynamicLayers(this, level);
         return this;
      }

      public Builder addConstantLayer(final EnvironmentAttributeMap attributeMap) {
         for(EnvironmentAttribute<?> attribute : attributeMap.keySet()) {
            this.addConstantEntry(attribute, attributeMap);
         }

         return this;
      }

      private <Value> Builder addConstantEntry(final EnvironmentAttribute<Value> attribute, final EnvironmentAttributeMap attributeMap) {
         EnvironmentAttributeMap.Entry<Value, ?> entry = attributeMap.get(attribute);
         if (entry == null) {
            throw new IllegalArgumentException("Missing attribute " + String.valueOf(attribute));
         } else {
            Objects.requireNonNull(entry);
            return this.addConstantLayer(attribute, entry::applyModifier);
         }
      }

      public <Value> Builder addConstantLayer(final EnvironmentAttribute<Value> attribute, final EnvironmentAttributeLayer.Constant<Value> layer) {
         return this.addLayer(attribute, layer);
      }

      public <Value> Builder addTimeBasedLayer(final EnvironmentAttribute<Value> attribute, final EnvironmentAttributeLayer.TimeBased<Value> layer) {
         return this.addLayer(attribute, layer);
      }

      public <Value> Builder addPositionalLayer(final EnvironmentAttribute<Value> attribute, final EnvironmentAttributeLayer.Positional<Value> layer) {
         return this.addLayer(attribute, layer);
      }

      private <Value> Builder addLayer(final EnvironmentAttribute<Value> attribute, final EnvironmentAttributeLayer<Value> layer) {
         ((List)this.layersByAttribute.computeIfAbsent(attribute, (t) -> new ArrayList())).add(layer);
         return this;
      }

      public Builder addTimelineLayer(final Holder<Timeline> timeline, final ClockManager clockManager) {
         for(EnvironmentAttribute<?> attribute : (timeline.value()).attributes()) {
            this.addTimelineLayerForAttribute(timeline, attribute, clockManager);
         }

         return this;
      }

      private <Value> void addTimelineLayerForAttribute(final Holder<Timeline> timeline, final EnvironmentAttribute<Value> attribute, final ClockManager clockManager) {
         this.addTimeBasedLayer(attribute, (timeline.value()).createTrackSampler(attribute, clockManager));
      }

      public EnvironmentAttributeSystem build() {
         return new EnvironmentAttributeSystem(this.layersByAttribute);
      }
   }

   private static class ValueSampler<Value> {
      private final EnvironmentAttribute<Value> attribute;
      private final Value baseValue;
      private final List<EnvironmentAttributeLayer<Value>> layers;
      private final boolean isAffectedByPosition;
      private @Nullable Value cachedTickValue;
      private int cacheTickId;

      private ValueSampler(final EnvironmentAttribute<Value> attribute, final Value baseValue, final List<EnvironmentAttributeLayer<Value>> layers, final boolean isAffectedByPosition) {
         super();
         this.attribute = attribute;
         this.baseValue = baseValue;
         this.layers = layers;
         this.isAffectedByPosition = isAffectedByPosition;
      }

      public void invalidateTickCache() {
         this.cachedTickValue = null;
         ++this.cacheTickId;
      }

      public Value getDimensionValue() {
         if (this.cachedTickValue != null) {
            return this.cachedTickValue;
         } else {
            Value result = (Value)this.computeValueNotPositional();
            this.cachedTickValue = result;
            return result;
         }
      }

      public Value getValue(final Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
         return (Value)(!this.isAffectedByPosition ? this.getDimensionValue() : this.computeValuePositional(pos, biomeInterpolator));
      }

      private Value computeValuePositional(final Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
         Value result = this.baseValue;

         for(EnvironmentAttributeLayer<Value> layer : this.layers) {
            Objects.requireNonNull(layer);
            byte var7 = 0;
            Object var10000;
            //$FF: var7->value
            //0->net/minecraft/world/attribute/EnvironmentAttributeLayer$Constant
            //1->net/minecraft/world/attribute/EnvironmentAttributeLayer$TimeBased
            //2->net/minecraft/world/attribute/EnvironmentAttributeLayer$Positional
            switch (layer.typeSwitch<invokedynamic>(layer, var7)) {
               case 0:
                  EnvironmentAttributeLayer.Constant<Value> constantLayer = (EnvironmentAttributeLayer.Constant)layer;
                  var10000 = constantLayer.applyConstant(result);
                  break;
               case 1:
                  EnvironmentAttributeLayer.TimeBased<Value> timeBasedLayer = (EnvironmentAttributeLayer.TimeBased)layer;
                  var10000 = timeBasedLayer.applyTimeBased(result, this.cacheTickId);
                  break;
               case 2:
                  EnvironmentAttributeLayer.Positional<Value> positionalLayer = (EnvironmentAttributeLayer.Positional)layer;
                  var10000 = positionalLayer.applyPositional(result, (Vec3)Objects.requireNonNull(pos), biomeInterpolator);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            result = (Value)var10000;
         }

         return this.attribute.sanitizeValue(result);
      }

      private Value computeValueNotPositional() {
         Value result = this.baseValue;

         for(EnvironmentAttributeLayer<Value> layer : this.layers) {
            Objects.requireNonNull(layer);
            byte var5 = 0;
            Object var10000;
            //$FF: var5->value
            //0->net/minecraft/world/attribute/EnvironmentAttributeLayer$Constant
            //1->net/minecraft/world/attribute/EnvironmentAttributeLayer$TimeBased
            //2->net/minecraft/world/attribute/EnvironmentAttributeLayer$Positional
            switch (layer.typeSwitch<invokedynamic>(layer, var5)) {
               case 0:
                  EnvironmentAttributeLayer.Constant<Value> constantLayer = (EnvironmentAttributeLayer.Constant)layer;
                  var10000 = constantLayer.applyConstant(result);
                  break;
               case 1:
                  EnvironmentAttributeLayer.TimeBased<Value> timeBasedLayer = (EnvironmentAttributeLayer.TimeBased)layer;
                  var10000 = timeBasedLayer.applyTimeBased(result, this.cacheTickId);
                  break;
               case 2:
                  EnvironmentAttributeLayer.Positional<Value> ignored = (EnvironmentAttributeLayer.Positional)layer;
                  var10000 = result;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            result = (Value)var10000;
         }

         return this.attribute.sanitizeValue(result);
      }
   }
}
