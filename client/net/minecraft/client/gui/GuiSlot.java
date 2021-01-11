package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class GuiSlot {
   protected final Minecraft field_148161_k;
   protected int field_148155_a;
   protected int field_148158_l;
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
   protected int field_148157_o = -2;
   protected float field_148170_p;
   protected float field_148169_q;
   protected int field_148168_r = -1;
   protected long field_148167_s;
   protected boolean field_178041_q = true;
   protected boolean field_148166_t = true;
   protected boolean field_148165_u;
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

   protected void func_178040_a(int var1, int var2, int var3) {
   }

   protected abstract void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6);

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

   protected void func_148121_k() {
      this.field_148169_q = MathHelper.func_76131_a(this.field_148169_q, 0.0F, (float)this.func_148135_f());
   }

   public int func_148135_f() {
      return Math.max(0, this.func_148138_e() - (this.field_148154_c - this.field_148153_b - 4));
   }

   public int func_148148_g() {
      return (int)this.field_148169_q;
   }

   public boolean func_148141_e(int var1) {
      return var1 >= this.field_148153_b && var1 <= this.field_148154_c && this.field_148150_g >= this.field_148152_e && this.field_148150_g <= this.field_148151_d;
   }

   public void func_148145_f(int var1) {
      this.field_148169_q += (float)var1;
      this.func_148121_k();
      this.field_148157_o = -2;
   }

   public void func_148147_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == this.field_148159_m) {
            this.field_148169_q -= (float)(this.field_148149_f * 2 / 3);
            this.field_148157_o = -2;
            this.func_148121_k();
         } else if (var1.field_146127_k == this.field_148156_n) {
            this.field_148169_q += (float)(this.field_148149_f * 2 / 3);
            this.field_148157_o = -2;
            this.func_148121_k();
         }

      }
   }

   public void func_148128_a(int var1, int var2, float var3) {
      if (this.field_178041_q) {
         this.field_148150_g = var1;
         this.field_148162_h = var2;
         this.func_148123_a();
         int var4 = this.func_148137_d();
         int var5 = var4 + 6;
         this.func_148121_k();
         GlStateManager.func_179140_f();
         GlStateManager.func_179106_n();
         Tessellator var6 = Tessellator.func_178181_a();
         WorldRenderer var7 = var6.func_178180_c();
         this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110325_k);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         float var8 = 32.0F;
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148154_c, 0.0D).func_181673_a((double)((float)this.field_148152_e / var8), (double)((float)(this.field_148154_c + (int)this.field_148169_q) / var8)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148154_c, 0.0D).func_181673_a((double)((float)this.field_148151_d / var8), (double)((float)(this.field_148154_c + (int)this.field_148169_q) / var8)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148153_b, 0.0D).func_181673_a((double)((float)this.field_148151_d / var8), (double)((float)(this.field_148153_b + (int)this.field_148169_q) / var8)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148153_b, 0.0D).func_181673_a((double)((float)this.field_148152_e / var8), (double)((float)(this.field_148153_b + (int)this.field_148169_q) / var8)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var6.func_78381_a();
         int var9 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
         int var10 = this.field_148153_b + 4 - (int)this.field_148169_q;
         if (this.field_148165_u) {
            this.func_148129_a(var9, var10, var6);
         }

         this.func_148120_b(var9, var10, var1, var2);
         GlStateManager.func_179097_i();
         byte var11 = 4;
         this.func_148136_c(0, this.field_148153_b, 255, 255);
         this.func_148136_c(this.field_148154_c, this.field_148158_l, 255, 255);
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         GlStateManager.func_179118_c();
         GlStateManager.func_179103_j(7425);
         GlStateManager.func_179090_x();
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)this.field_148152_e, (double)(this.field_148153_b + var11), 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)(this.field_148153_b + var11), 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148153_b, 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148153_b, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var6.func_78381_a();
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148154_c, 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148154_c, 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)(this.field_148154_c - var11), 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var7.func_181662_b((double)this.field_148152_e, (double)(this.field_148154_c - var11), 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var6.func_78381_a();
         int var12 = this.func_148135_f();
         if (var12 > 0) {
            int var13 = (this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b) / this.func_148138_e();
            var13 = MathHelper.func_76125_a(var13, 32, this.field_148154_c - this.field_148153_b - 8);
            int var14 = (int)this.field_148169_q * (this.field_148154_c - this.field_148153_b - var13) / var12 + this.field_148153_b;
            if (var14 < this.field_148153_b) {
               var14 = this.field_148153_b;
            }

            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)this.field_148154_c, 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)this.field_148154_c, 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)this.field_148153_b, 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)this.field_148153_b, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var6.func_78381_a();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)(var14 + var13), 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)(var14 + var13), 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)var14, 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)var14, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var6.func_78381_a();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)(var14 + var13 - 1), 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)(var5 - 1), (double)(var14 + var13 - 1), 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)(var5 - 1), (double)var14, 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)var14, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var6.func_78381_a();
         }

         this.func_148142_b(var1, var2);
         GlStateManager.func_179098_w();
         GlStateManager.func_179103_j(7424);
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
      }
   }

   public void func_178039_p() {
      if (this.func_148141_e(this.field_148162_h)) {
         int var1;
         int var2;
         int var3;
         int var4;
         if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.field_148162_h >= this.field_148153_b && this.field_148162_h <= this.field_148154_c) {
            var1 = (this.field_148155_a - this.func_148139_c()) / 2;
            var2 = (this.field_148155_a + this.func_148139_c()) / 2;
            var3 = this.field_148162_h - this.field_148153_b - this.field_148160_j + (int)this.field_148169_q - 4;
            var4 = var3 / this.field_148149_f;
            if (var4 < this.func_148127_b() && this.field_148150_g >= var1 && this.field_148150_g <= var2 && var4 >= 0 && var3 >= 0) {
               this.func_148144_a(var4, false, this.field_148150_g, this.field_148162_h);
               this.field_148168_r = var4;
            } else if (this.field_148150_g >= var1 && this.field_148150_g <= var2 && var3 < 0) {
               this.func_148132_a(this.field_148150_g - var1, this.field_148162_h - this.field_148153_b + (int)this.field_148169_q - 4);
            }
         }

         if (Mouse.isButtonDown(0) && this.func_148125_i()) {
            if (this.field_148157_o == -1) {
               boolean var10 = true;
               if (this.field_148162_h >= this.field_148153_b && this.field_148162_h <= this.field_148154_c) {
                  var2 = (this.field_148155_a - this.func_148139_c()) / 2;
                  var3 = (this.field_148155_a + this.func_148139_c()) / 2;
                  var4 = this.field_148162_h - this.field_148153_b - this.field_148160_j + (int)this.field_148169_q - 4;
                  int var5 = var4 / this.field_148149_f;
                  if (var5 < this.func_148127_b() && this.field_148150_g >= var2 && this.field_148150_g <= var3 && var5 >= 0 && var4 >= 0) {
                     boolean var6 = var5 == this.field_148168_r && Minecraft.func_71386_F() - this.field_148167_s < 250L;
                     this.func_148144_a(var5, var6, this.field_148150_g, this.field_148162_h);
                     this.field_148168_r = var5;
                     this.field_148167_s = Minecraft.func_71386_F();
                  } else if (this.field_148150_g >= var2 && this.field_148150_g <= var3 && var4 < 0) {
                     this.func_148132_a(this.field_148150_g - var2, this.field_148162_h - this.field_148153_b + (int)this.field_148169_q - 4);
                     var10 = false;
                  }

                  int var11 = this.func_148137_d();
                  int var7 = var11 + 6;
                  if (this.field_148150_g >= var11 && this.field_148150_g <= var7) {
                     this.field_148170_p = -1.0F;
                     int var8 = this.func_148135_f();
                     if (var8 < 1) {
                        var8 = 1;
                     }

                     int var9 = (int)((float)((this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b)) / (float)this.func_148138_e());
                     var9 = MathHelper.func_76125_a(var9, 32, this.field_148154_c - this.field_148153_b - 8);
                     this.field_148170_p /= (float)(this.field_148154_c - this.field_148153_b - var9) / (float)var8;
                  } else {
                     this.field_148170_p = 1.0F;
                  }

                  if (var10) {
                     this.field_148157_o = this.field_148162_h;
                  } else {
                     this.field_148157_o = -2;
                  }
               } else {
                  this.field_148157_o = -2;
               }
            } else if (this.field_148157_o >= 0) {
               this.field_148169_q -= (float)(this.field_148162_h - this.field_148157_o) * this.field_148170_p;
               this.field_148157_o = this.field_148162_h;
            }
         } else {
            this.field_148157_o = -1;
         }

         var1 = Mouse.getEventDWheel();
         if (var1 != 0) {
            if (var1 > 0) {
               var1 = -1;
            } else if (var1 < 0) {
               var1 = 1;
            }

            this.field_148169_q += (float)(var1 * this.field_148149_f / 2);
         }

      }
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
      Tessellator var6 = Tessellator.func_178181_a();
      WorldRenderer var7 = var6.func_178180_c();

      for(int var8 = 0; var8 < var5; ++var8) {
         int var9 = var2 + var8 * this.field_148149_f + this.field_148160_j;
         int var10 = this.field_148149_f - 4;
         if (var9 > this.field_148154_c || var9 + var10 < this.field_148153_b) {
            this.func_178040_a(var8, var1, var9);
         }

         if (this.field_148166_t && this.func_148131_a(var8)) {
            int var11 = this.field_148152_e + (this.field_148155_a / 2 - this.func_148139_c() / 2);
            int var12 = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179090_x();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var11, (double)(var9 + var10 + 2), 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var12, (double)(var9 + var10 + 2), 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var12, (double)(var9 - 2), 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var11, (double)(var9 - 2), 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)(var11 + 1), (double)(var9 + var10 + 1), 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)(var12 - 1), (double)(var9 + var10 + 1), 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)(var12 - 1), (double)(var9 - 1), 0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)(var11 + 1), (double)(var9 - 1), 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var6.func_78381_a();
            GlStateManager.func_179098_w();
         }

         this.func_180791_a(var8, var1, var9, var10, var3, var4);
      }

   }

   protected int func_148137_d() {
      return this.field_148155_a / 2 + 124;
   }

   protected void func_148136_c(int var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.func_178181_a();
      WorldRenderer var6 = var5.func_178180_c();
      this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110325_k);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = 32.0F;
      var6.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var6.func_181662_b((double)this.field_148152_e, (double)var2, 0.0D).func_181673_a(0.0D, (double)((float)var2 / 32.0F)).func_181669_b(64, 64, 64, var4).func_181675_d();
      var6.func_181662_b((double)(this.field_148152_e + this.field_148155_a), (double)var2, 0.0D).func_181673_a((double)((float)this.field_148155_a / 32.0F), (double)((float)var2 / 32.0F)).func_181669_b(64, 64, 64, var4).func_181675_d();
      var6.func_181662_b((double)(this.field_148152_e + this.field_148155_a), (double)var1, 0.0D).func_181673_a((double)((float)this.field_148155_a / 32.0F), (double)((float)var1 / 32.0F)).func_181669_b(64, 64, 64, var3).func_181675_d();
      var6.func_181662_b((double)this.field_148152_e, (double)var1, 0.0D).func_181673_a(0.0D, (double)((float)var1 / 32.0F)).func_181669_b(64, 64, 64, var3).func_181675_d();
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
