package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

class GuiMerchant$MerchantButton extends GuiButton {
   private final boolean field_146157_o;

   public GuiMerchant$MerchantButton(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, 12, 19, "");
      this.field_146157_o = var4;
   }

   @Override
   public void func_146112_a(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         var1.func_110434_K().func_110577_a(GuiMerchant.access$000());
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = var2 >= this.field_146128_h
            && var3 >= this.field_146129_i
            && var2 < this.field_146128_h + this.field_146120_f
            && var3 < this.field_146129_i + this.field_146121_g;
         int var5 = 0;
         int var6 = 176;
         if (!this.field_146124_l) {
            var6 += this.field_146120_f * 2;
         } else if (var4) {
            var6 += this.field_146120_f;
         }

         if (!this.field_146157_o) {
            var5 += this.field_146121_g;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var6, var5, this.field_146120_f, this.field_146121_g);
      }
   }
}
