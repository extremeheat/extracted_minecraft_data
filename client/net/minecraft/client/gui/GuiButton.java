package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButton extends Gui {
   protected static final ResourceLocation field_146122_a = new ResourceLocation("textures/gui/widgets.png");
   protected int field_146120_f;
   protected int field_146121_g;
   public int field_146128_h;
   public int field_146129_i;
   public String field_146126_j;
   public int field_146127_k;
   public boolean field_146124_l;
   public boolean field_146125_m;
   protected boolean field_146123_n;

   public GuiButton(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, 200, 20, var4);
   }

   public GuiButton(int var1, int var2, int var3, int var4, int var5, String var6) {
      super();
      this.field_146120_f = 200;
      this.field_146121_g = 20;
      this.field_146124_l = true;
      this.field_146125_m = true;
      this.field_146127_k = var1;
      this.field_146128_h = var2;
      this.field_146129_i = var3;
      this.field_146120_f = var4;
      this.field_146121_g = var5;
      this.field_146126_j = var6;
   }

   protected int func_146114_a(boolean var1) {
      byte var2 = 1;
      if (!this.field_146124_l) {
         var2 = 0;
      } else if (var1) {
         var2 = 2;
      }

      return var2;
   }

   public void func_146112_a(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         FontRenderer var4 = var1.field_71466_p;
         var1.func_110434_K().func_110577_a(field_146122_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_146123_n = var2 >= this.field_146128_h && var3 >= this.field_146129_i && var2 < this.field_146128_h + this.field_146120_f && var3 < this.field_146129_i + this.field_146121_g;
         int var5 = this.func_146114_a(this.field_146123_n);
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179112_b(770, 771);
         this.func_73729_b(this.field_146128_h, this.field_146129_i, 0, 46 + var5 * 20, this.field_146120_f / 2, this.field_146121_g);
         this.func_73729_b(this.field_146128_h + this.field_146120_f / 2, this.field_146129_i, 200 - this.field_146120_f / 2, 46 + var5 * 20, this.field_146120_f / 2, this.field_146121_g);
         this.func_146119_b(var1, var2, var3);
         int var6 = 14737632;
         if (!this.field_146124_l) {
            var6 = 10526880;
         } else if (this.field_146123_n) {
            var6 = 16777120;
         }

         this.func_73732_a(var4, this.field_146126_j, this.field_146128_h + this.field_146120_f / 2, this.field_146129_i + (this.field_146121_g - 8) / 2, var6);
      }
   }

   protected void func_146119_b(Minecraft var1, int var2, int var3) {
   }

   public void func_146118_a(int var1, int var2) {
   }

   public boolean func_146116_c(Minecraft var1, int var2, int var3) {
      return this.field_146124_l && this.field_146125_m && var2 >= this.field_146128_h && var3 >= this.field_146129_i && var2 < this.field_146128_h + this.field_146120_f && var3 < this.field_146129_i + this.field_146121_g;
   }

   public boolean func_146115_a() {
      return this.field_146123_n;
   }

   public void func_146111_b(int var1, int var2) {
   }

   public void func_146113_a(SoundHandler var1) {
      var1.func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
   }

   public int func_146117_b() {
      return this.field_146120_f;
   }

   public void func_175211_a(int var1) {
      this.field_146120_f = var1;
   }
}
