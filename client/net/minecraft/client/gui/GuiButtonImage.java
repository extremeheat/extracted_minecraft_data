package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class GuiButtonImage extends GuiButton {
   private final ResourceLocation field_191750_o;
   private final int field_191747_p;
   private final int field_191748_q;
   private final int field_191749_r;

   public GuiButtonImage(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, ResourceLocation var9) {
      super(var1, var2, var3, var4, var5, "");
      this.field_191747_p = var6;
      this.field_191748_q = var7;
      this.field_191749_r = var8;
      this.field_191750_o = var9;
   }

   public void func_191746_c(int var1, int var2) {
      this.field_146128_h = var1;
      this.field_146129_i = var2;
   }

   public void func_194828_a(int var1, int var2, float var3) {
      if (this.field_146125_m) {
         this.field_146123_n = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
         Minecraft var4 = Minecraft.func_71410_x();
         var4.func_110434_K().func_110577_a(this.field_191750_o);
         GlStateManager.func_179097_i();
         int var5 = this.field_191748_q;
         if (this.field_146123_n) {
            var5 += this.field_191749_r;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, this.field_191747_p, var5, this.field_146120_f, this.field_146121_g);
         GlStateManager.func_179126_j();
      }
   }
}
