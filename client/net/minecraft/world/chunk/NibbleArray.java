package net.minecraft.world.chunk;

public class NibbleArray {
   private final byte[] field_76585_a;

   public NibbleArray() {
      super();
      this.field_76585_a = new byte[2048];
   }

   public NibbleArray(byte[] var1) {
      super();
      this.field_76585_a = var1;
      if (var1.length != 2048) {
         throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + var1.length);
      }
   }

   public int func_76582_a(int var1, int var2, int var3) {
      return this.func_177480_a(this.func_177483_b(var1, var2, var3));
   }

   public void func_76581_a(int var1, int var2, int var3, int var4) {
      this.func_177482_a(this.func_177483_b(var1, var2, var3), var4);
   }

   private int func_177483_b(int var1, int var2, int var3) {
      return var2 << 8 | var3 << 4 | var1;
   }

   public int func_177480_a(int var1) {
      int var2 = this.func_177478_c(var1);
      return this.func_177479_b(var1) ? this.field_76585_a[var2] & 15 : this.field_76585_a[var2] >> 4 & 15;
   }

   public void func_177482_a(int var1, int var2) {
      int var3 = this.func_177478_c(var1);
      if (this.func_177479_b(var1)) {
         this.field_76585_a[var3] = (byte)(this.field_76585_a[var3] & 240 | var2 & 15);
      } else {
         this.field_76585_a[var3] = (byte)(this.field_76585_a[var3] & 15 | (var2 & 15) << 4);
      }

   }

   private boolean func_177479_b(int var1) {
      return (var1 & 1) == 0;
   }

   private int func_177478_c(int var1) {
      return var1 >> 1;
   }

   public byte[] func_177481_a() {
      return this.field_76585_a;
   }
}
