package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;

public class GuiOptionSlider extends GuiButton {
   private float field_146134_p;
   public boolean field_146135_o;
   private GameSettings.Options field_146133_q;
   private final float field_146132_r;
   private final float field_146131_s;

   public GuiOptionSlider(int var1, int var2, int var3, GameSettings.Options var4) {
      this(var1, var2, var3, var4, 0.0F, 1.0F);
   }

   public GuiOptionSlider(int var1, int var2, int var3, GameSettings.Options var4, float var5, float var6) {
      super(var1, var2, var3, 150, 20, "");
      this.field_146134_p = 1.0F;
      this.field_146133_q = var4;
      this.field_146132_r = var5;
      this.field_146131_s = var6;
      Minecraft var7 = Minecraft.func_71410_x();
      this.field_146134_p = var4.func_148266_c(var7.field_71474_y.func_74296_a(var4));
      this.field_146126_j = var7.field_71474_y.func_74297_c(var4);
   }

   protected int func_146114_a(boolean var1) {
      return 0;
   }

   protected void func_146119_b(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         if (this.field_146135_o) {
            this.field_146134_p = (float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);
            this.field_146134_p = MathHelper.func_76131_a(this.field_146134_p, 0.0F, 1.0F);
            float var4 = this.field_146133_q.func_148262_d(this.field_146134_p);
            var1.field_71474_y.func_74304_a(this.field_146133_q, var4);
            this.field_146134_p = this.field_146133_q.func_148266_c(var4);
            this.field_146126_j = var1.field_71474_y.func_74297_c(this.field_146133_q);
         }

         var1.func_110434_K().func_110577_a(field_146122_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_73729_b(this.field_146128_h + (int)(this.field_146134_p * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
         this.func_73729_b(this.field_146128_h + (int)(this.field_146134_p * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
      }
   }

   public boolean func_146116_c(Minecraft var1, int var2, int var3) {
      if (super.func_146116_c(var1, var2, var3)) {
         this.field_146134_p = (float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);
         this.field_146134_p = MathHelper.func_76131_a(this.field_146134_p, 0.0F, 1.0F);
         var1.field_71474_y.func_74304_a(this.field_146133_q, this.field_146133_q.func_148262_d(this.field_146134_p));
         this.field_146126_j = var1.field_71474_y.func_74297_c(this.field_146133_q);
         this.field_146135_o = true;
         return true;
      } else {
         return false;
      }
   }

   public void func_146118_a(int var1, int var2) {
      this.field_146135_o = false;
   }
}
