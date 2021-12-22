package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;

public class Rotations {
   // $FF: renamed from: x float
   protected final float field_37;
   // $FF: renamed from: y float
   protected final float field_38;
   // $FF: renamed from: z float
   protected final float field_39;

   public Rotations(float var1, float var2, float var3) {
      super();
      this.field_37 = !Float.isInfinite(var1) && !Float.isNaN(var1) ? var1 % 360.0F : 0.0F;
      this.field_38 = !Float.isInfinite(var2) && !Float.isNaN(var2) ? var2 % 360.0F : 0.0F;
      this.field_39 = !Float.isInfinite(var3) && !Float.isNaN(var3) ? var3 % 360.0F : 0.0F;
   }

   public Rotations(ListTag var1) {
      this(var1.getFloat(0), var1.getFloat(1), var1.getFloat(2));
   }

   public ListTag save() {
      ListTag var1 = new ListTag();
      var1.add(FloatTag.valueOf(this.field_37));
      var1.add(FloatTag.valueOf(this.field_38));
      var1.add(FloatTag.valueOf(this.field_39));
      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Rotations)) {
         return false;
      } else {
         Rotations var2 = (Rotations)var1;
         return this.field_37 == var2.field_37 && this.field_38 == var2.field_38 && this.field_39 == var2.field_39;
      }
   }

   public float getX() {
      return this.field_37;
   }

   public float getY() {
      return this.field_38;
   }

   public float getZ() {
      return this.field_39;
   }

   public float getWrappedX() {
      return Mth.wrapDegrees(this.field_37);
   }

   public float getWrappedY() {
      return Mth.wrapDegrees(this.field_38);
   }

   public float getWrappedZ() {
      return Mth.wrapDegrees(this.field_39);
   }
}
