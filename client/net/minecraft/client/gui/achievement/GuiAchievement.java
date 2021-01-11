package net.minecraft.client.gui.achievement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

public class GuiAchievement extends Gui {
   private static final ResourceLocation field_146261_a = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   private Minecraft field_146259_f;
   private int field_146260_g;
   private int field_146267_h;
   private String field_146268_i;
   private String field_146265_j;
   private Achievement field_146266_k;
   private long field_146263_l;
   private RenderItem field_146264_m;
   private boolean field_146262_n;

   public GuiAchievement(Minecraft var1) {
      super();
      this.field_146259_f = var1;
      this.field_146264_m = var1.func_175599_af();
   }

   public void func_146256_a(Achievement var1) {
      this.field_146268_i = I18n.func_135052_a("achievement.get");
      this.field_146265_j = var1.func_150951_e().func_150260_c();
      this.field_146263_l = Minecraft.func_71386_F();
      this.field_146266_k = var1;
      this.field_146262_n = false;
   }

   public void func_146255_b(Achievement var1) {
      this.field_146268_i = var1.func_150951_e().func_150260_c();
      this.field_146265_j = var1.func_75989_e();
      this.field_146263_l = Minecraft.func_71386_F() + 2500L;
      this.field_146266_k = var1;
      this.field_146262_n = true;
   }

   private void func_146258_c() {
      GlStateManager.func_179083_b(0, 0, this.field_146259_f.field_71443_c, this.field_146259_f.field_71440_d);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      this.field_146260_g = this.field_146259_f.field_71443_c;
      this.field_146267_h = this.field_146259_f.field_71440_d;
      ScaledResolution var1 = new ScaledResolution(this.field_146259_f);
      this.field_146260_g = var1.func_78326_a();
      this.field_146267_h = var1.func_78328_b();
      GlStateManager.func_179086_m(256);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179130_a(0.0D, (double)this.field_146260_g, (double)this.field_146267_h, 0.0D, 1000.0D, 3000.0D);
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
   }

   public void func_146254_a() {
      if (this.field_146266_k != null && this.field_146263_l != 0L && Minecraft.func_71410_x().field_71439_g != null) {
         double var1 = (double)(Minecraft.func_71386_F() - this.field_146263_l) / 3000.0D;
         if (!this.field_146262_n) {
            if (var1 < 0.0D || var1 > 1.0D) {
               this.field_146263_l = 0L;
               return;
            }
         } else if (var1 > 0.5D) {
            var1 = 0.5D;
         }

         this.func_146258_c();
         GlStateManager.func_179097_i();
         GlStateManager.func_179132_a(false);
         double var3 = var1 * 2.0D;
         if (var3 > 1.0D) {
            var3 = 2.0D - var3;
         }

         var3 *= 4.0D;
         var3 = 1.0D - var3;
         if (var3 < 0.0D) {
            var3 = 0.0D;
         }

         var3 *= var3;
         var3 *= var3;
         int var5 = this.field_146260_g - 160;
         int var6 = 0 - (int)(var3 * 36.0D);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179098_w();
         this.field_146259_f.func_110434_K().func_110577_a(field_146261_a);
         GlStateManager.func_179140_f();
         this.func_73729_b(var5, var6, 96, 202, 160, 32);
         if (this.field_146262_n) {
            this.field_146259_f.field_71466_p.func_78279_b(this.field_146265_j, var5 + 30, var6 + 7, 120, -1);
         } else {
            this.field_146259_f.field_71466_p.func_78276_b(this.field_146268_i, var5 + 30, var6 + 7, -256);
            this.field_146259_f.field_71466_p.func_78276_b(this.field_146265_j, var5 + 30, var6 + 18, -1);
         }

         RenderHelper.func_74520_c();
         GlStateManager.func_179140_f();
         GlStateManager.func_179091_B();
         GlStateManager.func_179142_g();
         GlStateManager.func_179145_e();
         this.field_146264_m.func_180450_b(this.field_146266_k.field_75990_d, var5 + 8, var6 + 8);
         GlStateManager.func_179140_f();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179126_j();
      }
   }

   public void func_146257_b() {
      this.field_146266_k = null;
      this.field_146263_l = 0L;
   }
}
