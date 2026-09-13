package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

class GuiBeacon$Button extends GuiButton {
   private final ResourceLocation field_146145_o;
   private final int field_146144_p;
   private final int field_146143_q;
   private boolean field_146142_r;

   protected GuiBeacon$Button(int var1, int var2, int var3, ResourceLocation var4, int var5, int var6) {
      super(var1, var2, var3, 22, 22, "");
      this.field_146145_o = var4;
      this.field_146144_p = var5;
      this.field_146143_q = var6;
   }

   @Override
   public void func_146112_a(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         var1.func_110434_K().func_110577_a(GuiBeacon.access$000());
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_146123_n = var2 >= this.field_146128_h
            && var3 >= this.field_146129_i
            && var2 < this.field_146128_h + this.field_146120_f
            && var3 < this.field_146129_i + this.field_146121_g;
         short var4 = 219;
         int var5 = 0;
         if (!this.field_146124_l) {
            var5 += this.field_146120_f * 2;
         } else if (this.field_146142_r) {
            var5 += this.field_146120_f * 1;
         } else if (this.field_146123_n) {
            var5 += this.field_146120_f * 3;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, var4, this.field_146120_f, this.field_146121_g);
         if (!GuiBeacon.access$000().equals(this.field_146145_o)) {
            var1.func_110434_K().func_110577_a(this.field_146145_o);
         }

         this.func_73729_b(this.field_146128_h + 2, this.field_146129_i + 2, this.field_146144_p, this.field_146143_q, 18, 18);
      }
   }

   public boolean func_146141_c() {
      return this.field_146142_r;
   }

   public void func_146140_b(boolean var1) {
      this.field_146142_r = var1;
   }
}
