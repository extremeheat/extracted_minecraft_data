package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

class GuiScreenBook$NextPageButton extends GuiButton {
   private final boolean field_146151_o;

   public GuiScreenBook$NextPageButton(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, 23, 13, "");
      this.field_146151_o = var4;
   }

   @Override
   public void func_146112_a(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         boolean var4 = var2 >= this.field_146128_h
            && var3 >= this.field_146129_i
            && var2 < this.field_146128_h + this.field_146120_f
            && var3 < this.field_146129_i + this.field_146121_g;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         var1.func_110434_K().func_110577_a(GuiScreenBook.access$000());
         int var5 = 0;
         int var6 = 192;
         if (var4) {
            var5 += 23;
         }

         if (!this.field_146151_o) {
            var6 += 13;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, var6, 23, 13);
      }
   }
}
