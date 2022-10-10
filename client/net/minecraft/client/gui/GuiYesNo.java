package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class GuiYesNo extends GuiScreen {
   protected GuiYesNoCallback field_146355_a;
   protected String field_146351_f;
   private final String field_146354_r;
   private final List<String> field_175298_s = Lists.newArrayList();
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

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.func_189646_b(new GuiOptionButton(0, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 96, this.field_146352_g) {
         public void func_194829_a(double var1, double var3) {
            GuiYesNo.this.field_146355_a.confirmResult(true, GuiYesNo.this.field_146357_i);
         }
      });
      this.func_189646_b(new GuiOptionButton(1, this.field_146294_l / 2 - 155 + 160, this.field_146295_m / 6 + 96, this.field_146356_h) {
         public void func_194829_a(double var1, double var3) {
            GuiYesNo.this.field_146355_a.confirmResult(false, GuiYesNo.this.field_146357_i);
         }
      });
      this.field_175298_s.clear();
      this.field_175298_s.addAll(this.field_146289_q.func_78271_c(this.field_146354_r, this.field_146294_l - 50));
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146351_f, this.field_146294_l / 2, 70, 16777215);
      int var4 = 90;

      for(Iterator var5 = this.field_175298_s.iterator(); var5.hasNext(); var4 += this.field_146289_q.field_78288_b) {
         String var6 = (String)var5.next();
         this.func_73732_a(this.field_146289_q, var6, this.field_146294_l / 2, var4, 16777215);
      }

      super.func_73863_a(var1, var2, var3);
   }

   public void func_146350_a(int var1) {
      this.field_146353_s = var1;

      GuiButton var3;
      for(Iterator var2 = this.field_146292_n.iterator(); var2.hasNext(); var3.field_146124_l = false) {
         var3 = (GuiButton)var2.next();
      }

   }

   public void func_73876_c() {
      super.func_73876_c();
      GuiButton var2;
      if (--this.field_146353_s == 0) {
         for(Iterator var1 = this.field_146292_n.iterator(); var1.hasNext(); var2.field_146124_l = true) {
            var2 = (GuiButton)var1.next();
         }
      }

   }

   public boolean func_195120_Y_() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.field_146355_a.confirmResult(false, this.field_146357_i);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }
}
