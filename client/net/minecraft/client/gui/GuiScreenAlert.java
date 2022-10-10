package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public class GuiScreenAlert extends GuiScreen {
   private final Runnable field_201552_h;
   protected final ITextComponent field_201548_a;
   protected final ITextComponent field_201550_f;
   private final List<String> field_201553_i;
   protected String field_201551_g;
   private int field_201549_s;

   public GuiScreenAlert(Runnable var1, ITextComponent var2, ITextComponent var3) {
      this(var1, var2, var3, "gui.back");
   }

   public GuiScreenAlert(Runnable var1, ITextComponent var2, ITextComponent var3, String var4) {
      super();
      this.field_201553_i = Lists.newArrayList();
      this.field_201552_h = var1;
      this.field_201548_a = var2;
      this.field_201550_f = var3;
      this.field_201551_g = I18n.func_135052_a(var4);
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, this.field_201551_g) {
         public void func_194829_a(double var1, double var3) {
            GuiScreenAlert.this.field_201552_h.run();
         }
      });
      this.field_201553_i.clear();
      this.field_201553_i.addAll(this.field_146289_q.func_78271_c(this.field_201550_f.func_150254_d(), this.field_146294_l - 50));
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_201548_a.func_150254_d(), this.field_146294_l / 2, 70, 16777215);
      int var4 = 90;

      for(Iterator var5 = this.field_201553_i.iterator(); var5.hasNext(); var4 += this.field_146289_q.field_78288_b) {
         String var6 = (String)var5.next();
         this.func_73732_a(this.field_146289_q, var6, this.field_146294_l / 2, var4, 16777215);
      }

      super.func_73863_a(var1, var2, var3);
   }

   public void func_73876_c() {
      super.func_73876_c();
      GuiButton var2;
      if (--this.field_201549_s == 0) {
         for(Iterator var1 = this.field_146292_n.iterator(); var1.hasNext(); var2.field_146124_l = true) {
            var2 = (GuiButton)var1.next();
         }
      }

   }
}
