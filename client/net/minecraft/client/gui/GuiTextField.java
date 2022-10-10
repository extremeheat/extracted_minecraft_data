package net.minecraft.client.gui;

import com.google.common.base.Predicates;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;

public class GuiTextField extends Gui implements IGuiEventListener {
   private final int field_175208_g;
   private final FontRenderer field_146211_a;
   public int field_146209_f;
   public int field_146210_g;
   private final int field_146218_h;
   private final int field_146219_i;
   private String field_146216_j;
   private int field_146217_k;
   private int field_146214_l;
   private boolean field_146215_m;
   private boolean field_146212_n;
   private boolean field_146213_o;
   private boolean field_146226_p;
   private int field_146225_q;
   private int field_146224_r;
   private int field_146223_s;
   private int field_146222_t;
   private int field_146221_u;
   private boolean field_146220_v;
   private String field_195614_x;
   private BiConsumer<Integer, String> field_175210_x;
   private Predicate<String> field_175209_y;
   private BiFunction<String, Integer, String> field_195613_A;

   public GuiTextField(int var1, FontRenderer var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, (GuiTextField)null);
   }

   public GuiTextField(int var1, FontRenderer var2, int var3, int var4, int var5, int var6, @Nullable GuiTextField var7) {
      super();
      this.field_146216_j = "";
      this.field_146217_k = 32;
      this.field_146215_m = true;
      this.field_146212_n = true;
      this.field_146226_p = true;
      this.field_146222_t = 14737632;
      this.field_146221_u = 7368816;
      this.field_146220_v = true;
      this.field_175209_y = Predicates.alwaysTrue();
      this.field_195613_A = (var0, var1x) -> {
         return var0;
      };
      this.field_175208_g = var1;
      this.field_146211_a = var2;
      this.field_146209_f = var3;
      this.field_146210_g = var4;
      this.field_146218_h = var5;
      this.field_146219_i = var6;
      if (var7 != null) {
         this.func_146180_a(var7.func_146179_b());
      }

   }

   public void func_195609_a(BiConsumer<Integer, String> var1) {
      this.field_175210_x = var1;
   }

   public void func_195607_a(BiFunction<String, Integer, String> var1) {
      this.field_195613_A = var1;
   }

   public void func_146178_a() {
      ++this.field_146214_l;
   }

   public void func_146180_a(String var1) {
      if (this.field_175209_y.test(var1)) {
         if (var1.length() > this.field_146217_k) {
            this.field_146216_j = var1.substring(0, this.field_146217_k);
         } else {
            this.field_146216_j = var1;
         }

         this.func_190516_a(this.field_175208_g, var1);
         this.func_146202_e();
      }
   }

   public String func_146179_b() {
      return this.field_146216_j;
   }

   public String func_146207_c() {
      int var1 = this.field_146224_r < this.field_146223_s ? this.field_146224_r : this.field_146223_s;
      int var2 = this.field_146224_r < this.field_146223_s ? this.field_146223_s : this.field_146224_r;
      return this.field_146216_j.substring(var1, var2);
   }

   public void func_200675_a(Predicate<String> var1) {
      this.field_175209_y = var1;
   }

   public void func_146191_b(String var1) {
      String var2 = "";
      String var3 = SharedConstants.func_71565_a(var1);
      int var4 = this.field_146224_r < this.field_146223_s ? this.field_146224_r : this.field_146223_s;
      int var5 = this.field_146224_r < this.field_146223_s ? this.field_146223_s : this.field_146224_r;
      int var6 = this.field_146217_k - this.field_146216_j.length() - (var4 - var5);
      if (!this.field_146216_j.isEmpty()) {
         var2 = var2 + this.field_146216_j.substring(0, var4);
      }

      int var7;
      if (var6 < var3.length()) {
         var2 = var2 + var3.substring(0, var6);
         var7 = var6;
      } else {
         var2 = var2 + var3;
         var7 = var3.length();
      }

      if (!this.field_146216_j.isEmpty() && var5 < this.field_146216_j.length()) {
         var2 = var2 + this.field_146216_j.substring(var5);
      }

      if (this.field_175209_y.test(var2)) {
         this.field_146216_j = var2;
         this.func_146182_d(var4 - this.field_146223_s + var7);
         this.func_190516_a(this.field_175208_g, this.field_146216_j);
      }
   }

   public void func_190516_a(int var1, String var2) {
      if (this.field_175210_x != null) {
         this.field_175210_x.accept(var1, var2);
      }

   }

   public void func_146177_a(int var1) {
      if (!this.field_146216_j.isEmpty()) {
         if (this.field_146223_s != this.field_146224_r) {
            this.func_146191_b("");
         } else {
            this.func_146175_b(this.func_146187_c(var1) - this.field_146224_r);
         }
      }
   }

   public void func_146175_b(int var1) {
      if (!this.field_146216_j.isEmpty()) {
         if (this.field_146223_s != this.field_146224_r) {
            this.func_146191_b("");
         } else {
            boolean var2 = var1 < 0;
            int var3 = var2 ? this.field_146224_r + var1 : this.field_146224_r;
            int var4 = var2 ? this.field_146224_r : this.field_146224_r + var1;
            String var5 = "";
            if (var3 >= 0) {
               var5 = this.field_146216_j.substring(0, var3);
            }

            if (var4 < this.field_146216_j.length()) {
               var5 = var5 + this.field_146216_j.substring(var4);
            }

            if (this.field_175209_y.test(var5)) {
               this.field_146216_j = var5;
               if (var2) {
                  this.func_146182_d(var1);
               }

               this.func_190516_a(this.field_175208_g, this.field_146216_j);
            }
         }
      }
   }

   public int func_146187_c(int var1) {
      return this.func_146183_a(var1, this.func_146198_h());
   }

   public int func_146183_a(int var1, int var2) {
      return this.func_146197_a(var1, var2, true);
   }

   public int func_146197_a(int var1, int var2, boolean var3) {
      int var4 = var2;
      boolean var5 = var1 < 0;
      int var6 = Math.abs(var1);

      for(int var7 = 0; var7 < var6; ++var7) {
         if (!var5) {
            int var8 = this.field_146216_j.length();
            var4 = this.field_146216_j.indexOf(32, var4);
            if (var4 == -1) {
               var4 = var8;
            } else {
               while(var3 && var4 < var8 && this.field_146216_j.charAt(var4) == ' ') {
                  ++var4;
               }
            }
         } else {
            while(var3 && var4 > 0 && this.field_146216_j.charAt(var4 - 1) == ' ') {
               --var4;
            }

            while(var4 > 0 && this.field_146216_j.charAt(var4 - 1) != ' ') {
               --var4;
            }
         }
      }

      return var4;
   }

   public void func_146182_d(int var1) {
      this.func_146190_e(this.field_146223_s + var1);
   }

   public void func_146190_e(int var1) {
      this.func_212422_f(var1);
      this.func_146199_i(this.field_146224_r);
      this.func_190516_a(this.field_175208_g, this.field_146216_j);
   }

   public void func_212422_f(int var1) {
      this.field_146224_r = MathHelper.func_76125_a(var1, 0, this.field_146216_j.length());
   }

   public void func_146196_d() {
      this.func_146190_e(0);
   }

   public void func_146202_e() {
      this.func_146190_e(this.field_146216_j.length());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.func_146176_q() && this.func_146206_l()) {
         if (GuiScreen.func_175278_g(var1)) {
            this.func_146202_e();
            this.func_146199_i(0);
            return true;
         } else if (GuiScreen.func_175280_f(var1)) {
            Minecraft.func_71410_x().field_195559_v.func_197960_a(this.func_146207_c());
            return true;
         } else if (GuiScreen.func_175279_e(var1)) {
            if (this.field_146226_p) {
               this.func_146191_b(Minecraft.func_71410_x().field_195559_v.func_197965_a());
            }

            return true;
         } else if (GuiScreen.func_175277_d(var1)) {
            Minecraft.func_71410_x().field_195559_v.func_197960_a(this.func_146207_c());
            if (this.field_146226_p) {
               this.func_146191_b("");
            }

            return true;
         } else {
            switch(var1) {
            case 259:
               if (GuiScreen.func_146271_m()) {
                  if (this.field_146226_p) {
                     this.func_146177_a(-1);
                  }
               } else if (this.field_146226_p) {
                  this.func_146175_b(-1);
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               return var1 != 256;
            case 261:
               if (GuiScreen.func_146271_m()) {
                  if (this.field_146226_p) {
                     this.func_146177_a(1);
                  }
               } else if (this.field_146226_p) {
                  this.func_146175_b(1);
               }

               return true;
            case 262:
               if (GuiScreen.func_146272_n()) {
                  if (GuiScreen.func_146271_m()) {
                     this.func_146199_i(this.func_146183_a(1, this.func_146186_n()));
                  } else {
                     this.func_146199_i(this.func_146186_n() + 1);
                  }
               } else if (GuiScreen.func_146271_m()) {
                  this.func_146190_e(this.func_146187_c(1));
               } else {
                  this.func_146182_d(1);
               }

               return true;
            case 263:
               if (GuiScreen.func_146272_n()) {
                  if (GuiScreen.func_146271_m()) {
                     this.func_146199_i(this.func_146183_a(-1, this.func_146186_n()));
                  } else {
                     this.func_146199_i(this.func_146186_n() - 1);
                  }
               } else if (GuiScreen.func_146271_m()) {
                  this.func_146190_e(this.func_146187_c(-1));
               } else {
                  this.func_146182_d(-1);
               }

               return true;
            case 268:
               if (GuiScreen.func_146272_n()) {
                  this.func_146199_i(0);
               } else {
                  this.func_146196_d();
               }

               return true;
            case 269:
               if (GuiScreen.func_146272_n()) {
                  this.func_146199_i(this.field_146216_j.length());
               } else {
                  this.func_146202_e();
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (this.func_146176_q() && this.func_146206_l()) {
         if (SharedConstants.func_71566_a(var1)) {
            if (this.field_146226_p) {
               this.func_146191_b(Character.toString(var1));
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.func_146176_q()) {
         return false;
      } else {
         boolean var6 = var1 >= (double)this.field_146209_f && var1 < (double)(this.field_146209_f + this.field_146218_h) && var3 >= (double)this.field_146210_g && var3 < (double)(this.field_146210_g + this.field_146219_i);
         if (this.field_146212_n) {
            this.func_146195_b(var6);
         }

         if (this.field_146213_o && var6 && var5 == 0) {
            int var7 = MathHelper.func_76128_c(var1) - this.field_146209_f;
            if (this.field_146215_m) {
               var7 -= 4;
            }

            String var8 = this.field_146211_a.func_78269_a(this.field_146216_j.substring(this.field_146225_q), this.func_146200_o());
            this.func_146190_e(this.field_146211_a.func_78269_a(var8, var7).length() + this.field_146225_q);
            return true;
         } else {
            return false;
         }
      }
   }

   public void func_195608_a(int var1, int var2, float var3) {
      if (this.func_146176_q()) {
         if (this.func_146181_i()) {
            func_73734_a(this.field_146209_f - 1, this.field_146210_g - 1, this.field_146209_f + this.field_146218_h + 1, this.field_146210_g + this.field_146219_i + 1, -6250336);
            func_73734_a(this.field_146209_f, this.field_146210_g, this.field_146209_f + this.field_146218_h, this.field_146210_g + this.field_146219_i, -16777216);
         }

         int var4 = this.field_146226_p ? this.field_146222_t : this.field_146221_u;
         int var5 = this.field_146224_r - this.field_146225_q;
         int var6 = this.field_146223_s - this.field_146225_q;
         String var7 = this.field_146211_a.func_78269_a(this.field_146216_j.substring(this.field_146225_q), this.func_146200_o());
         boolean var8 = var5 >= 0 && var5 <= var7.length();
         boolean var9 = this.field_146213_o && this.field_146214_l / 6 % 2 == 0 && var8;
         int var10 = this.field_146215_m ? this.field_146209_f + 4 : this.field_146209_f;
         int var11 = this.field_146215_m ? this.field_146210_g + (this.field_146219_i - 8) / 2 : this.field_146210_g;
         int var12 = var10;
         if (var6 > var7.length()) {
            var6 = var7.length();
         }

         if (!var7.isEmpty()) {
            String var13 = var8 ? var7.substring(0, var5) : var7;
            var12 = this.field_146211_a.func_175063_a((String)this.field_195613_A.apply(var13, this.field_146225_q), (float)var10, (float)var11, var4);
         }

         boolean var16 = this.field_146224_r < this.field_146216_j.length() || this.field_146216_j.length() >= this.func_146208_g();
         int var14 = var12;
         if (!var8) {
            var14 = var5 > 0 ? var10 + this.field_146218_h : var10;
         } else if (var16) {
            var14 = var12 - 1;
            --var12;
         }

         if (!var7.isEmpty() && var8 && var5 < var7.length()) {
            var12 = this.field_146211_a.func_175063_a((String)this.field_195613_A.apply(var7.substring(var5), this.field_146224_r), (float)var12, (float)var11, var4);
         }

         if (!var16 && this.field_195614_x != null) {
            this.field_146211_a.func_175063_a(this.field_195614_x, (float)(var14 - 1), (float)var11, -8355712);
         }

         if (var9) {
            if (var16) {
               Gui.func_73734_a(var14, var11 - 1, var14 + 1, var11 + 1 + this.field_146211_a.field_78288_b, -3092272);
            } else {
               this.field_146211_a.func_175063_a("_", (float)var14, (float)var11, var4);
            }
         }

         if (var6 != var5) {
            int var15 = var10 + this.field_146211_a.func_78256_a(var7.substring(0, var6));
            this.func_146188_c(var14, var11 - 1, var15 - 1, var11 + 1 + this.field_146211_a.field_78288_b);
         }

      }
   }

   private void func_146188_c(int var1, int var2, int var3, int var4) {
      int var5;
      if (var1 < var3) {
         var5 = var1;
         var1 = var3;
         var3 = var5;
      }

      if (var2 < var4) {
         var5 = var2;
         var2 = var4;
         var4 = var5;
      }

      if (var3 > this.field_146209_f + this.field_146218_h) {
         var3 = this.field_146209_f + this.field_146218_h;
      }

      if (var1 > this.field_146209_f + this.field_146218_h) {
         var1 = this.field_146209_f + this.field_146218_h;
      }

      Tessellator var7 = Tessellator.func_178181_a();
      BufferBuilder var6 = var7.func_178180_c();
      GlStateManager.func_179131_c(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.func_179090_x();
      GlStateManager.func_179115_u();
      GlStateManager.func_187422_a(GlStateManager.LogicOp.OR_REVERSE);
      var6.func_181668_a(7, DefaultVertexFormats.field_181705_e);
      var6.func_181662_b((double)var1, (double)var4, 0.0D).func_181675_d();
      var6.func_181662_b((double)var3, (double)var4, 0.0D).func_181675_d();
      var6.func_181662_b((double)var3, (double)var2, 0.0D).func_181675_d();
      var6.func_181662_b((double)var1, (double)var2, 0.0D).func_181675_d();
      var7.func_78381_a();
      GlStateManager.func_179134_v();
      GlStateManager.func_179098_w();
   }

   public void func_146203_f(int var1) {
      this.field_146217_k = var1;
      if (this.field_146216_j.length() > var1) {
         this.field_146216_j = this.field_146216_j.substring(0, var1);
         this.func_190516_a(this.field_175208_g, this.field_146216_j);
      }

   }

   public int func_146208_g() {
      return this.field_146217_k;
   }

   public int func_146198_h() {
      return this.field_146224_r;
   }

   public boolean func_146181_i() {
      return this.field_146215_m;
   }

   public void func_146185_a(boolean var1) {
      this.field_146215_m = var1;
   }

   public void func_146193_g(int var1) {
      this.field_146222_t = var1;
   }

   public void func_146204_h(int var1) {
      this.field_146221_u = var1;
   }

   public void func_205700_b(boolean var1) {
      this.func_146195_b(var1);
   }

   public boolean func_207704_ae_() {
      return true;
   }

   public void func_146195_b(boolean var1) {
      if (var1 && !this.field_146213_o) {
         this.field_146214_l = 0;
      }

      this.field_146213_o = var1;
   }

   public boolean func_146206_l() {
      return this.field_146213_o;
   }

   public void func_146184_c(boolean var1) {
      this.field_146226_p = var1;
   }

   public int func_146186_n() {
      return this.field_146223_s;
   }

   public int func_146200_o() {
      return this.func_146181_i() ? this.field_146218_h - 8 : this.field_146218_h;
   }

   public void func_146199_i(int var1) {
      int var2 = this.field_146216_j.length();
      if (var1 > var2) {
         var1 = var2;
      }

      if (var1 < 0) {
         var1 = 0;
      }

      this.field_146223_s = var1;
      if (this.field_146211_a != null) {
         if (this.field_146225_q > var2) {
            this.field_146225_q = var2;
         }

         int var3 = this.func_146200_o();
         String var4 = this.field_146211_a.func_78269_a(this.field_146216_j.substring(this.field_146225_q), var3);
         int var5 = var4.length() + this.field_146225_q;
         if (var1 == this.field_146225_q) {
            this.field_146225_q -= this.field_146211_a.func_78262_a(this.field_146216_j, var3, true).length();
         }

         if (var1 > var5) {
            this.field_146225_q += var1 - var5;
         } else if (var1 <= this.field_146225_q) {
            this.field_146225_q -= this.field_146225_q - var1;
         }

         this.field_146225_q = MathHelper.func_76125_a(this.field_146225_q, 0, var2);
      }

   }

   public void func_146205_d(boolean var1) {
      this.field_146212_n = var1;
   }

   public boolean func_146176_q() {
      return this.field_146220_v;
   }

   public void func_146189_e(boolean var1) {
      this.field_146220_v = var1;
   }

   public void func_195612_c(@Nullable String var1) {
      this.field_195614_x = var1;
   }

   public int func_195611_j(int var1) {
      return var1 > this.field_146216_j.length() ? this.field_146209_f : this.field_146209_f + this.field_146211_a.func_78256_a(this.field_146216_j.substring(0, var1));
   }
}
