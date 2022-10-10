package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class GuiOptionSlider extends GuiButton {
   private double field_146134_p;
   public boolean field_146135_o;
   private final GameSettings.Options field_146133_q;
   private final double field_146132_r;
   private final double field_146131_s;

   public GuiOptionSlider(int var1, int var2, int var3, GameSettings.Options var4) {
      this(var1, var2, var3, var4, 0.0D, 1.0D);
   }

   public GuiOptionSlider(int var1, int var2, int var3, GameSettings.Options var4, double var5, double var7) {
      this(var1, var2, var3, 150, 20, var4, var5, var7);
   }

   public GuiOptionSlider(int var1, int var2, int var3, int var4, int var5, GameSettings.Options var6, double var7, double var9) {
      super(var1, var2, var3, var4, var5, "");
      this.field_146134_p = 1.0D;
      this.field_146133_q = var6;
      this.field_146132_r = var7;
      this.field_146131_s = var9;
      Minecraft var11 = Minecraft.func_71410_x();
      this.field_146134_p = var6.func_198008_a(var11.field_71474_y.func_198015_a(var6));
      this.field_146126_j = var11.field_71474_y.func_74297_c(var6);
   }

   protected int func_146114_a(boolean var1) {
      return 0;
   }

   protected void func_146119_b(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         if (this.field_146135_o) {
            this.field_146134_p = (double)((float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8));
            this.field_146134_p = MathHelper.func_151237_a(this.field_146134_p, 0.0D, 1.0D);
         }

         if (this.field_146135_o || this.field_146133_q == GameSettings.Options.FULLSCREEN_RESOLUTION) {
            double var4 = this.field_146133_q.func_198004_b(this.field_146134_p);
            var1.field_71474_y.func_198016_a(this.field_146133_q, var4);
            this.field_146134_p = this.field_146133_q.func_198008_a(var4);
            this.field_146126_j = var1.field_71474_y.func_74297_c(this.field_146133_q);
         }

         var1.func_110434_K().func_110577_a(field_146122_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_73729_b(this.field_146128_h + (int)(this.field_146134_p * (double)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
         this.func_73729_b(this.field_146128_h + (int)(this.field_146134_p * (double)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
      }
   }

   public final void func_194829_a(double var1, double var3) {
      this.field_146134_p = (var1 - (double)(this.field_146128_h + 4)) / (double)(this.field_146120_f - 8);
      this.field_146134_p = MathHelper.func_151237_a(this.field_146134_p, 0.0D, 1.0D);
      Minecraft var5 = Minecraft.func_71410_x();
      var5.field_71474_y.func_198016_a(this.field_146133_q, this.field_146133_q.func_198004_b(this.field_146134_p));
      this.field_146126_j = var5.field_71474_y.func_74297_c(this.field_146133_q);
      this.field_146135_o = true;
   }

   public void func_194831_b(double var1, double var3) {
      this.field_146135_o = false;
   }
}
