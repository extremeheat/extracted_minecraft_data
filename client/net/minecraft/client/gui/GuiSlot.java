package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot {
   private final Minecraft field_148161_k;
   protected int field_148155_a;
   private int field_148158_l;
   protected int field_148153_b;
   protected int field_148154_c;
   protected int field_148151_d;
   protected int field_148152_e;
   protected final int field_148149_f;
   private int field_148159_m;
   private int field_148156_n;
   protected int field_148150_g;
   protected int field_148162_h;
   protected boolean field_148163_i = true;
   private float field_148157_o = -2.0F;
   private float field_148170_p;
   private float field_148169_q;
   private int field_148168_r = -1;
   private long field_148167_s;
   private boolean field_148166_t = true;
   private boolean field_148165_u;
   protected int field_148160_j;
   private boolean field_148164_v = true;

   public GuiSlot(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.field_148161_k = var1;
      this.field_148155_a = var2;
      this.field_148158_l = var3;
      this.field_148153_b = var4;
      this.field_148154_c = var5;
      this.field_148149_f = var6;
      this.field_148152_e = 0;
      this.field_148151_d = var2;
   }

   public void func_148122_a(int var1, int var2, int var3, int var4) {
      this.field_148155_a = var1;
      this.field_148158_l = var2;
      this.field_148153_b = var3;
      this.field_148154_c = var4;
      this.field_148152_e = 0;
      this.field_148151_d = var1;
   }

   public void func_148130_a(boolean var1) {
      this.field_148166_t = var1;
   }

   protected void func_148133_a(boolean var1, int var2) {
      this.field_148165_u = var1;
      this.field_148160_j = var2;
      if (!var1) {
         this.field_148160_j = 0;
      }
   }

   protected abstract int func_148127_b();

   protected abstract void func_148144_a(int var1, boolean var2, int var3, int var4);

   protected abstract boolean func_148131_a(int var1);

   protected int func_148138_e() {
      return this.func_148127_b() * this.field_148149_f + this.field_148160_j;
   }

   protected abstract void func_148123_a();

   protected abstract void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7);

   protected void func_148129_a(int var1, int var2, Tessellator var3) {
   }

   protected void func_148132_a(int var1, int var2) {
   }

   protected void func_148142_b(int var1, int var2) {
   }

   public int func_148124_c(int var1, int var2) {
      int var3 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2;
      int var4 = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
      int var5 = var2 - this.field_148153_b - this.field_148160_j + (int)this.field_148169_q - 4;
      int var6 = var5 / this.field_148149_f;
      return var1 < this.func_148137_d() && var1 >= var3 && var1 <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.func_148127_b() ? var6 : -1;
   }

   public void func_148134_d(int var1, int var2) {
      this.field_148159_m = var1;
      this.field_148156_n = var2;
   }

   private void func_148121_k() {
      int var1 = this.func_148135_f();
      if (var1 < 0) {
         var1 /= 2;
      }

      if (!this.field_148163_i && var1 < 0) {
         var1 = 0;
      }

      if (this.field_148169_q < 0.0F) {
         this.field_148169_q = 0.0F;
      }

      if (this.field_148169_q > (float)var1) {
         this.field_148169_q = (float)var1;
      }
   }

   public int func_148135_f() {
      return this.func_148138_e() - (this.field_148154_c - this.field_148153_b - 4);
   }

   public int func_148148_g() {
      return (int)this.field_148169_q;
   }

   public boolean func_148141_e(int var1) {
      return var1 >= this.field_148153_b && var1 <= this.field_148154_c;
   }

   public void func_148145_f(int var1) {
      this.field_148169_q += (float)var1;
      this.func_148121_k();
      this.field_148157_o = -2.0F;
   }

   public void func_148147_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == this.field_148159_m) {
            this.field_148169_q -= (float)(this.field_148149_f * 2 / 3);
            this.field_148157_o = -2.0F;
            this.func_148121_k();
         } else if (var1.field_146127_k == this.field_148156_n) {
            this.field_148169_q += (float)(this.field_148149_f * 2 / 3);
            this.field_148157_o = -2.0F;
            this.func_148121_k();
         }
      }
   }

   public void func_148128_a(int var1, int var2, float var3) {
      this.field_148150_g = var1;
      this.field_148162_h = var2;
      this.func_148123_a();
      int var4 = this.func_148127_b();
      int var5 = this.func_148137_d();
      int var6 = var5 + 6;
      if (var1 > this.field_148152_e && var1 < this.field_148151_d && var2 > this.field_148153_b && var2 < this.field_148154_c) {
         if (!Mouse.isButtonDown(0) || !this.func_148125_i()) {
            for(; !this.field_148161_k.field_71474_y.field_85185_A && Mouse.next(); this.field_148161_k.field_71462_r.func_146274_d()) {
               int var15 = Mouse.getEventDWheel();
               if (var15 != 0) {
                  if (var15 > 0) {
                     var15 = -1;
                  } else if (var15 < 0) {
                     var15 = 1;
                  }

                  this.field_148169_q += (float)(var15 * this.field_148149_f / 2);
               }
            }

            this.field_148157_o = -1.0F;
         } else if (this.field_148157_o == -1.0F) {
            boolean var7 = true;
            if (var2 >= this.field_148153_b && var2 <= this.field_148154_c) {
               int var8 = this.field_148155_a / 2 - this.func_148139_c() / 2;
               int var9 = this.field_148155_a / 2 + this.func_148139_c() / 2;
               int var10 = var2 - this.field_148153_b - this.field_148160_j + (int)this.field_148169_q - 4;
               int var11 = var10 / this.field_148149_f;
               if (var1 >= var8 && var1 <= var9 && var11 >= 0 && var10 >= 0 && var11 < var4) {
                  boolean var12 = var11 == this.field_148168_r && Minecraft.func_71386_F() - this.field_148167_s < 250L;
                  this.func_148144_a(var11, var12, var1, var2);
                  this.field_148168_r = var11;
                  this.field_148167_s = Minecraft.func_71386_F();
               } else if (var1 >= var8 && var1 <= var9 && var10 < 0) {
                  this.func_148132_a(var1 - var8, var2 - this.field_148153_b + (int)this.field_148169_q - 4);
                  var7 = false;
               }

               if (var1 >= var5 && var1 <= var6) {
                  this.field_148170_p = -1.0F;
                  int var21 = this.func_148135_f();
                  if (var21 < 1) {
                     var21 = 1;
                  }

                  int var13 = (int)(
                     (float)((this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b)) / (float)this.func_148138_e()
                  );
                  if (var13 < 32) {
                     var13 = 32;
                  }

                  if (var13 > this.field_148154_c - this.field_148153_b - 8) {
                     var13 = this.field_148154_c - this.field_148153_b - 8;
                  }

                  this.field_148170_p /= (float)(this.field_148154_c - this.field_148153_b - var13) / (float)var21;
               } else {
                  this.field_148170_p = 1.0F;
               }

               if (var7) {
                  this.field_148157_o = (float)var2;
               } else {
                  this.field_148157_o = -2.0F;
               }
            } else {
               this.field_148157_o = -2.0F;
            }
         } else if (this.field_148157_o >= 0.0F) {
            this.field_148169_q -= ((float)var2 - this.field_148157_o) * this.field_148170_p;
            this.field_148157_o = (float)var2;
         }
      }

      this.func_148121_k();
      GL11.glDisable(2896);
      GL11.glDisable(2912);
      Tessellator var16 = Tessellator.field_78398_a;
      this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110325_k);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var17 = 32.0F;
      var16.func_78382_b();
      var16.func_78378_d(2105376);
      var16.func_78374_a(
         (double)this.field_148152_e,
         (double)this.field_148154_c,
         0.0,
         (double)((float)this.field_148152_e / var17),
         (double)((float)(this.field_148154_c + (int)this.field_148169_q) / var17)
      );
      var16.func_78374_a(
         (double)this.field_148151_d,
         (double)this.field_148154_c,
         0.0,
         (double)((float)this.field_148151_d / var17),
         (double)((float)(this.field_148154_c + (int)this.field_148169_q) / var17)
      );
      var16.func_78374_a(
         (double)this.field_148151_d,
         (double)this.field_148153_b,
         0.0,
         (double)((float)this.field_148151_d / var17),
         (double)((float)(this.field_148153_b + (int)this.field_148169_q) / var17)
      );
      var16.func_78374_a(
         (double)this.field_148152_e,
         (double)this.field_148153_b,
         0.0,
         (double)((float)this.field_148152_e / var17),
         (double)((float)(this.field_148153_b + (int)this.field_148169_q) / var17)
      );
      var16.func_78381_a();
      int var18 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
      int var19 = this.field_148153_b + 4 - (int)this.field_148169_q;
      if (this.field_148165_u) {
         this.func_148129_a(var18, var19, var16);
      }

      this.func_148120_b(var18, var19, var1, var2);
      GL11.glDisable(2929);
      byte var20 = 4;
      this.func_148136_c(0, this.field_148153_b, 255, 255);
      this.func_148136_c(this.field_148154_c, this.field_148158_l, 255, 255);
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 0, 1);
      GL11.glDisable(3008);
      GL11.glShadeModel(7425);
      GL11.glDisable(3553);
      var16.func_78382_b();
      var16.func_78384_a(0, 0);
      var16.func_78374_a((double)this.field_148152_e, (double)(this.field_148153_b + var20), 0.0, 0.0, 1.0);
      var16.func_78374_a((double)this.field_148151_d, (double)(this.field_148153_b + var20), 0.0, 1.0, 1.0);
      var16.func_78384_a(0, 255);
      var16.func_78374_a((double)this.field_148151_d, (double)this.field_148153_b, 0.0, 1.0, 0.0);
      var16.func_78374_a((double)this.field_148152_e, (double)this.field_148153_b, 0.0, 0.0, 0.0);
      var16.func_78381_a();
      var16.func_78382_b();
      var16.func_78384_a(0, 255);
      var16.func_78374_a((double)this.field_148152_e, (double)this.field_148154_c, 0.0, 0.0, 1.0);
      var16.func_78374_a((double)this.field_148151_d, (double)this.field_148154_c, 0.0, 1.0, 1.0);
      var16.func_78384_a(0, 0);
      var16.func_78374_a((double)this.field_148151_d, (double)(this.field_148154_c - var20), 0.0, 1.0, 0.0);
      var16.func_78374_a((double)this.field_148152_e, (double)(this.field_148154_c - var20), 0.0, 0.0, 0.0);
      var16.func_78381_a();
      int var22 = this.func_148135_f();
      if (var22 > 0) {
         int var23 = (this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b) / this.func_148138_e();
         if (var23 < 32) {
            var23 = 32;
         }

         if (var23 > this.field_148154_c - this.field_148153_b - 8) {
            var23 = this.field_148154_c - this.field_148153_b - 8;
         }

         int var14 = (int)this.field_148169_q * (this.field_148154_c - this.field_148153_b - var23) / var22 + this.field_148153_b;
         if (var14 < this.field_148153_b) {
            var14 = this.field_148153_b;
         }

         var16.func_78382_b();
         var16.func_78384_a(0, 255);
         var16.func_78374_a((double)var5, (double)this.field_148154_c, 0.0, 0.0, 1.0);
         var16.func_78374_a((double)var6, (double)this.field_148154_c, 0.0, 1.0, 1.0);
         var16.func_78374_a((double)var6, (double)this.field_148153_b, 0.0, 1.0, 0.0);
         var16.func_78374_a((double)var5, (double)this.field_148153_b, 0.0, 0.0, 0.0);
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78384_a(8421504, 255);
         var16.func_78374_a((double)var5, (double)(var14 + var23), 0.0, 0.0, 1.0);
         var16.func_78374_a((double)var6, (double)(var14 + var23), 0.0, 1.0, 1.0);
         var16.func_78374_a((double)var6, (double)var14, 0.0, 1.0, 0.0);
         var16.func_78374_a((double)var5, (double)var14, 0.0, 0.0, 0.0);
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78384_a(12632256, 255);
         var16.func_78374_a((double)var5, (double)(var14 + var23 - 1), 0.0, 0.0, 1.0);
         var16.func_78374_a((double)(var6 - 1), (double)(var14 + var23 - 1), 0.0, 1.0, 1.0);
         var16.func_78374_a((double)(var6 - 1), (double)var14, 0.0, 1.0, 0.0);
         var16.func_78374_a((double)var5, (double)var14, 0.0, 0.0, 0.0);
         var16.func_78381_a();
      }

      this.func_148142_b(var1, var2);
      GL11.glEnable(3553);
      GL11.glShadeModel(7424);
      GL11.glEnable(3008);
      GL11.glDisable(3042);
   }

   public void func_148143_b(boolean var1) {
      this.field_148164_v = var1;
   }

   public boolean func_148125_i() {
      return this.field_148164_v;
   }

   public int func_148139_c() {
      return 220;
   }

   protected void func_148120_b(int var1, int var2, int var3, int var4) {
      int var5 = this.func_148127_b();
      Tessellator var6 = Tessellator.field_78398_a;

      for(int var7 = 0; var7 < var5; ++var7) {
         int var8 = var2 + var7 * this.field_148149_f + this.field_148160_j;
         int var9 = this.field_148149_f - 4;
         if (var8 <= this.field_148154_c && var8 + var9 >= this.field_148153_b) {
            if (this.field_148166_t && this.func_148131_a(var7)) {
               int var10 = this.field_148152_e + (this.field_148155_a / 2 - this.func_148139_c() / 2);
               int var11 = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glDisable(3553);
               var6.func_78382_b();
               var6.func_78378_d(8421504);
               var6.func_78374_a((double)var10, (double)(var8 + var9 + 2), 0.0, 0.0, 1.0);
               var6.func_78374_a((double)var11, (double)(var8 + var9 + 2), 0.0, 1.0, 1.0);
               var6.func_78374_a((double)var11, (double)(var8 - 2), 0.0, 1.0, 0.0);
               var6.func_78374_a((double)var10, (double)(var8 - 2), 0.0, 0.0, 0.0);
               var6.func_78378_d(0);
               var6.func_78374_a((double)(var10 + 1), (double)(var8 + var9 + 1), 0.0, 0.0, 1.0);
               var6.func_78374_a((double)(var11 - 1), (double)(var8 + var9 + 1), 0.0, 1.0, 1.0);
               var6.func_78374_a((double)(var11 - 1), (double)(var8 - 1), 0.0, 1.0, 0.0);
               var6.func_78374_a((double)(var10 + 1), (double)(var8 - 1), 0.0, 0.0, 0.0);
               var6.func_78381_a();
               GL11.glEnable(3553);
            }

            this.func_148126_a(var7, var1, var8, var9, var6, var3, var4);
         }
      }
   }

   protected int func_148137_d() {
      return this.field_148155_a / 2 + 124;
   }

   private void func_148136_c(int var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110325_k);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var6 = 32.0F;
      var5.func_78382_b();
      var5.func_78384_a(4210752, var4);
      var5.func_78374_a((double)this.field_148152_e, (double)var2, 0.0, 0.0, (double)((float)var2 / var6));
      var5.func_78374_a(
         (double)(this.field_148152_e + this.field_148155_a), (double)var2, 0.0, (double)((float)this.field_148155_a / var6), (double)((float)var2 / var6)
      );
      var5.func_78384_a(4210752, var3);
      var5.func_78374_a(
         (double)(this.field_148152_e + this.field_148155_a), (double)var1, 0.0, (double)((float)this.field_148155_a / var6), (double)((float)var1 / var6)
      );
      var5.func_78374_a((double)this.field_148152_e, (double)var1, 0.0, 0.0, (double)((float)var1 / var6));
      var5.func_78381_a();
   }

   public void func_148140_g(int var1) {
      this.field_148152_e = var1;
      this.field_148151_d = var1 + this.field_148155_a;
   }

   public int func_148146_j() {
      return this.field_148149_f;
   }
}
