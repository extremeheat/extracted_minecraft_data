package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonLanguage extends GuiButton {
   public GuiButtonLanguage(int var1, int var2, int var3) {
      super(var1, var2, var3, 20, 20, "");
   }

   public void func_146112_a(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         var1.func_110434_K().func_110577_a(GuiButton.field_146122_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = var2 >= this.field_146128_h && var3 >= this.field_146129_i && var2 < this.field_146128_h + this.field_146120_f && var3 < this.field_146129_i + this.field_146121_g;
         int var5 = 106;
         if (var4) {
            var5 += this.field_146121_g;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, 0, var5, this.field_146120_f, this.field_146121_g);
      }
   }
}
