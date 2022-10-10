package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.math.MathHelper;

public class RealmsSimpleScrolledSelectionListProxy extends GuiSlot {
   private final RealmsSimpleScrolledSelectionList field_207727_v;

   public RealmsSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.func_71410_x(), var2, var3, var4, var5, var6);
      this.field_207727_v = var1;
   }

   protected int func_148127_b() {
      return this.field_207727_v.getItemCount();
   }

   protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
      return this.field_207727_v.selectItem(var1, var2, var3, var5);
   }

   protected boolean func_148131_a(int var1) {
      return this.field_207727_v.isSelectedItem(var1);
   }

   protected void func_148123_a() {
      this.field_207727_v.renderBackground();
   }

   protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.field_207727_v.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int func_207726_c() {
      return this.field_148155_a;
   }

   protected int func_148138_e() {
      return this.field_207727_v.getMaxPosition();
   }

   protected int func_148137_d() {
      return this.field_207727_v.getScrollbarPosition();
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
         int var8 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
         int var9 = this.field_148153_b + 4 - (int)this.field_148169_q;
         if (this.field_148165_u) {
            this.func_148129_a(var8, var9, var6);
         }

         this.func_192638_a(var8, var9, var1, var2, var3);
         GlStateManager.func_179097_i();
         this.func_148136_c(0, this.field_148153_b, 255, 255);
         this.func_148136_c(this.field_148154_c, this.field_148158_l, 255, 255);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.func_179118_c();
         GlStateManager.func_179103_j(7425);
         GlStateManager.func_179090_x();
         int var10 = this.func_148135_f();
         if (var10 > 0) {
            int var11 = (this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b) / this.func_148138_e();
            var11 = MathHelper.func_76125_a(var11, 32, this.field_148154_c - this.field_148153_b - 8);
            int var12 = (int)this.field_148169_q * (this.field_148154_c - this.field_148153_b - var11) / var10 + this.field_148153_b;
            if (var12 < this.field_148153_b) {
               var12 = this.field_148153_b;
            }

            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)this.field_148154_c, 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)this.field_148154_c, 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)this.field_148153_b, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)this.field_148153_b, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var6.func_78381_a();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)(var12 + var11), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)(var12 + var11), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var5, (double)var12, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)var12, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).func_181675_d();
            var6.func_78381_a();
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var7.func_181662_b((double)var4, (double)(var12 + var11 - 1), 0.0D).func_187315_a(0.0D, 1.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)(var5 - 1), (double)(var12 + var11 - 1), 0.0D).func_187315_a(1.0D, 1.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)(var5 - 1), (double)var12, 0.0D).func_187315_a(1.0D, 0.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var7.func_181662_b((double)var4, (double)var12, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(192, 192, 192, 255).func_181675_d();
            var6.func_78381_a();
         }

         this.func_148142_b(var1, var2);
         GlStateManager.func_179098_w();
         GlStateManager.func_179103_j(7424);
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
      }
   }

   public boolean mouseScrolled(double var1) {
      return this.field_207727_v.mouseScrolled(var1) ? true : super.mouseScrolled(var1);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.field_207727_v.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.field_207727_v.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.field_207727_v.mouseDragged(var1, var3, var5, var6, var8);
   }
}
