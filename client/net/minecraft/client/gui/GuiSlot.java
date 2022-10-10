package net.minecraft.client.gui;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

public abstract class GuiSlot extends GuiEventHandler {
   protected final Minecraft field_148161_k;
   protected int field_148155_a;
   protected int field_148158_l;
   protected int field_148153_b;
   protected int field_148154_c;
   protected int field_148151_d;
   protected int field_148152_e;
   protected final int field_148149_f;
   protected boolean field_148163_i = true;
   protected int field_148157_o = -2;
   protected double field_148169_q;
   protected int field_148168_r;
   protected long field_148167_s = -9223372036854775808L;
   protected boolean field_178041_q = true;
   protected boolean field_148166_t = true;
   protected boolean field_148165_u;
   protected int field_148160_j;
   private boolean field_195084_v;

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

   public void func_193651_b(boolean var1) {
      this.field_148166_t = var1;
   }

   protected void func_148133_a(boolean var1, int var2) {
      this.field_148165_u = var1;
      this.field_148160_j = var2;
      if (!var1) {
         this.field_148160_j = 0;
      }

   }

   public boolean func_195082_l() {
      return this.field_178041_q;
   }

   protected abstract int func_148127_b();

   public void func_195080_b(int var1) {
   }

   protected List<? extends IGuiEventListener> func_195074_b() {
      return Collections.emptyList();
   }

   protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
      return true;
   }

   protected abstract boolean func_148131_a(int var1);

   protected int func_148138_e() {
      return this.func_148127_b() * this.field_148149_f + this.field_148160_j;
   }

   protected abstract void func_148123_a();

   protected void func_192639_a(int var1, int var2, int var3, float var4) {
   }

   protected abstract void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7);

   protected void func_148129_a(int var1, int var2, Tessellator var3) {
   }

   protected void func_148132_a(int var1, int var2) {
   }

   protected void func_148142_b(int var1, int var2) {
   }

   public int func_195083_a(double var1, double var3) {
      int var5 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2;
      int var6 = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
      int var7 = MathHelper.func_76128_c(var3 - (double)this.field_148153_b) - this.field_148160_j + (int)this.field_148169_q - 4;
      int var8 = var7 / this.field_148149_f;
      return var1 < (double)this.func_148137_d() && var1 >= (double)var5 && var1 <= (double)var6 && var8 >= 0 && var7 >= 0 && var8 < this.func_148127_b() ? var8 : -1;
   }

   protected void func_148121_k() {
      this.field_148169_q = MathHelper.func_151237_a(this.field_148169_q, 0.0D, (double)this.func_148135_f());
   }

   public int func_148135_f() {
      return Math.max(0, this.func_148138_e() - (this.field_148154_c - this.field_148153_b - 4));
   }

   public int func_148148_g() {
      return (int)this.field_148169_q;
   }

   public boolean func_195079_b(double var1, double var3) {
      return var3 >= (double)this.field_148153_b && var3 <= (double)this.field_148154_c && var1 >= (double)this.field_148152_e && var1 <= (double)this.field_148151_d;
   }

   public void func_148145_f(int var1) {
      this.field_148169_q += (double)var1;
      this.func_148121_k();
      this.field_148157_o = -2;
   }

   public void func_148128_a(int var1, int var2, float var3) {
      if (this.field_178041_q) {
         this.func_148123_a();
         int var4 = this.func_148137_d();
         int var5 = var4 + 6;
         this.func_148121_k();
         GlStateManager.func_179140_f();
         GlStateManager.func_179106_n();
         Tessellator var6 = Tessellator.func_178181_a();
         BufferBuilder var7 = var6.func_178180_c();
         this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110325_k);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         float var8 = 32.0F;
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148154_c, 0.0D).func_187315_a((double)((float)this.field_148152_e / 32.0F), (double)((float)(this.field_148154_c + (int)this.field_148169_q) / 32.0F)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148154_c, 0.0D).func_187315_a((double)((float)this.field_148151_d / 32.0F), (double)((float)(this.field_148154_c + (int)this.field_148169_q) / 32.0F)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148153_b, 0.0D).func_187315_a((double)((float)this.field_148151_d / 32.0F), (double)((float)(this.field_148153_b + (int)this.field_148169_q) / 32.0F)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148153_b, 0.0D).func_187315_a((double)((float)this.field_148152_e / 32.0F), (double)((float)(this.field_148153_b + (int)this.field_148169_q) / 32.0F)).func_181669_b(32, 32, 32, 255).func_181675_d();
         var6.func_78381_a();
         int var9 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
         int var10 = this.field_148153_b + 4 - (int)this.field_148169_q;
         if (this.field_148165_u) {
            this.func_148129_a(var9, var10, var6);
         }

         this.func_192638_a(var9, var10, var1, var2, var3);
         GlStateManager.func_179097_i();
         this.func_148136_c(0, this.field_148153_b, 255, 255);
         this.func_148136_c(this.field_148154_c, this.field_148158_l, 255, 255);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.func_179118_c();
         GlStateManager.func_179103_j(7425);
         GlStateManager.func_179090_x();
         boolean var11 = true;
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)this.field_148152_e, (double)(this.field_148153_b + 4), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)(this.field_148153_b + 4), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148153_b, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148153_b, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var6.func_78381_a();
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)this.field_148152_e, (double)this.field_148154_c, 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)this.field_148154_c, 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var7.func_181662_b((double)this.field_148151_d, (double)(this.field_148154_c - 4), 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var7.func_181662_b((double)this.field_148152_e, (double)(this.field_148154_c - 4), 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 0).func_181675_d();
         var6.func_78381_a();
         int var12 = this.func_148135_f();
         if (var12 > 0) {
            int var13 = (int)((float)((this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b)) / (float)this.func_148138_e());
            var13 = MathHelper.func_76125_a(var13, 32, this.field_148154_c - this.field_148153_b - 8);
            int var14 = (int)this.field_148169_q * (this.field_148154_c - this.field_148153_b - var13) / var12 + this.field_148153_b;
            if (var14 < this.field_148153_b) {
               var14 = this.field_148153_b;
            }

            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)this.field_148154_c, 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)this.field_148154_c, 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)this.field_148153_b, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)this.field_148153_b, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var6.func_78381_a();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)(var14 + var13), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)(var14 + var13), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)var14, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)var14, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var6.func_78381_a();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)(var14 + var13 - 1), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)(var5 - 1), (double)(var14 + var13 - 1), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)(var5 - 1), (double)var14, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)var14, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var6.func_78381_a();
         }

         this.func_148142_b(var1, var2);
         GlStateManager.func_179098_w();
         GlStateManager.func_179103_j(7424);
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
      }
   }

   protected void func_195077_a(double var1, double var3, int var5) {
      this.field_195084_v = var5 == 0 && var1 >= (double)this.func_148137_d() && var1 < (double)(this.func_148137_d() + 6);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.func_195077_a(var1, var3, var5);
      if (this.func_195082_l() && this.func_195079_b(var1, var3)) {
         int var6 = this.func_195083_a(var1, var3);
         if (var6 == -1 && var5 == 0) {
            this.func_148132_a((int)(var1 - (double)(this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2)), (int)(var3 - (double)this.field_148153_b) + (int)this.field_148169_q - 4);
            return true;
         } else if (var6 != -1 && this.func_195078_a(var6, var5, var1, var3)) {
            if (this.func_195074_b().size() > var6) {
               this.func_195073_a((IGuiEventListener)this.func_195074_b().get(var6));
            }

            this.func_195072_d(true);
            this.func_195080_b(var6);
            return true;
         } else {
            return this.field_195084_v;
         }
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(var1, var3, var5);
      }

      this.func_195074_b().forEach((var5x) -> {
         var5x.mouseReleased(var1, var3, var5);
      });
      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if (this.func_195082_l() && var5 == 0 && this.field_195084_v) {
         if (var3 < (double)this.field_148153_b) {
            this.field_148169_q = 0.0D;
         } else if (var3 > (double)this.field_148154_c) {
            this.field_148169_q = (double)this.func_148135_f();
         } else {
            double var10 = (double)this.func_148135_f();
            if (var10 < 1.0D) {
               var10 = 1.0D;
            }

            int var12 = (int)((float)((this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b)) / (float)this.func_148138_e());
            var12 = MathHelper.func_76125_a(var12, 32, this.field_148154_c - this.field_148153_b - 8);
            double var13 = var10 / (double)(this.field_148154_c - this.field_148153_b - var12);
            if (var13 < 1.0D) {
               var13 = 1.0D;
            }

            this.field_148169_q += var8 * var13;
            this.func_148121_k();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1) {
      if (!this.func_195082_l()) {
         return false;
      } else {
         this.field_148169_q -= var1 * (double)this.field_148149_f / 2.0D;
         return true;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return !this.func_195082_l() ? false : super.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return !this.func_195082_l() ? false : super.charTyped(var1, var2);
   }

   public int func_148139_c() {
      return 220;
   }

   protected void func_192638_a(int var1, int var2, int var3, int var4, float var5) {
      int var6 = this.func_148127_b();
      Tessellator var7 = Tessellator.func_178181_a();
      BufferBuilder var8 = var7.func_178180_c();

      for(int var9 = 0; var9 < var6; ++var9) {
         int var10 = var2 + var9 * this.field_148149_f + this.field_148160_j;
         int var11 = this.field_148149_f - 4;
         if (var10 > this.field_148154_c || var10 + var11 < this.field_148153_b) {
            this.func_192639_a(var9, var1, var10, var5);
         }

         if (this.field_148166_t && this.func_148131_a(var9)) {
            int var12 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2;
            int var13 = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179090_x();
            var8.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var8.func_181662_b((double)var12, (double)(var10 + var11 + 2), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var8.func_181662_b((double)var13, (double)(var10 + var11 + 2), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var8.func_181662_b((double)var13, (double)(var10 - 2), 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var8.func_181662_b((double)var12, (double)(var10 - 2), 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var8.func_181662_b((double)(var12 + 1), (double)(var10 + var11 + 1), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var8.func_181662_b((double)(var13 - 1), (double)(var10 + var11 + 1), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var8.func_181662_b((double)(var13 - 1), (double)(var10 - 1), 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var8.func_181662_b((double)(var12 + 1), (double)(var10 - 1), 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_78381_a();
            GlStateManager.func_179098_w();
         }

         this.func_192637_a(var9, var1, var10, var11, var3, var4, var5);
      }

   }

   protected int func_148137_d() {
      return this.field_148155_a / 2 + 124;
   }

   protected void func_148136_c(int var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.func_178181_a();
      BufferBuilder var6 = var5.func_178180_c();
      this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110325_k);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = 32.0F;
      var6.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var6.func_181662_b((double)this.field_148152_e, (double)var2, 0.0D).func_187315_a(0.0D, (double)((float)var2 / 32.0F)).func_181669_b(64, 64, 64, var4).func_181675_d();
      var6.func_181662_b((double)(this.field_148152_e + this.field_148155_a), (double)var2, 0.0D).func_187315_a((double)((float)this.field_148155_a / 32.0F), (double)((float)var2 / 32.0F)).func_181669_b(64, 64, 64, var4).func_181675_d();
      var6.func_181662_b((double)(this.field_148152_e + this.field_148155_a), (double)var1, 0.0D).func_187315_a((double)((float)this.field_148155_a / 32.0F), (double)((float)var1 / 32.0F)).func_181669_b(64, 64, 64, var3).func_181675_d();
      var6.func_181662_b((double)this.field_148152_e, (double)var1, 0.0D).func_187315_a(0.0D, (double)((float)var1 / 32.0F)).func_181669_b(64, 64, 64, var3).func_181675_d();
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
