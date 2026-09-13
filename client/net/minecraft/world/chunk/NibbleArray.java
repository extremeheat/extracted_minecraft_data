package net.minecraft.world.chunk;

public class NibbleArray {
   public final byte[] field_76585_a;
   private final int field_76583_b;
   private final int field_76584_c;

   public NibbleArray(int var1, int var2) {
      super();
      this.field_76585_a = new byte[var1 >> 1];
      this.field_76583_b = var2;
      this.field_76584_c = var2 + 4;
   }

   public NibbleArray(byte[] var1, int var2) {
      super();
      this.field_76585_a = var1;
      this.field_76583_b = var2;
      this.field_76584_c = var2 + 4;
   }

   public int func_76582_a(int var1, int var2, int var3) {
      int var4 = var2 << this.field_76584_c | var3 << this.field_76583_b | var1;
      int var5 = var4 >> 1;
      int var6 = var4 & 1;
      return var6 == 0 ? this.field_76585_a[var5] & 15 : this.field_76585_a[var5] >> 4 & 15;
   }

   public void func_76581_a(int var1, int var2, int var3, int var4) {
      int var5 = var2 << this.field_76584_c | var3 << this.field_76583_b | var1;
      int var6 = var5 >> 1;
      int var7 = var5 & 1;
      if (var7 == 0) {
         this.field_76585_a[var6] = (byte)(this.field_76585_a[var6] & 240 | var4 & 15);
      } else {
         this.field_76585_a[var6] = (byte)(this.field_76585_a[var6] & 15 | (var4 & 15) << 4);
      }
   }
}
