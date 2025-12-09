package net.minecraft.client.model.geom.builders;

public record UVPair(float u, float v) {
   public UVPair(float var1, float var2) {
      super();
      this.u = var1;
      this.v = var2;
   }

   public String toString() {
      return "(" + this.u + "," + this.v + ")";
   }

   public static long pack(float var0, float var1) {
      long var2 = (long)Float.floatToIntBits(var0) & 4294967295L;
      long var4 = (long)Float.floatToIntBits(var1) & 4294967295L;
      return var2 << 32 | var4;
   }

   public static float unpackU(long var0) {
      int var2 = (int)(var0 >> 32);
      return Float.intBitsToFloat(var2);
   }

   public static float unpackV(long var0) {
      return Float.intBitsToFloat((int)var0);
   }
}
