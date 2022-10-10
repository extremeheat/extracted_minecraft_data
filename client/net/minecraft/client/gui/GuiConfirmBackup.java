package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class GuiConfirmBackup extends GuiScreen {
   private final GuiScreen field_212110_s;
   protected GuiConfirmBackup.ICallback field_212109_a;
   protected String field_212113_f;
   private final String field_212111_t;
   private final List<String> field_212112_u = Lists.newArrayList();
   protected String field_212114_g;
   protected String field_212115_h;
   protected String field_212116_i;

   public GuiConfirmBackup(GuiScreen var1, GuiConfirmBackup.ICallback var2, String var3, String var4) {
      super();
      this.field_212110_s = var1;
      this.field_212109_a = var2;
      this.field_212113_f = var3;
      this.field_212111_t = var4;
      this.field_212114_g = I18n.func_135052_a("selectWorld.backupJoinConfirmButton");
      this.field_212115_h = I18n.func_135052_a("selectWorld.backupJoinSkipButton");
      this.field_212116_i = I18n.func_135052_a("gui.cancel");
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_212112_u.clear();
      this.field_212112_u.addAll(this.field_146289_q.func_78271_c(this.field_212111_t, this.field_146294_l - 50));
      this.func_189646_b(new GuiOptionButton(0, this.field_146294_l / 2 - 155, 100 + (this.field_212112_u.size() + 1) * this.field_146289_q.field_78288_b, this.field_212114_g) {
         public void func_194829_a(double var1, double var3) {
            GuiConfirmBackup.this.field_212109_a.proceed(true);
         }
      });
      this.func_189646_b(new GuiOptionButton(1, this.field_146294_l / 2 - 155 + 160, 100 + (this.field_212112_u.size() + 1) * this.field_146289_q.field_78288_b, this.field_212115_h) {
         public void func_194829_a(double var1, double var3) {
            GuiConfirmBackup.this.field_212109_a.proceed(false);
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 155 + 80, 124 + (this.field_212112_u.size() + 1) * this.field_146289_q.field_78288_b, 150, 20, this.field_212116_i) {
         public void func_194829_a(double var1, double var3) {
            GuiConfirmBackup.this.field_146297_k.func_147108_a(GuiConfirmBackup.this.field_212110_s);
         }
      });
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_212113_f, this.field_146294_l / 2, 70, 16777215);
      int var4 = 90;

      for(Iterator var5 = this.field_212112_u.iterator(); var5.hasNext(); var4 += this.field_146289_q.field_78288_b) {
         String var6 = (String)var5.next();
         this.func_73732_a(this.field_146289_q, var6, this.field_146294_l / 2, var4, 16777215);
      }

      super.func_73863_a(var1, var2, var3);
   }

   public boolean func_195120_Y_() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.field_146297_k.func_147108_a(this.field_212110_s);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public interface ICallback {
      void proceed(boolean var1);
   }
}
