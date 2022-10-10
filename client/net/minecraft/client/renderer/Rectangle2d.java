package net.minecraft.client.renderer;

public class Rectangle2d {
   private int field_199320_a;
   private int field_199321_b;
   private int field_199322_c;
   private int field_199323_d;

   public Rectangle2d(int var1, int var2, int var3, int var4) {
      super();
      this.field_199320_a = var1;
      this.field_199321_b = var2;
      this.field_199322_c = var3;
      this.field_199323_d = var4;
   }

   public int func_199318_a() {
      return this.field_199320_a;
   }

   public int func_199319_b() {
      return this.field_199321_b;
   }

   public int func_199316_c() {
      return this.field_199322_c;
   }

   public int func_199317_d() {
      return this.field_199323_d;
   }

   public boolean func_199315_b(int var1, int var2) {
      return var1 >= this.field_199320_a && var1 <= this.field_199320_a + this.field_199322_c && var2 >= this.field_199321_b && var2 <= this.field_199321_b + this.field_199323_d;
   }
}
