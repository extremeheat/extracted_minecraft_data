package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class GuiScreenDemo extends GuiScreen {
   private static final ResourceLocation field_146348_f = new ResourceLocation("textures/gui/demo_background.png");

   public GuiScreenDemo() {
      super();
   }

   protected void func_73866_w_() {
      boolean var1 = true;
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 116, this.field_146295_m / 2 + 62 + -16, 114, 20, I18n.func_135052_a("demo.help.buy")) {
         public void func_194829_a(double var1, double var3) {
            this.field_146124_l = false;
            Util.func_110647_a().func_195640_a("http://www.minecraft.net/store?source=demo");
         }
      });
      this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 + 2, this.field_146295_m / 2 + 62 + -16, 114, 20, I18n.func_135052_a("demo.help.later")) {
         public void func_194829_a(double var1, double var3) {
            GuiScreenDemo.this.field_146297_k.func_147108_a((GuiScreen)null);
            GuiScreenDemo.this.field_146297_k.field_71417_B.func_198034_i();
         }
      });
   }

   public void func_146276_q_() {
      super.func_146276_q_();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146348_f);
      int var1 = (this.field_146294_l - 248) / 2;
      int var2 = (this.field_146295_m - 166) / 2;
      this.func_73729_b(var1, var2, 0, 0, 248, 166);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      int var4 = (this.field_146294_l - 248) / 2 + 10;
      int var5 = (this.field_146295_m - 166) / 2 + 8;
      this.field_146289_q.func_211126_b(I18n.func_135052_a("demo.help.title"), (float)var4, (float)var5, 2039583);
      var5 += 12;
      GameSettings var6 = this.field_146297_k.field_71474_y;
      this.field_146289_q.func_211126_b(I18n.func_135052_a("demo.help.movementShort", var6.field_74351_w.func_197978_k(), var6.field_74370_x.func_197978_k(), var6.field_74368_y.func_197978_k(), var6.field_74366_z.func_197978_k()), (float)var4, (float)var5, 5197647);
      this.field_146289_q.func_211126_b(I18n.func_135052_a("demo.help.movementMouse"), (float)var4, (float)(var5 + 12), 5197647);
      this.field_146289_q.func_211126_b(I18n.func_135052_a("demo.help.jump", var6.field_74314_A.func_197978_k()), (float)var4, (float)(var5 + 24), 5197647);
      this.field_146289_q.func_211126_b(I18n.func_135052_a("demo.help.inventory", var6.field_151445_Q.func_197978_k()), (float)var4, (float)(var5 + 36), 5197647);
      this.field_146289_q.func_78279_b(I18n.func_135052_a("demo.help.fullWrapped"), var4, var5 + 68, 218, 2039583);
      super.func_73863_a(var1, var2, var3);
   }
}
