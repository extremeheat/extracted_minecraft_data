package com.mojang.blaze3d.vertex;

import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.ints.IntArrays;
import org.joml.Vector3f;

public interface VertexSorting {
   VertexSorting DISTANCE_TO_ORIGIN = byDistance(0.0F, 0.0F, 0.0F);
   VertexSorting ORTHOGRAPHIC_Z = byDistance((VertexSorting.DistanceFunction)(var0 -> -var0.z()));

   static VertexSorting byDistance(float var0, float var1, float var2) {
      return byDistance(new Vector3f(var0, var1, var2));
   }

   static VertexSorting byDistance(Vector3f var0) {
      return byDistance(var0::distanceSquared);
   }

   static VertexSorting byDistance(VertexSorting.DistanceFunction var0) {
      return var1 -> {
         float[] var2 = new float[var1.length];
         int[] var3 = new int[var1.length];

         for (int var4 = 0; var4 < var1.length; var3[var4] = var4++) {
            var2[var4] = var0.apply(var1[var4]);
         }

         IntArrays.mergeSort(var3, (var1x, var2x) -> Floats.compare(var2[var2x], var2[var1x]));
         return var3;
      };
   }

   int[] sort(Vector3f[] var1);

   public interface DistanceFunction {
      float apply(Vector3f var1);
   }
}
