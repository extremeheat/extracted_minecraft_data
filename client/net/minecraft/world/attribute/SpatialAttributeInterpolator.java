package net.minecraft.world.attribute;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMaps;
import java.util.Objects;

public class SpatialAttributeInterpolator {
   private final Reference2DoubleArrayMap<EnvironmentAttributeMap> weightsBySource = new Reference2DoubleArrayMap();

   public SpatialAttributeInterpolator() {
      super();
   }

   public void clear() {
      this.weightsBySource.clear();
   }

   public SpatialAttributeInterpolator accumulate(final double weight, final EnvironmentAttributeMap attributes) {
      this.weightsBySource.mergeDouble(attributes, weight, Double::sum);
      return this;
   }

   public <Value> Value applyAttributeLayer(final EnvironmentAttribute<Value> attribute, final Value baseValue) {
      if (this.weightsBySource.isEmpty()) {
         return baseValue;
      } else if (this.weightsBySource.size() == 1) {
         EnvironmentAttributeMap sourceAttributes = (EnvironmentAttributeMap)this.weightsBySource.keySet().iterator().next();
         return (Value)sourceAttributes.applyModifier(attribute, baseValue);
      } else {
         LerpFunction<Value> lerp = attribute.type().spatialLerp();
         Value resultValue = null;
         double accumulatedWeight = 0.0;
         ObjectIterator var7 = Reference2DoubleMaps.fastIterable(this.weightsBySource).iterator();

         while(var7.hasNext()) {
            Reference2DoubleMap.Entry<EnvironmentAttributeMap> entry = (Reference2DoubleMap.Entry)var7.next();
            EnvironmentAttributeMap sourceAttributes = (EnvironmentAttributeMap)entry.getKey();
            double sourceWeight = entry.getDoubleValue();
            Value sourceValue = (Value)sourceAttributes.applyModifier(attribute, baseValue);
            accumulatedWeight += sourceWeight;
            if (resultValue == null) {
               resultValue = sourceValue;
            } else {
               float relativeFraction = (float)(sourceWeight / accumulatedWeight);
               resultValue = lerp.apply(relativeFraction, resultValue, sourceValue);
            }
         }

         return (Value)Objects.requireNonNull(resultValue);
      }
   }
}
