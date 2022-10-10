package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public class GuiDisconnected extends GuiScreen {
   private final String field_146306_a;
   private final ITextComponent field_146304_f;
   private List<String> field_146305_g;
   private final GuiScreen field_146307_h;
   private int field_175353_i;

   public GuiDisconnected(GuiScreen var1, String var2, ITextComponent var3) {
      super();
      this.field_146307_h = var1;
      this.field_146306_a = I18n.func_135052_a(var2);
      this.field_146304_f = var3;
   }

   public boolean func_195120_Y_() {
      return false;
   }

   protected void func_73866_w_() {
      this.field_146305_g = this.field_146289_q.func_78271_c(this.field_146304_f.func_150254_d(), this.field_146294_l - 50);
      this.field_175353_i = this.field_146305_g.size() * this.field_146289_q.field_78288_b;
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, Math.min(this.field_146295_m / 2 + this.field_175353_i / 2 + this.field_146289_q.field_78288_b, this.field_146295_m - 30), I18n.func_135052_a("gui.toMenu")) {
         public void func_194829_a(double var1, double var3) {
            GuiDisconnected.this.field_146297_k.func_147108_a(GuiDisconnected.this.field_146307_h);
         }
      });
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146306_a, this.field_146294_l / 2, this.field_146295_m / 2 - this.field_175353_i / 2 - this.field_146289_q.field_78288_b * 2, 11184810);
      int var4 = this.field_146295_m / 2 - this.field_175353_i / 2;
      if (this.field_146305_g != null) {
         for(Iterator var5 = this.field_146305_g.iterator(); var5.hasNext(); var4 += this.field_146289_q.field_78288_b) {
            String var6 = (String)var5.next();
            this.func_73732_a(this.field_146289_q, var6, this.field_146294_l / 2, var4, 16777215);
         }
      }

      super.func_73863_a(var1, var2, var3);
   }
}
