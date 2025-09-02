package com.mojang.blaze3d.vertex;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CompactVectorArray {
   private final float[] contents;

   public CompactVectorArray(int var1) {
      super();
      this.contents = new float[3 * var1];
   }

   public int size() {
      return this.contents.length / 3;
   }

   public void set(int var1, Vector3fc var2) {
      this.set(var1, var2.x(), var2.y(), var2.z());
   }

   public void set(int var1, float var2, float var3, float var4) {
      this.contents[3 * var1 + 0] = var2;
      this.contents[3 * var1 + 1] = var3;
      this.contents[3 * var1 + 2] = var4;
   }

   public Vector3f get(int var1, Vector3f var2) {
      return var2.set(this.contents[3 * var1 + 0], this.contents[3 * var1 + 1], this.contents[3 * var1 + 2]);
   }

   public float getX(int var1) {
      return this.contents[3 * var1 + 0];
   }

   public float getY(int var1) {
      return this.contents[3 * var1 + 1];
   }

   public float getZ(int var1) {
      return this.contents[3 * var1 + 1];
   }
}
