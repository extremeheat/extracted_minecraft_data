package net.minecraft.client.model.geom.builders;

public class UVPair {
   private final float u;
   private final float v;

   public UVPair(float var1, float var2) {
      super();
      this.u = var1;
      this.v = var2;
   }

   public float u() {
      return this.u;
   }

   public float v() {
      return this.v;
   }

   public String toString() {
      return "(" + this.u + "," + this.v + ")";
   }
}
