package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
   public final byte[] field_76689_a;
   private final int field_76687_b;
   private final int field_76688_c;

   public NibbleArrayReader(byte[] var1, int var2) {
      super();
      this.field_76689_a = var1;
      this.field_76687_b = var2;
      this.field_76688_c = var2 + 4;
   }

   public int func_76686_a(int var1, int var2, int var3) {
      int var4 = var1 << this.field_76688_c | var3 << this.field_76687_b | var2;
      int var5 = var4 >> 1;
      int var6 = var4 & 1;
      return var6 == 0 ? this.field_76689_a[var5] & 15 : this.field_76689_a[var5] >> 4 & 15;
   }
}
