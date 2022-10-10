package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class GuiSpectator extends Gui implements ISpectatorMenuRecipient {
   private static final ResourceLocation field_175267_f = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation field_175269_a = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft field_175268_g;
   private long field_175270_h;
   private SpectatorMenu field_175271_i;

   public GuiSpectator(Minecraft var1) {
      super();
      this.field_175268_g = var1;
   }

   public void func_175260_a(int var1) {
      this.field_175270_h = Util.func_211177_b();
      if (this.field_175271_i != null) {
         this.field_175271_i.func_178644_b(var1);
      } else {
         this.field_175271_i = new SpectatorMenu(this);
      }

   }

   private float func_175265_c() {
      long var1 = this.field_175270_h - Util.func_211177_b() + 5000L;
      return MathHelper.func_76131_a((float)var1 / 2000.0F, 0.0F, 1.0F);
   }

   public void func_195622_a(float var1) {
      if (this.field_175271_i != null) {
         float var2 = this.func_175265_c();
         if (var2 <= 0.0F) {
            this.field_175271_i.func_178641_d();
         } else {
            int var3 = this.field_175268_g.field_195558_d.func_198107_o() / 2;
            float var4 = this.field_73735_i;
            this.field_73735_i = -90.0F;
            float var5 = (float)this.field_175268_g.field_195558_d.func_198087_p() - 22.0F * var2;
            SpectatorDetails var6 = this.field_175271_i.func_178646_f();
            this.func_195624_a(var2, var3, var5, var6);
            this.field_73735_i = var4;
         }
      }
   }

   protected void func_195624_a(float var1, int var2, float var3, SpectatorDetails var4) {
      GlStateManager.func_179091_B();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var1);
      this.field_175268_g.func_110434_K().func_110577_a(field_175267_f);
      this.func_175174_a((float)(var2 - 91), var3, 0, 0, 182, 22);
      if (var4.func_178681_b() >= 0) {
         this.func_175174_a((float)(var2 - 91 - 1 + var4.func_178681_b() * 20), var3 - 1.0F, 0, 22, 24, 22);
      }

      RenderHelper.func_74520_c();

      for(int var5 = 0; var5 < 9; ++var5) {
         this.func_175266_a(var5, this.field_175268_g.field_195558_d.func_198107_o() / 2 - 90 + var5 * 20 + 2, var3 + 3.0F, var1, var4.func_178680_a(var5));
      }

      RenderHelper.func_74518_a();
      GlStateManager.func_179101_C();
      GlStateManager.func_179084_k();
   }

   private void func_175266_a(int var1, int var2, float var3, float var4, ISpectatorMenuObject var5) {
      this.field_175268_g.func_110434_K().func_110577_a(field_175269_a);
      if (var5 != SpectatorMenu.field_178657_a) {
         int var6 = (int)(var4 * 255.0F);
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)var2, var3, 0.0F);
         float var7 = var5.func_178662_A_() ? 1.0F : 0.25F;
         GlStateManager.func_179131_c(var7, var7, var7, var4);
         var5.func_178663_a(var7, var6);
         GlStateManager.func_179121_F();
         String var8 = String.valueOf(this.field_175268_g.field_71474_y.field_151456_ac[var1].func_197978_k());
         if (var6 > 3 && var5.func_178662_A_()) {
            this.field_175268_g.field_71466_p.func_175063_a(var8, (float)(var2 + 19 - 2 - this.field_175268_g.field_71466_p.func_78256_a(var8)), var3 + 6.0F + 3.0F, 16777215 + (var6 << 24));
         }
      }

   }

   public void func_195623_a() {
      int var1 = (int)(this.func_175265_c() * 255.0F);
      if (var1 > 3 && this.field_175271_i != null) {
         ISpectatorMenuObject var2 = this.field_175271_i.func_178645_b();
         String var3 = var2 == SpectatorMenu.field_178657_a ? this.field_175271_i.func_178650_c().func_178670_b().func_150254_d() : var2.func_178664_z_().func_150254_d();
         if (var3 != null) {
            int var4 = (this.field_175268_g.field_195558_d.func_198107_o() - this.field_175268_g.field_71466_p.func_78256_a(var3)) / 2;
            int var5 = this.field_175268_g.field_195558_d.func_198087_p() - 35;
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.field_175268_g.field_71466_p.func_175063_a(var3, (float)var4, (float)var5, 16777215 + (var1 << 24));
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }
      }

   }

   public void func_175257_a(SpectatorMenu var1) {
      this.field_175271_i = null;
      this.field_175270_h = 0L;
   }

   public boolean func_175262_a() {
      return this.field_175271_i != null;
   }

   public void func_195621_a(double var1) {
      int var3;
      for(var3 = this.field_175271_i.func_178648_e() + (int)var1; var3 >= 0 && var3 <= 8 && (this.field_175271_i.func_178643_a(var3) == SpectatorMenu.field_178657_a || !this.field_175271_i.func_178643_a(var3).func_178662_A_()); var3 = (int)((double)var3 + var1)) {
      }

      if (var3 >= 0 && var3 <= 8) {
         this.field_175271_i.func_178644_b(var3);
         this.field_175270_h = Util.func_211177_b();
      }

   }

   public void func_175261_b() {
      this.field_175270_h = Util.func_211177_b();
      if (this.func_175262_a()) {
         int var1 = this.field_175271_i.func_178648_e();
         if (var1 != -1) {
            this.field_175271_i.func_178644_b(var1);
         }
      } else {
         this.field_175271_i = new SpectatorMenu(this);
      }

   }
}
