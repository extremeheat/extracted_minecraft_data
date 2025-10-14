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

   public SpatialAttributeInterpolator accumulate(double var1, EnvironmentAttributeMap var3) {
      this.weightsBySource.mergeDouble(var3, var1, Double::sum);
      return this;
   }

   public <Value> Value applyAttributeLayer(EnvironmentAttribute<Value> var1, Value var2) {
      if (this.weightsBySource.isEmpty()) {
         return var2;
      } else if (this.weightsBySource.size() == 1) {
         EnvironmentAttributeMap var14 = (EnvironmentAttributeMap)this.weightsBySource.keySet().iterator().next();
         return (Value)var14.applyModifier(var1, var2);
      } else {
         LerpFunction var3 = var1.type().spatialLerp();
         Object var4 = null;
         double var5 = 0.0;
         ObjectIterator var7 = Reference2DoubleMaps.fastIterable(this.weightsBySource).iterator();

         while(var7.hasNext()) {
            Reference2DoubleMap.Entry var8 = (Reference2DoubleMap.Entry)var7.next();
            EnvironmentAttributeMap var9 = (EnvironmentAttributeMap)var8.getKey();
            double var10 = var8.getDoubleValue();
            Object var12 = var9.applyModifier(var1, var2);
            var5 += var10;
            if (var4 == null) {
               var4 = var12;
            } else {
               float var13 = (float)(var10 / var5);
               var4 = var3.apply(var13, var4, var12);
            }
         }

         return (Value)Objects.requireNonNull(var4);
      }
   }
}
