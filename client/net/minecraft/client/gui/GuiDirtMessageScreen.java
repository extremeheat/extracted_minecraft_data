package net.minecraft.client.gui;

public class GuiDirtMessageScreen extends GuiScreen {
   private final String field_205029_a;

   public GuiDirtMessageScreen(String var1) {
      super();
      this.field_205029_a = var1;
   }

   public boolean func_195120_Y_() {
      return false;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146278_c(0);
      this.func_73732_a(this.field_146289_q, this.field_205029_a, this.field_146294_l / 2, 70, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
