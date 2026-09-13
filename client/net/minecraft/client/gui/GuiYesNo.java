package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiYesNo extends GuiScreen {
   protected GuiYesNoCallback field_146355_a;
   protected String field_146351_f;
   private String field_146354_r;
   protected String field_146352_g;
   protected String field_146356_h;
   protected int field_146357_i;
   private int field_146353_s;

   public GuiYesNo(GuiYesNoCallback var1, String var2, String var3, int var4) {
      super();
      this.field_146355_a = var1;
      this.field_146351_f = var2;
      this.field_146354_r = var3;
      this.field_146357_i = var4;
      this.field_146352_g = I18n.func_135052_a("gui.yes");
      this.field_146356_h = I18n.func_135052_a("gui.no");
   }

   public GuiYesNo(GuiYesNoCallback var1, String var2, String var3, String var4, String var5, int var6) {
      super();
      this.field_146355_a = var1;
      this.field_146351_f = var2;
      this.field_146354_r = var3;
      this.field_146352_g = var4;
      this.field_146356_h = var5;
      this.field_146357_i = var6;
   }

   @Override
   public void func_73866_w_() {
      this.field_146292_n.add(new GuiOptionButton(0, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 96, this.field_146352_g));
      this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 - 155 + 160, this.field_146295_m / 6 + 96, this.field_146356_h));
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      this.field_146355_a.func_73878_a(var1.field_146127_k == 0, this.field_146357_i);
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146351_f, this.field_146294_l / 2, 70, 16777215);
      this.func_73732_a(this.field_146289_q, this.field_146354_r, this.field_146294_l / 2, 90, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   public void func_146350_a(int var1) {
      this.field_146353_s = var1;

      for(GuiButton var3 : this.field_146292_n) {
         var3.field_146124_l = false;
      }
   }

   @Override
   public void func_73876_c() {
      super.func_73876_c();
      if (--this.field_146353_s == 0) {
         for(GuiButton var2 : this.field_146292_n) {
            var2.field_146124_l = true;
         }
      }
   }
}
