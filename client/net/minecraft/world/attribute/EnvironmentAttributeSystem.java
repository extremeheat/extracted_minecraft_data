package net.minecraft.world.attribute;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.DevUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.timeline.AttributeTrackSampler;
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

      while(!SharedConstants.DEBUG_ENABLED && !DevUtil.IS_RUNNING_IN_IDE && !layers.isEmpty()) {
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
      HolderLookup.Provider registries = level.registryAccess();
      addDimensionLayer(builder, level.dimensionType());
      addBiomeLayer(builder, registries.lookupOrThrow(Registries.BIOME), level);
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

   private static void addBiomeLayer(final Builder builder, final HolderLookup<Biome> biomes, final BiomeResolver biomeResolver) {
      Stream<EnvironmentAttribute<?>> attributesProvidedByBiomes = biomes.listElements().flatMap((biome) -> ((Biome)biome.value()).getAttributes().keySet().stream()).distinct();
      attributesProvidedByBiomes.forEach((attribute) -> addBiomeLayerForAttribute(builder, attribute, biomeResolver));
   }

   private static <Value> void addBiomeLayerForAttribute(final Builder builder, final EnvironmentAttribute<Value> attribute, final BiomeResolver biomeResolver) {
      builder.addPositionalLayer(attribute, new BiomeLayer(attribute, biomeResolver));
   }

   public void invalidateTickCache() {
      this.attributeSamplers.values().forEach(ValueSampler::invalidateTickCache);
   }

   void setLayerDisabled(final EnvironmentAttribute<?> attribute, final int layerIndex, final boolean disabled) {
      ValueSampler<?> sampler = this.getValueSampler(attribute);
      if (sampler != null) {
         sampler.setLayerDisabled(layerIndex, disabled);
      }

   }

   private <Value> @Nullable ValueSampler<Value> getValueSampler(final EnvironmentAttribute<Value> attribute) {
      return (ValueSampler)this.attributeSamplers.get(attribute);
   }

   public <Value> Value getDimensionValue(final EnvironmentAttribute<Value> attribute) {
      if (DevUtil.IS_RUNNING_IN_IDE && attribute.isPositional()) {
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

   <Value> EnvironmentAttributeLayerValues getValuePerLayer(final EnvironmentAttribute<Value> attribute, final Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
      ValueSampler<Value> sampler = this.<Value>getValueSampler(attribute);
      return sampler == null ? new EnvironmentAttributeLayerValues(List.of(), attribute.defaultValue()) : sampler.getValuePerLayer(attribute.isPositional() ? pos : null, biomeInterpolator);
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

   private static <Value> @Nullable String describeModifier(final EnvironmentAttribute<Value> attribute, final EnvironmentAttributeLayer<Value> layer, final @Nullable Vec3 pos) {
      Objects.requireNonNull(layer);
      EnvironmentAttributeLayer var3 = layer;
      byte var4 = 0;

      while(true) {
         String var10000;
         //$FF: var4->value
         //0->net/minecraft/world/attribute/EnvironmentAttributeSystem$ConstantLayer
         //1->net/minecraft/world/timeline/AttributeTrackSampler
         //2->net/minecraft/world/attribute/EnvironmentAttributeSystem$BiomeLayer
         switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
            case 0:
               ConstantLayer<Value> constantLayer = (ConstantLayer)var3;
               var10000 = EnvironmentAttributeDebugFormat.describeModifier(attribute.type(), constantLayer.entry().modifier(), constantLayer.entry().argument());
               return var10000;
            case 1:
               AttributeTrackSampler<?, ?> sampler = (AttributeTrackSampler)var3;
               var10000 = EnvironmentAttributeDebugFormat.describeModifier(attribute.type(), sampler.modifier(), sampler.sampleArgument());
               return var10000;
            case 2:
               BiomeLayer<Value> biomeLayer = (BiomeLayer)var3;
               if (pos == null) {
                  var4 = 3;
                  break;
               }

               var10000 = describeBiomeModifier(attribute, biomeLayer.biomeAt(pos));
               return var10000;
            default:
               var10000 = null;
               return var10000;
         }
      }
   }

   private static String describeBiomeModifier(final EnvironmentAttribute<?> attribute, final Holder<Biome> biome) {
      EnvironmentAttributeMap.Entry<?, ?> entry = ((Biome)biome.value()).getAttributes().get(attribute);
      String var10000 = biome.getRegisteredName();
      return var10000 + (entry != null ? " " + EnvironmentAttributeDebugFormat.describeModifier(attribute.type(), entry.modifier(), entry.argument()) : "");
   }

   private static String kindName(final EnvironmentAttributeLayer<?> layer) {
      Objects.requireNonNull(layer);
      byte var2 = 0;
      String var10000;
      //$FF: var2->value
      //0->net/minecraft/world/timeline/AttributeTrackSampler
      //1->net/minecraft/world/attribute/EnvironmentAttributeSystem$BiomeLayer
      //2->net/minecraft/world/attribute/EnvironmentAttributeLayer$Constant
      //3->net/minecraft/world/attribute/EnvironmentAttributeLayer$TimeBased
      //4->net/minecraft/world/attribute/EnvironmentAttributeLayer$Positional
      switch (layer.typeSwitch<invokedynamic>(layer, var2)) {
         case 0 -> var10000 = "Timeline";
         case 1 -> var10000 = "Biome";
         case 2 -> var10000 = "Constant";
         case 3 -> var10000 = "Time-based";
         case 4 -> var10000 = "Positional";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static record ConstantLayer<Value>(EnvironmentAttributeMap.Entry<Value, ?> entry) implements EnvironmentAttributeLayer.Constant<Value> {
      private ConstantLayer {
         super();
      }

      public Value applyConstant(final Value baseValue) {
         return this.entry.applyModifier(baseValue);
      }
   }

   private static record BiomeLayer<Value>(EnvironmentAttribute<Value> attribute, BiomeResolver biomeResolver) implements EnvironmentAttributeLayer.Positional<Value> {
      private BiomeLayer {
         super();
      }

      public Value applyPositional(final Value baseValue, final Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeWeights) {
         return (Value)(biomeWeights != null && this.attribute.isSpatiallyInterpolated() ? biomeWeights.applyAttributeLayer(this.attribute, baseValue) : ((Biome)this.biomeAt(pos).value()).getAttributes().applyModifier(this.attribute, baseValue));
      }

      private Holder<Biome> biomeAt(final Vec3 pos) {
         return this.biomeResolver.getBiome(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
      }
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
            return this.addConstantLayer(attribute, new ConstantLayer(entry));
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
      private List<EnvironmentAttributeLayer<Value>> layers;
      private final List<EnvironmentAttributeLayer<Value>> allLayers;
      private final boolean isAffectedByPosition;
      private @Nullable Value cachedTickValue;
      private int cacheTickId;
      private @Nullable IntSet disabledLayers;

      private ValueSampler(final EnvironmentAttribute<Value> attribute, final Value baseValue, final List<EnvironmentAttributeLayer<Value>> layers, final boolean isAffectedByPosition) {
         super();
         this.attribute = attribute;
         this.baseValue = baseValue;
         this.layers = layers;
         this.allLayers = layers;
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

      private void setLayerDisabled(final int layerIndex, final boolean disabled) {
         if (this.disabledLayers == null) {
            this.disabledLayers = new IntOpenHashSet();
         }

         if (disabled) {
            this.disabledLayers.add(layerIndex);
         } else {
            this.disabledLayers.remove(layerIndex);
         }

         IntStream var10001 = IntStream.range(0, this.allLayers.size()).filter((i) -> !this.disabledLayers.contains(i));
         List var10002 = this.allLayers;
         Objects.requireNonNull(var10002);
         this.layers = var10001.mapToObj(var10002::get).toList();
         this.invalidateTickCache();
      }

      private boolean isDisabled(final int layerIndex) {
         return this.disabledLayers != null && this.disabledLayers.contains(layerIndex);
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

      public EnvironmentAttributeLayerValues getValuePerLayer(final @Nullable Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
         List<EnvironmentAttributeLayerValues.LayerValue> layerValues = new ArrayList();
         Value result = this.baseValue;
         layerValues.add(new EnvironmentAttributeLayerValues.LayerValue(-1, "Base value", (String)null, result, false));

         for(int layerIndex = 0; layerIndex < this.allLayers.size(); ++layerIndex) {
            EnvironmentAttributeLayer<Value> layer = (EnvironmentAttributeLayer)this.allLayers.get(layerIndex);
            boolean disabled = this.isDisabled(layerIndex);
            if (!disabled) {
               Objects.requireNonNull(layer);
               byte var9 = 0;
               Object var10000;
               //$FF: var9->value
               //0->net/minecraft/world/attribute/EnvironmentAttributeLayer$Constant
               //1->net/minecraft/world/attribute/EnvironmentAttributeLayer$TimeBased
               //2->net/minecraft/world/attribute/EnvironmentAttributeLayer$Positional
               switch (layer.typeSwitch<invokedynamic>(layer, var9)) {
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
                     var10000 = pos != null ? positionalLayer.applyPositional(result, pos, biomeInterpolator) : result;
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }

               result = (Value)var10000;
            }

            layerValues.add(new EnvironmentAttributeLayerValues.LayerValue(layerIndex, EnvironmentAttributeSystem.kindName(layer), EnvironmentAttributeSystem.describeModifier(this.attribute, layer, pos), result, disabled));
         }

         return new EnvironmentAttributeLayerValues(List.copyOf(layerValues), this.attribute.sanitizeValue(result));
      }
   }
}
