package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiConfirmOpenLink extends GuiYesNo {
   private final String field_146363_r;
   private final String field_146362_s;
   private final String field_146361_t;
   private boolean field_146360_u = true;

   public GuiConfirmOpenLink(GuiYesNoCallback var1, String var2, int var3, boolean var4) {
      super(var1, I18n.func_135052_a(var4 ? "chat.link.confirmTrusted" : "chat.link.confirm"), var2, var3);
      this.field_146352_g = I18n.func_135052_a(var4 ? "chat.link.open" : "gui.yes");
      this.field_146356_h = I18n.func_135052_a(var4 ? "gui.cancel" : "gui.no");
      this.field_146362_s = I18n.func_135052_a("chat.copy");
      this.field_146363_r = I18n.func_135052_a("chat.link.warning");
      this.field_146361_t = var2;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_146292_n.clear();
      this.field_195124_j.clear();
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 50 - 105, this.field_146295_m / 6 + 96, 100, 20, this.field_146352_g) {
         public void func_194829_a(double var1, double var3) {
            GuiConfirmOpenLink.this.field_146355_a.confirmResult(true, GuiConfirmOpenLink.this.field_146357_i);
         }
      });
      this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 - 50, this.field_146295_m / 6 + 96, 100, 20, this.field_146362_s) {
         public void func_194829_a(double var1, double var3) {
            GuiConfirmOpenLink.this.func_146359_e();
            GuiConfirmOpenLink.this.field_146355_a.confirmResult(false, GuiConfirmOpenLink.this.field_146357_i);
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 50 + 105, this.field_146295_m / 6 + 96, 100, 20, this.field_146356_h) {
         public void func_194829_a(double var1, double var3) {
            GuiConfirmOpenLink.this.field_146355_a.confirmResult(false, GuiConfirmOpenLink.this.field_146357_i);
         }
      });
   }

   public void func_146359_e() {
      this.field_146297_k.field_195559_v.func_197960_a(this.field_146361_t);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      super.func_73863_a(var1, var2, var3);
      if (this.field_146360_u) {
         this.func_73732_a(this.field_146289_q, this.field_146363_r, this.field_146294_l / 2, 110, 16764108);
      }

   }

   public void func_146358_g() {
      this.field_146360_u = false;
   }
}
