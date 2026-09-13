package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings$Options;

public class GuiSnooper extends GuiScreen {
   private final GuiScreen field_146608_a;
   private final GameSettings field_146603_f;
   private final List field_146604_g = new ArrayList();
   private final List field_146609_h = new ArrayList();
   private String field_146610_i;
   private String[] field_146607_r;
   private GuiSnooper$List field_146606_s;
   private GuiButton field_146605_t;

   public GuiSnooper(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146608_a = var1;
      this.field_146603_f = var2;
   }

   @Override
   public void func_73866_w_() {
      this.field_146610_i = I18n.func_135052_a("options.snooper.title");
      String var1 = I18n.func_135052_a("options.snooper.desc");
      ArrayList var2 = new ArrayList();

      for(String var4 : this.field_146289_q.func_78271_c(var1, this.field_146294_l - 30)) {
         var2.add(var4);
      }

      this.field_146607_r = var2.toArray(new String[0]);
      this.field_146604_g.clear();
      this.field_146609_h.clear();
      this.field_146292_n
         .add(
            this.field_146605_t = new GuiButton(
               1, this.field_146294_l / 2 - 152, this.field_146295_m - 30, 150, 20, this.field_146603_f.func_74297_c(GameSettings$Options.SNOOPER_ENABLED)
            )
         );
      this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 + 2, this.field_146295_m - 30, 150, 20, I18n.func_135052_a("gui.done")));
      boolean var6 = this.field_146297_k.func_71401_C() != null && this.field_146297_k.func_71401_C().func_80003_ah() != null;

      for(Entry var5 : new TreeMap(this.field_146297_k.func_71378_E().func_76465_c()).entrySet()) {
         this.field_146604_g.add((var6 ? "C " : "") + (String)var5.getKey());
         this.field_146609_h.add(this.field_146289_q.func_78269_a((String)var5.getValue(), this.field_146294_l - 220));
      }

      if (var6) {
         for(Entry var9 : new TreeMap(this.field_146297_k.func_71401_C().func_80003_ah().func_76465_c()).entrySet()) {
            this.field_146604_g.add("S " + (String)var9.getKey());
            this.field_146609_h.add(this.field_146289_q.func_78269_a((String)var9.getValue(), this.field_146294_l - 220));
         }
      }

      this.field_146606_s = new GuiSnooper$List(this);
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 2) {
            this.field_146603_f.func_74303_b();
            this.field_146603_f.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_146608_a);
         }

         if (var1.field_146127_k == 1) {
            this.field_146603_f.func_74306_a(GameSettings$Options.SNOOPER_ENABLED, 1);
            this.field_146605_t.field_146126_j = this.field_146603_f.func_74297_c(GameSettings$Options.SNOOPER_ENABLED);
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146606_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146610_i, this.field_146294_l / 2, 8, 16777215);
      int var4 = 22;

      for(String var8 : this.field_146607_r) {
         this.func_73732_a(this.field_146289_q, var8, this.field_146294_l / 2, var4, 8421504);
         var4 += this.field_146289_q.field_78288_b;
      }

      super.func_73863_a(var1, var2, var3);
   }
}
