package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonToggle extends GuiButton {
   protected ResourceLocation field_191760_o;
   protected boolean field_191755_p;
   protected int field_191756_q;
   protected int field_191757_r;
   protected int field_191758_s;
   protected int field_191759_t;

   public GuiButtonToggle(int var1, int var2, int var3, int var4, int var5, boolean var6) {
      super(var1, var2, var3, var4, var5, "");
      this.field_191755_p = var6;
   }

   public void func_191751_a(int var1, int var2, int var3, int var4, ResourceLocation var5) {
      this.field_191756_q = var1;
      this.field_191757_r = var2;
      this.field_191758_s = var3;
      this.field_191759_t = var4;
      this.field_191760_o = var5;
   }

   public void func_191753_b(boolean var1) {
      this.field_191755_p = var1;
   }

   public boolean func_191754_c() {
      return this.field_191755_p;
   }

   public void func_191752_c(int var1, int var2) {
      this.field_146128_h = var1;
      this.field_146129_i = var2;
   }

   public void func_194828_a(int var1, int var2, float var3) {
      if (this.field_146125_m) {
         this.field_146123_n = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
         Minecraft var4 = Minecraft.func_71410_x();
         var4.func_110434_K().func_110577_a(this.field_191760_o);
         GlStateManager.func_179097_i();
         int var5 = this.field_191756_q;
         int var6 = this.field_191757_r;
         if (this.field_191755_p) {
            var5 += this.field_191758_s;
         }

         if (this.field_146123_n) {
            var6 += this.field_191759_t;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, var6, this.field_146120_f, this.field_146121_g);
         GlStateManager.func_179126_j();
      }
   }
}
