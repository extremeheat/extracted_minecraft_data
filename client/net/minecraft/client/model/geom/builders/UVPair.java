package net.minecraft.client.model.geom.builders;

public class UVPair {
   // $FF: renamed from: u float
   private final float field_366;
   // $FF: renamed from: v float
   private final float field_367;

   public UVPair(float var1, float var2) {
      super();
      this.field_366 = var1;
      this.field_367 = var2;
   }

   // $FF: renamed from: u () float
   public float method_98() {
      return this.field_366;
   }

   // $FF: renamed from: v () float
   public float method_99() {
      return this.field_367;
   }

   public String toString() {
      return "(" + this.field_366 + "," + this.field_367 + ")";
   }
}
