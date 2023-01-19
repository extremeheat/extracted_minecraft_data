package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;

public class Rotations {
   protected final float x;
   protected final float y;
   protected final float z;

   public Rotations(float var1, float var2, float var3) {
      super();
      this.x = !Float.isInfinite(var1) && !Float.isNaN(var1) ? var1 % 360.0F : 0.0F;
      this.y = !Float.isInfinite(var2) && !Float.isNaN(var2) ? var2 % 360.0F : 0.0F;
      this.z = !Float.isInfinite(var3) && !Float.isNaN(var3) ? var3 % 360.0F : 0.0F;
   }

   public Rotations(ListTag var1) {
      this(var1.getFloat(0), var1.getFloat(1), var1.getFloat(2));
   }

   public ListTag save() {
      ListTag var1 = new ListTag();
      var1.add(FloatTag.valueOf(this.x));
      var1.add(FloatTag.valueOf(this.y));
      var1.add(FloatTag.valueOf(this.z));
      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Rotations)) {
         return false;
      } else {
         Rotations var2 = (Rotations)var1;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public float getWrappedX() {
      return Mth.wrapDegrees(this.x);
   }

   public float getWrappedY() {
      return Mth.wrapDegrees(this.y);
   }

   public float getWrappedZ() {
      return Mth.wrapDegrees(this.z);
   }
}
