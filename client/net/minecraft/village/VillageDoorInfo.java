package net.minecraft.village;

public class VillageDoorInfo {
   public final int field_75481_a;
   public final int field_75479_b;
   public final int field_75480_c;
   public final int field_75477_d;
   public final int field_75478_e;
   public int field_75475_f;
   public boolean field_75476_g;
   private int field_75482_h;

   public VillageDoorInfo(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.field_75481_a = var1;
      this.field_75479_b = var2;
      this.field_75480_c = var3;
      this.field_75477_d = var4;
      this.field_75478_e = var5;
      this.field_75475_f = var6;
   }

   public int func_75474_b(int var1, int var2, int var3) {
      int var4 = var1 - this.field_75481_a;
      int var5 = var2 - this.field_75479_b;
      int var6 = var3 - this.field_75480_c;
      return var4 * var4 + var5 * var5 + var6 * var6;
   }

   public int func_75469_c(int var1, int var2, int var3) {
      int var4 = var1 - this.field_75481_a - this.field_75477_d;
      int var5 = var2 - this.field_75479_b;
      int var6 = var3 - this.field_75480_c - this.field_75478_e;
      return var4 * var4 + var5 * var5 + var6 * var6;
   }

   public int func_75471_a() {
      return this.field_75481_a + this.field_75477_d;
   }

   public int func_75473_b() {
      return this.field_75479_b;
   }

   public int func_75472_c() {
      return this.field_75480_c + this.field_75478_e;
   }

   public boolean func_75467_a(int var1, int var2) {
      int var3 = var1 - this.field_75481_a;
      int var4 = var2 - this.field_75480_c;
      return var3 * this.field_75477_d + var4 * this.field_75478_e >= 0;
   }

   public void func_75466_d() {
      this.field_75482_h = 0;
   }

   public void func_75470_e() {
      ++this.field_75482_h;
   }

   public int func_75468_f() {
      return this.field_75482_h;
   }
}
