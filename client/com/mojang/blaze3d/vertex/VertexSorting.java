package com.mojang.blaze3d.vertex;

import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Objects;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface VertexSorting {
   VertexSorting DISTANCE_TO_ORIGIN = byDistance(0.0F, 0.0F, 0.0F);
   VertexSorting ORTHOGRAPHIC_Z = byDistance((DistanceFunction)((var0) -> -var0.z()));

   static VertexSorting byDistance(float var0, float var1, float var2) {
      return byDistance((Vector3fc)(new Vector3f(var0, var1, var2)));
   }

   static VertexSorting byDistance(Vector3fc var0) {
      Objects.requireNonNull(var0);
      return byDistance(var0::distanceSquared);
   }

   static VertexSorting byDistance(DistanceFunction var0) {
      return (var1) -> {
         Vector3f var2 = new Vector3f();
         float[] var3 = new float[var1.size()];
         int[] var4 = new int[var1.size()];

         for(int var5 = 0; var5 < var1.size(); var4[var5] = var5++) {
            var3[var5] = var0.apply(var1.get(var5, var2));
         }

         IntArrays.mergeSort(var4, (var1x, var2x) -> Floats.compare(var3[var2x], var3[var1x]));
         return var4;
      };
   }

   int[] sort(CompactVectorArray var1);

   @FunctionalInterface
   public interface DistanceFunction {
      float apply(Vector3f var1);
   }
}
