package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen {
   private final String field_146313_a;
   private final String field_146312_f;

   public GuiErrorScreen(String var1, String var2) {
      super();
      this.field_146313_a = var1;
      this.field_146312_f = var2;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, 140, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiErrorScreen.this.field_146297_k.func_147108_a((GuiScreen)null);
         }
      });
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, -12574688, -11530224);
      this.func_73732_a(this.field_146289_q, this.field_146313_a, this.field_146294_l / 2, 90, 16777215);
      this.func_73732_a(this.field_146289_q, this.field_146312_f, this.field_146294_l / 2, 110, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   public boolean func_195120_Y_() {
      return false;
   }
}
