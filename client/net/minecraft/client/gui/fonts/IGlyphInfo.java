package net.minecraft.client.gui.fonts;

public interface IGlyphInfo extends IGlyph {
   int func_211202_a();

   int func_211203_b();

   void func_211573_a(int var1, int var2);

   boolean func_211579_f();

   float func_211578_g();

   default float func_211198_f() {
      return this.getBearingX();
   }

   default float func_211199_g() {
      return this.func_211198_f() + (float)this.func_211202_a() / this.func_211578_g();
   }

   default float func_211200_h() {
      return this.getBearingY();
   }

   default float func_211204_i() {
      return this.func_211200_h() + (float)this.func_211203_b() / this.func_211578_g();
   }

   default float getBearingY() {
      return 3.0F;
   }
}
